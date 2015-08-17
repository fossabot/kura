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

	private CanConnectionService 	m_canConnection;
	private Map<String,Object>   	m_properties;
	private Thread 					m_listenThread;
	private Thread 					m_sendThread;
	private String					m_ifName;
	private int						m_nextMessageIndex;

	private RechargeInfo rechargeInfo;
	private BookingInfo bookingInfo;
	private CurrentDateInfo currentDateInfo;

	public void setCanConnectionService(CanConnectionService canConnection) {
		this.m_canConnection = canConnection;
	}

	public void unsetCanConnectionService(CanConnectionService canConnection) {
		this.m_canConnection = null;
	}

	protected void activate(ComponentContext componentContext, Map<String,Object> properties) {
		m_properties = properties;
		s_logger.info("activating Panel PC can test");
		m_ifName="can0";

		if(m_properties!=null){
			if(m_properties.get("can.name") != null) 
				m_ifName = (String) m_properties.get("can.name");

			rechargeInfo= populateRechargeInfo();
			bookingInfo= populateBookingInfo();
			currentDateInfo= populateCurrentDateInfo();
		}

		if(m_listenThread!=null){
			m_listenThread.interrupt();
			try {
				m_listenThread.join(100);
			} catch (InterruptedException e) {
				// Ignore
			}
			m_listenThread=null;
		}

		m_listenThread = new Thread(new Runnable() {		
			@Override
			public void run() {
				if(m_canConnection!=null){
					while(true){
						//doCanTest();
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		});
		m_listenThread.start();


		if(m_sendThread!=null){
			m_sendThread.interrupt();
			try {
				m_sendThread.join(100);
			} catch (InterruptedException e) {
				// Ignore
			}
			m_sendThread=null;
		}

		m_sendThread = new Thread(new Runnable() {		
			@Override
			public void run() {
				if(m_canConnection!=null){
					while(true){
						doSendTest();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		});
		m_sendThread.start();
	}

	protected void deactivate(ComponentContext componentContext) {
		if(m_listenThread!=null){
			m_listenThread.interrupt();
			try {
				m_listenThread.join(100);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		m_listenThread=null;

		if(m_sendThread!=null){
			m_sendThread.interrupt();
			try {
				m_sendThread.join(100);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		m_sendThread=null;
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
		}
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

	public void doCanTest() {
		CanMessage cm = null;
		s_logger.info("Wait for a request");
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
		if(b!=null){
			StringBuilder sb = new StringBuilder("received : ");
			for(int i=0; i<b.length; i++){
				sb.append(b[i]);
				sb.append(";");
			}
			sb.append(" on id = ");
			sb.append(cm.getCanId());			
			s_logger.info(sb.toString());
		}
	}

	private void parseCanMessage2(CanMessage cm){
		byte[] b = null;
		b = cm.getData();
		if(b!=null){
			StringBuilder sb = new StringBuilder("received : ");
			for(int i=0; i<b.length; i++){
				sb.append(b[i]);
				sb.append(";");
			}
			sb.append(" on id = ");
			sb.append(cm.getCanId());			
			s_logger.info(sb.toString());
		}
	}

	private void parseCanMessage3(CanMessage cm){
		byte[] b = null;
		b = cm.getData();
		if(b!=null){
			StringBuilder sb = new StringBuilder("received : ");
			for(int i=0; i<b.length; i++){
				sb.append(b[i]);
				sb.append(";");
			}
			sb.append(" on id = ");
			sb.append(cm.getCanId());			
			s_logger.info(sb.toString());
		}
	}

	public void doSendTest() {

		try {
			if(m_nextMessageIndex == 0){
				sendMessage1(m_ifName);
			} else if(m_nextMessageIndex == 1){
				sendMessage2(m_ifName);
			} else {
				sendMessage3(m_ifName);
			}
		} catch (Exception e) {
			s_logger.warn("CanConnection Crash!");			
			e.printStackTrace();
		}
		m_nextMessageIndex++;
		m_nextMessageIndex= m_nextMessageIndex % 3;

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
		s_logger.info(sb.toString());

		m_canConnection.sendCanMessage(ifName, id, bMessage);
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
		bMessage[4]= (byte) ((bookingDateYear >> 8) & 0xFF); //Booking date: year
		bMessage[5]= (byte) (bookingDateYear & 0xFF); //Booking date: year
		bMessage[6]= (byte) currentTimeHour; //Current time: hour
		bMessage[7]= (byte) currentTimeMinute; //Current time: minute

		sb.append("Booking time: hour " +   bMessage[0] + ", ");
		sb.append("Booking time: minute " + bMessage[1] + ", ");
		sb.append("Booking date: day " +    bMessage[2] + ", ");
		sb.append("Booking date: month " +  bMessage[3] + ", ");
		sb.append("Booking date: year " +   buildShort(bMessage[4], bMessage[5]) + ", ");
		sb.append("Current time: hour " +   bMessage[6] + ", ");
		sb.append("Current time: minute " + bMessage[7]);

		sb.append(" and id = ");
		sb.append(id);
		s_logger.info(sb.toString());

		m_canConnection.sendCanMessage(ifName, id, bMessage);
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
		bCurrentDate[2]= (byte) ((currentDateYear >> 8) & 0xFF); //Current date: year
		bCurrentDate[3]= (byte) (currentDateYear & 0xFF); //Current date: year

		sb.append("Current date: day " +     bCurrentDate[0] + ", ");
		sb.append("Current date: month " +   bCurrentDate[1] + ", ");
		sb.append("Current date: year " +    buildShort(bCurrentDate[2], bCurrentDate[3]));

		sb.append(" and id = ");
		sb.append(id);
		s_logger.info(sb.toString());

		m_canConnection.sendCanMessage(ifName, id, bCurrentDate);
	}

	private int buildShort(byte high, byte low){
		return ((0xFF & (int) high) << 8) + ((0xFF & (int) low));
	}
}
