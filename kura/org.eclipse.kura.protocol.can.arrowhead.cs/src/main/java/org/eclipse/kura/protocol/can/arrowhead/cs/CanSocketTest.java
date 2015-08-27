package org.eclipse.kura.protocol.can.arrowhead.cs;

import java.io.IOException;
import java.util.Map;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.kura.protocol.can.CanConnectionService;
import org.eclipse.kura.protocol.can.CanMessage;
import org.eclipse.kura.protocol.can.cs.Message1;
import org.eclipse.kura.protocol.can.cs.Message2;
import org.eclipse.kura.protocol.can.cs.Message3;


public class CanSocketTest implements ConfigurableComponent {
	private static final Logger s_logger = LoggerFactory.getLogger(CanSocketTest.class);
	private static final String IS_BIG_ENDIAN= "can.bigendian";

	private CanConnectionService 	m_canConnection;
	private Map<String,Object>   	m_properties;
	private Thread 					m_listenThread;
	private Thread 					m_sendThread;
	private String					m_ifName;
	private int						m_nextMessageIndex;
	private boolean isBigEndian= true;

	private Message1 message1Info;
	private Message2 message2Info;
	private Message3 message3Info;

	private int counter= 0;

	public void setCanConnectionService(CanConnectionService canConnection) {
		this.m_canConnection = canConnection;
	}

	public void unsetCanConnectionService(CanConnectionService canConnection) {
		this.m_canConnection = null;
	}

	protected void activate(ComponentContext componentContext, Map<String,Object> properties) {
		m_properties = properties;
		s_logger.info("activating CS can test");
		m_ifName="can0";

		if(m_properties!=null){
			if(m_properties.get("can.name") != null) 
				m_ifName = (String) m_properties.get("can.name");

			message1Info= populateMessage1Info();
			message2Info= populateMessage2Info();
			message3Info= populateMessage3Info();

			isBigEndian= (Boolean) m_properties.get(IS_BIG_ENDIAN);
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
						doReceiveTest();
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
							Thread.sleep(10);
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
			message1Info= populateMessage1Info();
			message2Info= populateMessage2Info();
			message3Info= populateMessage3Info();

			isBigEndian= (Boolean) m_properties.get(IS_BIG_ENDIAN);
		}
	}

	private Message1 populateMessage1Info() {
		Message1 m1= new Message1();
		int powerOut = Integer.parseInt((String) m_properties.get(Message1.POWER_OUT));
		int timeToRechargeMinutes = Integer.parseInt((String) m_properties.get(Message1.TIME_TO_RECHARGE_MINUTES));
		int timeToRechargeSeconds = Integer.parseInt((String) m_properties.get(Message1.TIME_TO_RECHARGE_SECONDS));
		int energyOut = Integer.parseInt((String) m_properties.get(Message1.ENERGY_OUT));
		int powerPV = Integer.parseInt((String) m_properties.get(Message1.POWER_PV));

		m1.setPowerOut(powerOut);
		m1.setTimeToRechargeMinutes(timeToRechargeMinutes);
		m1.setTimeToRechargeSeconds(timeToRechargeSeconds);
		m1.setEnergyOut(energyOut);
		m1.setPowerPV(powerPV);

		return m1;
	}

