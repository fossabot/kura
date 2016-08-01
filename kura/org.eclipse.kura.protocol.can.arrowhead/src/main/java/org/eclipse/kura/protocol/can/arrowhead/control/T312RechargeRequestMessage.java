package org.eclipse.kura.protocol.can.arrowhead.control;

import org.eclipse.kura.message.KuraPayload;

public class T312RechargeRequestMessage extends ControlMessage {
	
	public String userName;
	public String reservationId;

	private static final String USER_NAME_METRIC_NAME = "userName";
	private static final String RESERVATION_ID_METRIC_NAME = "reservationId";
	
	public T312RechargeRequestMessage(int requestType, KuraPayload payload) throws InvalidControlMessageException {
		super(requestType);
		
		try {
		this.userName = (String) payload.getMetric(USER_NAME_METRIC_NAME);
		} catch (Exception e) {
			throw new InvalidControlMessageException("Invalid user id");
		}
		
		try {
		this.reservationId = (String) payload.getMetric(RESERVATION_ID_METRIC_NAME);
		} catch (Exception e) {
			throw new InvalidControlMessageException("Invalid reservation id");
		}
	}
	
	public T312RechargeRequestMessage(int requestType, String userName, String reservationId) {
		super(requestType);
		this.userName = userName;
		this.reservationId = reservationId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getReservationId() {
		return reservationId;
	}
	
	public String toString() {
		return "messageType: " + getMessageType() + " userName: " + getUserName() + "reservationId: " + getReservationId();
	} 
}
