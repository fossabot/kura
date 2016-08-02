package org.eclipse.kura.protocol.can.arrowhead;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.protocol.can.arrowhead.control.ControlMessage;
import org.eclipse.kura.protocol.can.arrowhead.control.ControlMessageCodes;
import org.eclipse.kura.protocol.can.arrowhead.control.T312RechargeRequestMessage;
import org.eclipse.kura.protocol.can.arrowhead.rest.ArrowheadRestClient;
import org.eclipse.kura.protocol.can.arrowhead.rest.ArrowheadRestClient.ArrowheadRestResponseListener;
import org.eclipse.kura.protocol.can.arrowhead.rest.ArrowheadRestClient.EVSEGetStatusResponse;
import org.eclipse.kura.protocol.can.arrowhead.rest.ArrowheadRestClient.EVSEStatusResponse;
import org.eclipse.kura.protocol.can.cs.data.MotoTronDataSnapshot;
import org.eclipse.kura.protocol.can.cs.data.PrivateCSDataSnapshot;
import org.eclipse.kura.protocol.can.cs.data.PublicCSDataSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class T312ApplicationLogic implements ArrowheadCanSocketImpl.ApplicationLogic {

	private static final String BASE_URI = "http://mml.arces.unibo.it:10750"; // TODO
																				// make
																				// base
																				// uri
																				// configurable
	private static final long BOOKING_SERVICE_POLL_RATE_MS = 2000; // TODO make
																	// booking
																	// service
																	// poll rate
																	// configurable

	private ArrowheadCanSocketImpl impl;
	private ArrowheadRestClient restClient = new ArrowheadRestClient(BASE_URI);
	private Logger logger = LoggerFactory.getLogger(T312ApplicationLogic.class);

	enum RechargeState {
		IDLE, RECHARGE_STARTING, RECHARGE_IN_PROGRESS, RECHARGE_STOPPING
	}

	private RechargeState rechargeState;

	enum ReservationState {
		UNKNOWN, RESERVED, NOT_RESERVED
	};

	private ReservationState reservationState = ReservationState.UNKNOWN;

	private Thread bookingServicePollThread;

	// TODO implement timers for resetting the CS if it takes too long for it to
	// change state
	// TODO handle failures of REST requests to notify recharge start/stop

	public T312ApplicationLogic(ArrowheadCanSocketImpl impl) throws KuraException {
		this.impl = impl;
		rechargeState = RechargeState.IDLE;
		this.bookingServicePollThread = new BookingServicePollThread();
		this.bookingServicePollThread.start();
	}
	
	private void setRechargeState(RechargeState newState) {
		logger.info("Status change: " + rechargeState + " -> " + newState);
		this.rechargeState = newState;
	}
	
	private void requestStateChange(RechargeState targetStatus) {
		switch (rechargeState) {
		case IDLE:
			if (targetStatus == RechargeState.RECHARGE_STARTING || targetStatus == RechargeState.RECHARGE_IN_PROGRESS) {
				impl.setStartRechargeFlag(1);
				setRechargeState(RechargeState.RECHARGE_STARTING);
			}
			break;
		case RECHARGE_IN_PROGRESS:
			if (targetStatus == RechargeState.IDLE || targetStatus == RechargeState.RECHARGE_STOPPING) {
				impl.setStartRechargeFlag(0);
				setRechargeState(RechargeState.RECHARGE_STOPPING);
			}
		case RECHARGE_STARTING:
			if (targetStatus == RechargeState.IDLE) {
				impl.setStartRechargeFlag(0);
				setRechargeState(RechargeState.IDLE);
			}
			break;
		case RECHARGE_STOPPING:
			// ignore user commands until CS notifies recharge end
			break;
		}
	}

	private void on0x101FrameReceived(PublicCSDataSnapshot snapshot) {
		boolean isRechargeInProgress = snapshot.getRechargeInProgress() == 1;

		switch (rechargeState) {
		case IDLE:
			if (isRechargeInProgress) {
				logger.warn("Unwanted recharge in progress..");
				impl.setStartRechargeFlag(0);
			}
			break;
		case RECHARGE_IN_PROGRESS:
			if (!isRechargeInProgress) {
				logger.info("CS notified recharge completion");
				setRechargeState(RechargeState.IDLE);
				impl.setStartRechargeFlag(0);
				notifyRechargeStoppedToBookingService();
			}
			break;
		case RECHARGE_STARTING:
			if (isRechargeInProgress) {
				logger.info("CS notified recharge start");
				setRechargeState(RechargeState.RECHARGE_IN_PROGRESS);
				notifyRechargeStartedToBookingService();
			}
			break;
		case RECHARGE_STOPPING:
			if (!isRechargeInProgress) {
				logger.info("CS notified recharge stop, recharge was aborted by user");
				setRechargeState(RechargeState.IDLE);
				notifyRechargeStoppedToBookingService();
			}
			break;
		}
	}
	
	private void updateNextBookingTime(long msFromNow) {
		Date nextBookingDate = new Date(System.currentTimeMillis() + msFromNow);
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(nextBookingDate);
		impl.setBookingTime(c.get(Calendar.DAY_OF_WEEK),
							c.get(Calendar.MONTH), 
							c.get(Calendar.YEAR),
							c.get(Calendar.HOUR),
							c.get(Calendar.MINUTE));
	}
	
	private class BookingServicePollThread extends Thread {

		@Override
		public void run() {
			while (!this.isInterrupted()) {
				logger.info("Polling Booking service for EVSE status");
				restClient.getEVSEStatus(impl.getEVSEId(),
						new ArrowheadRestResponseListener<ArrowheadRestClient.EVSEGetStatusResponse>() {

							@Override
							public void onResponse(EVSEGetStatusResponse data) {
								if (data == null) {
									logger.info("get EVSE status rest call failed, reservation status is now unknown");
									reservationState = ReservationState.UNKNOWN;
									return;
								}
								if (data.isReservedNow()) {
									reservationState = ReservationState.RESERVED;
									updateNextBookingTime(data.getNextReservationMs());
								} else {
									reservationState = ReservationState.NOT_RESERVED;
								}
							}
						});
				try {
					Thread.sleep(BOOKING_SERVICE_POLL_RATE_MS);
				} catch (InterruptedException e) {
					break;
				}
			}
			logger.info("BookingServicePollThread exiting..");
		}
	}

	private class TestResponseListener
			implements ArrowheadRestResponseListener<ArrowheadRestClient.EVSEStatusResponse> {

		@Override
		public void onResponse(EVSEStatusResponse data) {
			if (data == null) {
				logger.info("Error receiving response from REST service");
				return;
			}
			if (data.getStatus()) {
				logger.info("request completed succesfully");
			} else {
				logger.info("request failed");
			}
		}

	}

	private final TestResponseListener testResponseListener = new TestResponseListener(); // TODO
																							// implement
																							// proper
																							// handling
																							// of
																							// rest
																							// call
																							// responses

	private void notifyRechargeStartedToBookingService() {
		//this.restClient.notifyRechargeStateChange(impl.getEVSEId(), ArrowheadRestClient.RechargeStatus.RECHARGE_STARTED,
		//		"null", testResponseListener);
	}

	private void notifyRechargeStoppedToBookingService() {
		//this.restClient.notifyRechargeStateChange(impl.getEVSEId(), ArrowheadRestClient.RechargeStatus.RECHARGE_STOPPED,
		//		"null", testResponseListener);
	}

	@Override
	public void onPrivateCSMessage(int code, PrivateCSDataSnapshot data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPublicCSMessage(int code, PublicCSDataSnapshot snapshot) {
		if (code == 0x101) {
			this.on0x101FrameReceived(snapshot);
		}
	}

	@Override
	public void onMotoTronCSMessage(int code, MotoTronDataSnapshot snapshot) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onControlMessage(ControlMessage message) {

		if (!(message instanceof T312RechargeRequestMessage)) {
			logger.info("invalid control message received");
			return;
		}

		T312RechargeRequestMessage rechargeRequest = (T312RechargeRequestMessage) message;

		if (rechargeRequest.getMessageType() == ControlMessageCodes.T312_START_RECHARGE) {
			this.requestStateChange(RechargeState.RECHARGE_IN_PROGRESS);
		} else {
			this.requestStateChange(RechargeState.IDLE);
		}
	}

	@Override
	public void onShutdown() {
		this.restClient.close();
		try {
			this.bookingServicePollThread.interrupt();
			this.bookingServicePollThread.join();
		} catch (InterruptedException e) {
			// ignore
		}
	}

}
