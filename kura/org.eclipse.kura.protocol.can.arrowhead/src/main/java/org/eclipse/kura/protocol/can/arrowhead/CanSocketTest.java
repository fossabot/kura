package org.eclipse.kura.protocol.can.arrowhead;

import java.io.IOException;
import java.util.Map;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.kura.protocol.can.CanConnectionService;
import org.eclipse.kura.protocol.can.CanMessage;
import org.eclipse.kura.protocol.can.recharge.BookingInfo;
import org.eclipse.kura.protocol.can.recharge.CurrentDateInfo;
import org.eclipse.kura.protocol.can.recharge.RechargeInfo;


public class CanSocketTest implements ConfigurableComponent {
	private static final Logger s_logger = LoggerFactory.getLogger(CanSocketTest.class);
	private static final String THREAD_DELAY= "can.initial.threads.delay";
	private static final String ID_200_FREQUENCY= "can.id200.message.frequency";
	private static final String ID_201_FREQUENCY= "can.id201.message.frequency";
	private static final String ID_202_FREQUENCY= "can.id202.message.frequency";
	private static final String IS_BIG_ENDIAN= "can.bigendian";

	private CanConnectionService 	m_canConnection;
	private Map<String,Object>   	m_properties;
	private Thread 					m_listenThread;
	private Thread 					m_sendThread1;
	private Thread 					m_sendThread2;
	private Thread 					m_sendThread3;
	private String					m_ifName;

	private RechargeInfo rechargeInfo;
	private BookingInfo bookingInfo;
	private CurrentDateInfo currentDateInfo;
	private static int threadsDelay;
	private static int id200Freq;
	private static int id201Freq;
	private static int id202Freq;
	private boolean isBigEndian= true;
	
	private volatile boolean senderRunning = true;
	private volatile boolean receiverRunning = true;

	public void setCanConnectionService(CanConnectionService canConnection) {
		this.m_canConnection = canConnection;
	}

	public void unsetCanConnectionService(CanConnectionService canConnection) {
		this.m_canConnection = null;
	}

	protected void activate(ComponentContext componentContext, Map<String,Object> properties) {
		m_properties = properties;
		s_logger.info("activating Minigateway can test");
		m_ifName="can0";

		if(m_properties!=null){
			if(m_properties.get("can.name") != null) 
				m_ifName = (String) m_properties.get("can.name");

			rechargeInfo= populateRechargeInfo();
			bookingInfo= populateBookingInfo();
			currentDateInfo= populateCurrentDateInfo();
			
			getDelays();
			isBigEndian= (Boolean) m_properties.get(IS_BIG_ENDIAN);
		}

		if(m_listenThread!=null){
			m_listenThread.interrupt();
			try {
				m_listenThread.join(100);
				receiverRunning= false;
			} catch (InterruptedException e) {
				// Ignore
			}
			m_listenThread=null;
		}

		m_listenThread = new Thread(new Runnable() {		
			@Override
			public void run() {
				if(m_canConnection!=null){
					while(receiverRunning){
						doReceiveTest();
					}
				}
			}
		});
		m_listenThread.start();

		startSendThreads();
	}