	private Message2 populateMessage2Info() {
		Message2 m2= new Message2();
		int faultFlag = Integer.parseInt((String) m_properties.get(Message2.FAULT_FLAG));
		int rechargeAvailable = Integer.parseInt((String) m_properties.get(Message2.RECHARGE_AVAILABLE));
		int rechargeInProgress = Integer.parseInt((String) m_properties.get(Message2.RECHARGE_IN_PROGRESS));
		int pvSystemActive = Integer.parseInt((String) m_properties.get(Message2.PV_SYSTEM_ACTIVE));
		int auxChargerActive = Integer.parseInt((String) m_properties.get(Message2.AUX_CHARGER_ACTIVE));
		int storageBatteryContactorStatus = Integer.parseInt((String) m_properties.get(Message2.STORAGE_BATTERY_CONTACTOR_STATUS));
		int converterContactorStatus = Integer.parseInt((String) m_properties.get(Message2.CONVERTER_CONTACTOR_STATUS));
		int faultString = Integer.parseInt((String) m_properties.get(Message2.FAULT_STRING));
		int igbtTemp = Integer.parseInt((String)  m_properties.get(Message2.IGBT_TEMP));
		int storageTemp = Integer.parseInt((String)  m_properties.get(Message2.STORAGE_TEMP));
		int storageBatterySoc = Integer.parseInt((String)  m_properties.get(Message2.STORAGE_BATTERY_SOC));

		m2.setFaultFlag(faultFlag);
		m2.setRechargeAvailable(rechargeAvailable);
		m2.setRechargeInProgress(rechargeInProgress);
		m2.setPvSystemActive(pvSystemActive);
		m2.setAuxChargerActive(auxChargerActive);
		m2.setStorageBatteryContactorStatus(storageBatteryContactorStatus);
		m2.setConverterContactorStatus(converterContactorStatus);
		m2.setFaultString(faultString);
		m2.setIgbtTemp(igbtTemp);
		m2.setStorageTemp(storageTemp);
		m2.setStorageBatterySoc(storageBatterySoc);

		return m2;
	}

	private Message3 populateMessage3Info() {
		Message3 m3= new Message3();
		int vOut = Integer.parseInt((String)  m_properties.get(Message3.V_OUT));
		int storageBatteryV = Integer.parseInt((String)  m_properties.get(Message3.STORAGE_BATTERY_V));
		int pvSystemV = Integer.parseInt((String)  m_properties.get(Message3.PV_SYSTEM_V));
		int iOut = Integer.parseInt((String) m_properties.get(Message3.I_OUT));
		int storageBatteryI = Integer.parseInt((String) m_properties.get(Message3.STORAGE_BATTERY_I));

		m3.setvOut(vOut);
		m3.setStorageBatteryV(storageBatteryV);
		m3.setPvSystemV(pvSystemV);
		m3.setiOut(iOut);
		m3.setStorageBatteryI(storageBatteryI);

		return m3;
	}