	protected void deactivate(ComponentContext componentContext) {
		if(m_listenThread!=null){
			m_listenThread.interrupt();
			try {
				m_listenThread.join(100);
				receiverRunning= false;
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		m_listenThread=null;
		
		stopSendThreads();
	}
	
	public void updated(Map<String,Object> properties)
	{
		s_logger.debug("updated...");		

		m_properties = properties;
		if(m_properties!=null){
			if(m_properties.get("can.name") != null) 
				m_ifName = (String) m_properties.get("can.name");
			rechargeInfo= populateRechargeInfo();
			bookingInfo= populateBookingInfo();
			currentDateInfo= populateCurrentDateInfo();
			
			getDelays();
			isBigEndian= (Boolean) m_properties.get(IS_BIG_ENDIAN);
		}
		
		stopSendThreads();
		senderRunning= true;
		startSendThreads();
	}
	
	private void getDelays() {
		threadsDelay = Integer.parseInt((String) m_properties.get(THREAD_DELAY));
		id200Freq = Integer.parseInt((String) m_properties.get(ID_200_FREQUENCY));
		id201Freq = Integer.parseInt((String) m_properties.get(ID_201_FREQUENCY));
		id202Freq = Integer.parseInt((String) m_properties.get(ID_202_FREQUENCY));
		
	}
	
	private void startSendThreads(){
		if(m_sendThread1!=null){
			m_sendThread1.interrupt();
			try {
				m_sendThread1.join(100);
				senderRunning = false;
			} catch (InterruptedException e) {
				// Ignore
			}
			m_sendThread1=null;
		}

		m_sendThread1 = new Thread(new Runnable() {		
			@Override
			public void run() {
				try {
					Thread.sleep(threadsDelay * 0);
				} catch (InterruptedException e) {
				}
				if(m_canConnection!=null){
					while(senderRunning){
						doSend1Test();
						s_logger.debug("Thread1 sleeping for: " + id200Freq);
						try {
							Thread.sleep(id200Freq);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		});
		m_sendThread1.start();
		
		if(m_sendThread2!=null){
			m_sendThread2.interrupt();
			try {
				m_sendThread2.join(100);
				senderRunning= false;
			} catch (InterruptedException e) {
				// Ignore
			}
			m_sendThread2=null;
		}

		m_sendThread2 = new Thread(new Runnable() {		
			@Override
			public void run() {
				try {
					Thread.sleep(threadsDelay * 1);
				} catch (InterruptedException e) {
				}
				if(m_canConnection!=null){
					while(senderRunning){
						doSend2Test();
						s_logger.debug("Thread2 sleeping for: " + id201Freq);
						try {
							Thread.sleep(id201Freq);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		});
		m_sendThread2.start();
		
		
		if(m_sendThread3!=null){
			m_sendThread3.interrupt();
			try {
				m_sendThread3.join(100);
				senderRunning= false;
			} catch (InterruptedException e) {
				// Ignore
			}
			m_sendThread3=null;
		}

		m_sendThread3 = new Thread(new Runnable() {		
			@Override
			public void run() {
				try {
					Thread.sleep(threadsDelay * 2);
				} catch (InterruptedException e) {
				}
				if(m_canConnection!=null){
					while(senderRunning){
						doSend3Test();
						s_logger.debug("Thread3 sleeping for: " + id202Freq);
						try {
							Thread.sleep(id202Freq);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		});
		m_sendThread3.start();
	}
	
	private void stopSendThreads(){
		if(m_sendThread1!=null){
			m_sendThread1.interrupt();
			try {
				m_sendThread1.join(100);
				senderRunning= false;
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		m_sendThread1=null;
		
		
		if(m_sendThread2!=null){
			m_sendThread2.interrupt();
			try {
				m_sendThread2.join(100);
				senderRunning= false;
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		m_sendThread2=null;
		
		
		if(m_sendThread3!=null){
			m_sendThread3.interrupt();
			try {
				m_sendThread3.join(100);
				senderRunning= false;
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		m_sendThread3=null;
	}


	private RechargeInfo populateRechargeInfo() {
		RechargeInfo ri= new RechargeInfo();
		int startRecharge = Integer.parseInt((String) m_properties.get(RechargeInfo.START_RECHARGE));
		int rechargeBooked = Integer.parseInt((String) m_properties.get(RechargeInfo.RECHARGE_IS_BOOKED));
		int solarRadiation = Integer.parseInt((String) m_properties.get(RechargeInfo.SOLAR_RADIATION));
		int csReset = Integer.parseInt((String) m_properties.get(RechargeInfo.CS_RESET));

		ri.setStartRecharge(startRecharge);
		ri.setRechargeBooked(rechargeBooked);
		ri.setSolarRadiationLevel(solarRadiation);
		ri.setCsReset(csReset);

		return ri;
	}

	private BookingInfo populateBookingInfo() {
		BookingInfo bi= new BookingInfo();
		int bookingTimeHour = Integer.parseInt((String) m_properties.get(BookingInfo.BOOKING_TIME_HOUR));
		int bookingTimeMinute = Integer.parseInt((String) m_properties.get(BookingInfo.BOOKING_TIME_MINUTE));
		int bookingDateDay = Integer.parseInt((String) m_properties.get(BookingInfo.BOOKING_DATE_DAY));
		int bookingDateMonth = Integer.parseInt((String) m_properties.get(BookingInfo.BOOKING_DATE_MONTH));
		int bookingDateYear = Integer.parseInt((String) m_properties.get(BookingInfo.BOOKING_DATE_YEAR));
		int currentTimeHour = Integer.parseInt((String) m_properties.get(BookingInfo.CURRENT_TIME_HOUR));
		int currentTimeMinute = Integer.parseInt((String) m_properties.get(BookingInfo.CURRENT_TIME_MINUTE));

		bi.setBookingTimeHour(bookingTimeHour);
		bi.setBookingTimeMinute(bookingTimeMinute);
		bi.setBookingDateDay(bookingDateDay);
		bi.setBookingDateMonth(bookingDateMonth);
		bi.setBookingDateYear(bookingDateYear);
		bi.setCurrentTimeHour(currentTimeHour);
		bi.setCurrentTimeMinute(currentTimeMinute);

		return bi;
	}

	private CurrentDateInfo populateCurrentDateInfo() {
		CurrentDateInfo cdi= new CurrentDateInfo();
		int currentDateDay = Integer.parseInt((String) m_properties.get(CurrentDateInfo.CURRENT_DATE_DAY));
		int currentDateMonth = Integer.parseInt((String) m_properties.get(CurrentDateInfo.CURRENT_DATE_MONTH));
		int currentDateYear = Integer.parseInt((String) m_properties.get(CurrentDateInfo.CURRENT_DATE_YEAR));

		cdi.setCurrentDateDay(currentDateDay);
		cdi.setCurrentDateMonth(currentDateMonth);
		cdi.setCurrentDateYear(currentDateYear);

		return cdi;
	}

	public void doReceiveTest() {
		CanMessage cm = null;
		s_logger.debug("Waiting for a request");
		try {
			cm = m_canConnection.receiveCanMessage(-1,0x7FF);
		} catch (KuraException e) {
			s_logger.warn("CanConnection Crash! -> KuraException");			
			e.printStackTrace();
		} catch (IOException e) {
			s_logger.warn("CanConnection Crash! -> IOException");			
			e.printStackTrace();
		}
		
		if (cm != null) {
			int canId= cm.getCanId();
			s_logger.info("Received can message with Id: " + canId);

			if (canId == 0x100) {
				parseCanMessage1(cm);
			} else if (canId == 0x101) {
				parseCanMessage2(cm);
			} else if (canId == 0x102) {
				parseCanMessage3(cm);
			}
		}
		else{
			s_logger.warn("receive=null");
		}		
	}

	private void parseCanMessage1(CanMessage cm){
		byte[] b = null;
		b = cm.getData();
		if(b!=null && b.length == 8){
			StringBuilder sb = new StringBuilder("received : ");
			
			int powerOut;
			if (isBigEndian) {
				powerOut= buildShort(b[0], b[1]);
			} else {
				powerOut= buildShort(b[1], b[0]);
			}
			int minutesToRecharge= b[2];
			int secondsToRecharge= b[3];
			
			int energyOut;
			if (isBigEndian) {
				energyOut= buildShort(b[4], b[5]);
			} else {
				energyOut= buildShort(b[5], b[4]);
			}
			int powerPV;
			if (isBigEndian) {
				powerPV= buildShort(b[6], b[7]);
			} else {
				powerPV= buildShort(b[7], b[6]);
			}
			
			sb.append("Power out: " + powerOut + " W, ");
			sb.append("Minutes to recharge: " + minutesToRecharge + " minutes, ");
			sb.append("Seconds to recharge: " + secondsToRecharge + " seconds, ");
			sb.append("Energy out: " + energyOut + " Wh, ");
			sb.append("Power PV: " + powerPV + " W");
			
//			for(int i=0; i<b.length; i++){
//				sb.append(b[i]);
//				sb.append(";");
//			}
			sb.append(" on id = ");
			sb.append(cm.getCanId());			
			s_logger.debug(sb.toString());
		}
	}

	private void parseCanMessage2(CanMessage cm){
		byte[] b = null;
		b = cm.getData();
		if(b!=null && b.length == 5){
			StringBuilder sb = new StringBuilder("received : ");
			
			int faultFlag= b[0] & 0x01;
			int rechargeAvailable= (b[0] & 0x02) >> 1;
			int rechargeInProgress= (b[0] & 0x04) >> 2;
			int pvSystemActive= (b[0] & 0x08) >> 3;
			int auxChargerActive= (b[0] & 0x10) >> 4;
			int storageBatteryConcactorSts= (b[0] & 0x20) >> 5;
			int converterConcactorSts= (b[0] & 0x40) >> 6;
			
			int faultString= b[1];
			int igbtTemperature= b[2];
			int storageBatteryTemperature= b[3];
			int storageBatterySOC= b[4];
			
			sb.append("Fault flag: " + faultFlag + ", ");
			sb.append("Recharge available: " + rechargeAvailable + ", ");
			sb.append("Recharge in progress: " + rechargeInProgress + ", ");
			sb.append("PV System active: " + pvSystemActive + ", ");
			sb.append("Aux charger active: " + auxChargerActive + ", ");
			sb.append("Storage Battery Concactor Sts: " + storageBatteryConcactorSts + ", ");
			sb.append("Converter Contactor Sts: " + converterConcactorSts + ", ");
			
			sb.append("Fault string: " + faultString + ", ");
			sb.append("IGBT Temperature: " + igbtTemperature + " celsius, ");
			sb.append("Storage Battery Temperature: " + storageBatteryTemperature + " celsius, ");
			sb.append("Storage Battery SOC: " + storageBatterySOC + "celsius");
			
			sb.append(" on id = ");
			sb.append(cm.getCanId());			
			s_logger.debug(sb.toString());
		}
	}

	private void parseCanMessage3(CanMessage cm){
		byte[] b = null;
		b = cm.getData();
		if(b!=null && b.length == 8){
			StringBuilder sb = new StringBuilder("received : ");
			
			int vOut;
			if (isBigEndian) {
				vOut= buildShort(b[0], b[1]);
			} else {
				vOut= buildShort(b[1], b[0]);
			}
			
			int storageBatteryV;
			if (isBigEndian) {
				storageBatteryV= buildShort(b[2], b[3]);
			} else {
				storageBatteryV= buildShort(b[3], b[2]);
			}
			
			int pvSystemV;
			if (isBigEndian) {
				pvSystemV= buildShort(b[4], b[5]);
			} else {
				pvSystemV= buildShort(b[5], b[4]);
			}
		
			int iOut= b[6];
			int storageBatteryI= b[7];
			
			sb.append("V out: " + vOut + " V, ");
			sb.append("Storage Battery V: " + storageBatteryV + " V, ");
			sb.append("PV System V: " + pvSystemV + " V, ");
			sb.append("I out: " + iOut + " A, ");
			sb.append("Storage Battery I: " + storageBatteryI + " A");
			
			sb.append(" on id = ");
			sb.append(cm.getCanId());			
			s_logger.debug(sb.toString());
		}
	}

	public void doSend1Test() {
		try {
			sendMessage1(m_ifName);
		} catch (Exception e) {
			s_logger.warn("CanConnection Crash!");			
			e.printStackTrace();
		}
	}
	
	public void doSend2Test() {
		try {
			sendMessage2(m_ifName);
		} catch (Exception e) {
			s_logger.warn("CanConnection Crash!");			
			e.printStackTrace();
		}
	}
	
	public void doSend3Test() {
		try {
			sendMessage3(m_ifName);
		} catch (Exception e) {
			s_logger.warn("CanConnection Crash!");			
			e.printStackTrace();
		}
	}

	private void sendMessage1(String ifName) throws KuraException, IOException {

		if((m_canConnection==null)) 
			return;
		int id = 0x200;
		StringBuilder sb = new StringBuilder("Trying to send message 1 can frame with message = ");
		byte bMessage[] = new byte[1];
		int startRecharge= rechargeInfo.getStartRecharge(); //start recharge [0,1]
		int isBooked= (rechargeInfo.getRechargeBooked() << 1); //Recharge is booked? [0-No;1-Yes]
		int solarIrradiation= (rechargeInfo.getSolarRadiationLevel() << 2); //Next Day Solar Radiation Level [0-Low; 1-Medium; 2-High]
		int csReset= (rechargeInfo.getCsReset() << 4); //Charging station reset [0-No;1-Yes]

		bMessage[0]= (byte) (startRecharge + isBooked + solarIrradiation + csReset); //Panel PC Start/Stop/Booking/Next day weather forecast/CS Reset

		sb.append(bMessage[0]);
		sb.append(" ");

		sb.append(" and id = ");
		sb.append(id);
		s_logger.debug(sb.toString());

		m_canConnection.sendCanMessage(ifName, id, bMessage);
		s_logger.info("Message sent with id: " + id);
	}

	private void sendMessage2(String ifName) throws KuraException, IOException {

		if((m_canConnection==null)) 
			return;
		int id = 0x201;
		StringBuilder sb = new StringBuilder("Trying to send message 2 can frame with message = ");
		byte bMessage[] = new byte[8];

		int bookingTimeHour= bookingInfo.getBookingTimeHour();
		int bookingTimeMinute= bookingInfo.getBookingTimeMinute();
		int bookingDateDay= bookingInfo.getBookingDateDay();
		int bookingDateMonth= bookingInfo.getBookingDateMonth();
		int bookingDateYear= bookingInfo.getBookingDateYear();
		int currentTimeHour= bookingInfo.getCurrentTimeHour();
		int currentTimeMinute= bookingInfo.getCurrentTimeMinute();

		bMessage[0]= (byte) bookingTimeHour; //Booking time: hour
		bMessage[1]= (byte) bookingTimeMinute; //Booking time: minute
		bMessage[2]= (byte) bookingDateDay; //Booking date: day
		bMessage[3]= (byte) bookingDateMonth; //Booking date: month
		
		if (isBigEndian) {
			bMessage[4]= (byte) ((bookingDateYear >> 8) & 0xFF); //Booking date: year
			bMessage[5]= (byte) (bookingDateYear & 0xFF); //Booking date: year
		} else {
			bMessage[4]= (byte) (bookingDateYear & 0xFF); //Booking date: year
			bMessage[5]= (byte) ((bookingDateYear >> 8) & 0xFF); //Booking date: year
		}
		
		bMessage[6]= (byte) currentTimeHour; //Current time: hour
		bMessage[7]= (byte) currentTimeMinute; //Current time: minute

		sb.append("Booking time: hour " +   bMessage[0] + ", ");
		sb.append("Booking time: minute " + bMessage[1] + ", ");
		sb.append("Booking date: day " +    bMessage[2] + ", ");
		sb.append("Booking date: month " +  bMessage[3] + ", ");
		if (isBigEndian) {
			sb.append("Booking date: year " +   buildShort(bMessage[4], bMessage[5]) + ", ");
		} else {
			sb.append("Booking date: year " +   buildShort(bMessage[5], bMessage[4]) + ", ");
		}
		sb.append("Current time: hour " +   bMessage[6] + ", ");
		sb.append("Current time: minute " + bMessage[7]);

		sb.append(" and id = ");
		sb.append(id);
		s_logger.debug(sb.toString());

		m_canConnection.sendCanMessage(ifName, id, bMessage);
		s_logger.info("Message sent with id: " + id);
	}

	private void sendMessage3(String ifName) throws KuraException, IOException {

		if((m_canConnection==null)) 
			return;
		int id = 0x202;
		StringBuilder sb = new StringBuilder("Trying to send message 3 can frame with message = ");
		byte bCurrentDate[] = new byte[4];
		int currentDateDay= currentDateInfo.getCurrentDateDay();
		int currentDateMonth= currentDateInfo.getCurrentDateMonth();
		int currentDateYear= currentDateInfo.getCurrentDateYear();

		bCurrentDate[0]= (byte) currentDateDay; //Current date: day
		bCurrentDate[1]= (byte) currentDateMonth; //Current date: month
		
		if (isBigEndian) {
			bCurrentDate[2]= (byte) ((currentDateYear >> 8) & 0xFF); //Current date: year
			bCurrentDate[3]= (byte) (currentDateYear & 0xFF); //Current date: year
		} else {
			bCurrentDate[2]= (byte) (currentDateYear & 0xFF); //Current date: year
			bCurrentDate[3]= (byte) ((currentDateYear >> 8) & 0xFF); //Current date: year
		}

		sb.append("Current date: day " +     bCurrentDate[0] + ", ");
		sb.append("Current date: month " +   bCurrentDate[1] + ", ");
		if (isBigEndian) {
			sb.append("Current date: year " +    buildShort(bCurrentDate[2], bCurrentDate[3]));
		} else {
			sb.append("Current date: year " +    buildShort(bCurrentDate[3], bCurrentDate[2]));
		}

		sb.append(" and id = ");
		sb.append(id);
		s_logger.debug(sb.toString());

		m_canConnection.sendCanMessage(ifName, id, bCurrentDate);
		s_logger.info("Message sent with id: " + id);
	}

	private int buildShort(byte high, byte low){
		return ((0xFF & (int) high) << 8) + ((0xFF & (int) low));
	}
}