	public void doReceiveTest() {
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

			if (canId == 0x200) {
				parseCanMessage1(cm);
			} else if (canId == 0x201) {
				parseCanMessage2(cm);
			} else if (canId == 0x202) {
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
		if(b!=null && b.length == 1){
			StringBuilder sb = new StringBuilder("received : ");

			int startRecharge= b[0] & 0x01; //start recharge [0,1]
			int isBooked= (b[0] & 0x02) >> 1; //Recharge is booked? [0-No;1-Yes]
			int solarIrradiation= (b[0] & 0x0C) >> 2; //Next Day Solar Radiation Level [0-Low; 1-Medium; 2-High]
			int csReset= (b[0] & 0x10) >> 4; //Charging station reset [0-No;1-Yes]


			sb.append("start recharge: " + startRecharge + ", ");
			sb.append("Recharge is booked?: " + isBooked + ", ");
			sb.append("Next Day Solar Radiation Level: " + solarIrradiation + ", ");
			sb.append("Charging station reset: " + csReset);

			sb.append(" on id = ");
			sb.append(cm.getCanId());			
			s_logger.info(sb.toString());
		}
	}

	private void parseCanMessage2(CanMessage cm){
		byte[] b = null;
		b = cm.getData();
		if(b!=null && b.length == 8){
			StringBuilder sb = new StringBuilder("received : ");

			int bookingTimeHour= b[0]; //Booking time: hour
			int bookingTimeMinute= b[1]; //Booking time: minute
			int bookingDateDay= b[2]; //Booking date: day
			int bookingDateMonth= b[3]; //Booking date: month
			
			int bookingDateYear;
			if (isBigEndian) {
				bookingDateYear= buildShort(b[4], b[5]); //Booking date: year
			} else {
				bookingDateYear= buildShort(b[5], b[4]); //Booking date: year
			}
			int currentTimeHour= b[6]; //Current time: hour
			int currentTimeMinute= b[7]; //Current time: minute

			sb.append("Booking time: hour " + bookingTimeHour + ", ");
			sb.append("Booking time: minute " + bookingTimeMinute + ", ");
			sb.append("Booking date: day " + bookingDateDay + ", ");
			sb.append("Booking date: month " + bookingDateMonth + ", ");
			sb.append("Booking date: year " + bookingDateYear + ", ");
			sb.append("Current time: hour " + currentTimeHour + ", ");
			sb.append("Current time: minute " + currentTimeMinute);

			sb.append(" on id = ");
			sb.append(cm.getCanId());			
			s_logger.info(sb.toString());
		}
	}

	private void parseCanMessage3(CanMessage cm){
		byte[] b = null;
		b = cm.getData();
		if(b!=null && b.length == 4){
			StringBuilder sb = new StringBuilder("received : ");

			int currentDateDay= b[0];
			int currentDateMonth= b[1];

			int currentDateYear;
			if (isBigEndian) {
				currentDateYear= buildShort(b[2], b[3]);
			} else{
				currentDateYear= buildShort(b[3], b[2]);
			}

			sb.append("Current date: day " + currentDateDay + ", ");
			sb.append("Current date: month " + currentDateMonth + ", ");
			sb.append("Current date: year " +  currentDateYear);


			sb.append(" on id = ");
			sb.append(cm.getCanId());			
			s_logger.info(sb.toString());
		}
	}

	public void doSendTest() {


		try {
			if(counter != 0){
				sendMessage1(m_ifName);
			}else{
				if(m_nextMessageIndex == 0){
					sendMessage2(m_ifName);
				} else if(m_nextMessageIndex == 1){
					sendMessage3(m_ifName);
				}
				m_nextMessageIndex++;
				m_nextMessageIndex= m_nextMessageIndex % 2;
			}
		} catch (Exception e) {
			s_logger.warn("CanConnection Crash!");			
			e.printStackTrace();
		}
		counter++;
		counter = counter % 10;
	}

	private void sendMessage1(String ifName) throws KuraException, IOException {

		if((m_canConnection==null)) 
			return;
		int id = 0x100;
		StringBuilder sb = new StringBuilder("Trying to send message 1 can frame with message = ");
		byte bMessage[] = new byte[8];
		int powerOut= message1Info.getPowerOut(); //Power Out [W]
		int timeToRechargeMinutes= message1Info.getTimeToRechargeMinutes(); //Time to recharge [minutes]
		int timeToRechargeSeconds= message1Info.getTimeToRechargeSeconds(); //Time to recharge [seconds]
		int energyOut= message1Info.getEnergyOut(); //Energy Out [Wh]
		int powerPV= message1Info.getPowerPV(); //Power PV Out [W]

		if (isBigEndian) {
			bMessage[0]= (byte) ((powerOut >> 8) & 0xFF);
			bMessage[1]= (byte) (powerOut & 0xFF);
		} else {
			bMessage[0]= (byte) (powerOut & 0xFF);
			bMessage[1]= (byte) ((powerOut >> 8) & 0xFF);
		}

		bMessage[2]= (byte) timeToRechargeMinutes;
		bMessage[3]= (byte) timeToRechargeSeconds;

		if (isBigEndian) {
			bMessage[4]= (byte) ((energyOut >> 8) & 0xFF);
			bMessage[5]= (byte) (energyOut & 0xFF);
		} else {
			bMessage[4]= (byte) (energyOut & 0xFF);
			bMessage[5]= (byte) ((energyOut >> 8) & 0xFF);
		}

		if (isBigEndian) {
			bMessage[6]= (byte) ((powerPV >> 8) & 0xFF);
			bMessage[7]= (byte) (powerPV & 0xFF);
		} else {
			bMessage[6]= (byte) (powerPV & 0xFF);
			bMessage[7]= (byte) ((powerPV >> 8) & 0xFF);
		}

		if (isBigEndian) {
			sb.append("Power Out " +  buildShort(bMessage[0], bMessage[1]) + " W, ");
		} else {
			sb.append("Power Out " +  buildShort(bMessage[1], bMessage[0]) + " W, ");
		}
		sb.append("Time to Recharge " +  bMessage[2] + " minutes, ");
		sb.append("Time to Recharge " +  bMessage[3] + " s, ");
		if (isBigEndian) {
			sb.append("Energy Out " +  buildShort(bMessage[4], bMessage[5]) + " Wh, ");
		} else {
			sb.append("Energy Out " +  buildShort(bMessage[5], bMessage[4]) + " Wh, ");
		}
		if (isBigEndian) {
			sb.append("Power PV " +  buildShort(bMessage[6], bMessage[7]) + " W");
		} else {
			sb.append("Power PV " +  buildShort(bMessage[7], bMessage[6]) + " W");
		}

		sb.append(" and id = ");
		sb.append(id);
		s_logger.debug(sb.toString());

		m_canConnection.sendCanMessage(ifName, id, bMessage);
		s_logger.info("Message sent with id: " + id);
	}

	private void sendMessage2(String ifName) throws KuraException, IOException {

		if((m_canConnection==null)) 
			return;
		int id = 0x101;
		StringBuilder sb = new StringBuilder("Trying to send message 2 can frame with message = ");
		byte bMessage[] = new byte[5];

		int faultFlag= message2Info.isFaultFlag(); //fault flag [0,1]
		int rechargeAvailable= (message2Info.isRechargeAvailable() << 1); //recharge available [0,1]
		int rechargeInProgress= (message2Info.isRechargeInProgress() << 2); //recharge in progress [0,1]
		int pvSystemActive= (message2Info.isPvSystemActive() << 3); //Pv System Active [0,1]
		int auxChargerActive= (message2Info.isAuxChargerActive() << 4); //Aux Charger Active [0,1]
		int storageBatteryContractorStatus= (message2Info.isStorageBatteryContactorStatus() << 5); //Storage Battery Contactor Status [0,1]
		int converterContractorStatus= (message2Info.isConverterContactorStatus() << 6); //Converter Contactor Status [0,1]

		int faultString= message2Info.getFaultString();
		int igbtTemp= message2Info.getIgbtTemp();
		int storageBatteryTemperature= message2Info.getStorageTemp();
		int storageBatterySOC= message2Info.getStorageBatterySoc();

		bMessage[0]= (byte) (faultFlag + 
				rechargeAvailable + 
				rechargeInProgress + 
				pvSystemActive + 
				auxChargerActive + 
				storageBatteryContractorStatus + 
				converterContractorStatus); 


		bMessage[1]= (byte) faultString; //Fault String 
		bMessage[2]= (byte) igbtTemp; //IGBT_temp [°C]
		bMessage[3]= (byte) storageBatteryTemperature; //Storage temp [°C]
		bMessage[4]= (byte) storageBatterySOC; //Storage battery SOC [%]


		sb.append("fault flag: " +   message2Info.isFaultFlag() + ", ");
		sb.append("recharge available: " + message2Info.isRechargeAvailable() + ", ");
		sb.append("recharge in progress: " +  message2Info.isRechargeInProgress() + ", ");
		sb.append("Pv System Active: " +  message2Info.isPvSystemActive() + ", ");
		sb.append("Aux Charger Active: " +   message2Info.isAuxChargerActive() + ", ");
		sb.append("Storage Battery Contactor Status: " + message2Info.isStorageBatteryContactorStatus() + ", ");
		sb.append("Converter Contactor Status: " +  message2Info.isConverterContactorStatus() + ", ");


		sb.append("Fault String  " + bMessage[1] + ", ");
		sb.append("IGBT_temp " +    bMessage[2] + ", ");
		sb.append("Storage temp " +  bMessage[3] + ", ");
		sb.append("Storage battery SOC " + bMessage[4]);

		sb.append(" and id = ");
		sb.append(id);
		s_logger.debug(sb.toString());

		m_canConnection.sendCanMessage(ifName, id, bMessage);
		s_logger.info("Message sent with id: " + id);
	}

	private void sendMessage3(String ifName) throws KuraException, IOException {

		if((m_canConnection==null)) 
			return;
		int id = 0x102;
		StringBuilder sb = new StringBuilder("Trying to send message 3 can frame with message = ");
		byte bCurrentDate[] = new byte[8];

		int vOut= message3Info.getvOut();
		int storageBatteryV= message3Info.getStorageBatteryV();
		int pvSystemV= message3Info.getPvSystemV();
		int iOut= message3Info.getiOut();
		int storageBatteryI= message3Info.getStorageBatteryI();

		if (isBigEndian) {
			bCurrentDate[0]= (byte) ((vOut >> 8) & 0xFF); 
			bCurrentDate[1]= (byte) ((vOut) & 0xFF); //V Out
		}else{
			bCurrentDate[0]= (byte) ((vOut) & 0xFF); //V Out
			bCurrentDate[1]= (byte) ((vOut >> 8) & 0xFF); 
		}

		if (isBigEndian) {
			bCurrentDate[2]= (byte) ((storageBatteryV >> 8) & 0xFF); //Storage_Battery_V [V]
			bCurrentDate[3]= (byte) (storageBatteryV & 0xFF); //Storage_Battery_V [V]
		} else {
			bCurrentDate[2]= (byte) (storageBatteryV & 0xFF); //Storage_Battery_V [V]
			bCurrentDate[3]= (byte) ((storageBatteryV >> 8) & 0xFF); //Storage_Battery_V [V]
		}

		if (isBigEndian) {
			bCurrentDate[4]= (byte) ((pvSystemV >> 8) & 0xFF); //PV_System_V [V]
			bCurrentDate[5]= (byte) (pvSystemV & 0xFF); //PV_System_V [V]
		} else {
			bCurrentDate[4]= (byte) (pvSystemV & 0xFF); //PV_System_V [V]
			bCurrentDate[5]= (byte) ((pvSystemV >> 8) & 0xFF); //PV_System_V [V]
		}

		bCurrentDate[6]= (byte) iOut; //I_Out [A]

		bCurrentDate[7]= (byte) storageBatteryI; //Storage_Battery_I [A]


		if (isBigEndian) {
			sb.append("V Out: " +    buildShort(bCurrentDate[0], bCurrentDate[1]) + ", ");
		}else{
			sb.append("V Out: " +    buildShort(bCurrentDate[1], bCurrentDate[0]) + ", ");
		}

		if (isBigEndian) {
			sb.append("Storage_Battery_V: " +    buildShort(bCurrentDate[2], bCurrentDate[3]) + ", ");
		} else {
			sb.append("Storage_Battery_V: " +    buildShort(bCurrentDate[3], bCurrentDate[2]) + ", ");
		}

		if (isBigEndian) {
			sb.append("PV_System_V: " +    buildShort(bCurrentDate[4], bCurrentDate[5]) + ", ");
		} else {
			sb.append("PV_System_V: " +    buildShort(bCurrentDate[5], bCurrentDate[4]) + ", ");
		}
		sb.append("I_Out: " +     bCurrentDate[6] + ", ");
		sb.append("Storage_Battery_I: " +   bCurrentDate[7]);

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
