package org.eclipse.kura.protocol.can.arrowhead;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.CloudClient;
import org.eclipse.kura.cloud.CloudClientListener;
import org.eclipse.kura.cloud.CloudService;
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.kura.message.KuraPayload;
import org.eclipse.kura.protocol.can.CanConnectionService;
import org.eclipse.kura.protocol.can.CanMessage;
import org.eclipse.kura.protocol.can.cs.data.CSDataSnapshot;
import org.eclipse.kura.protocol.can.messages.CSMessage1;
import org.eclipse.kura.protocol.can.messages.CSMessage2;
import org.eclipse.kura.protocol.can.messages.CSMessage3;
import org.eclipse.kura.protocol.can.messages.GWMessage1;
import org.eclipse.kura.protocol.can.messages.GWMessage2;
import org.eclipse.kura.protocol.can.messages.GWMessage3;
import org.eclipse.kura.protocol.can.recharge.BookingInfo;
import org.eclipse.kura.protocol.can.recharge.CurrentDateInfo;
import org.eclipse.kura.protocol.can.recharge.RechargeInfo;


public class CanSocketTest implements ConfigurableComponent, CloudClientListener {
	private static final Logger s_logger = LoggerFactory.getLogger(CanSocketTest.class);
	private static final String APP_ID = "can_publisher";

	private static final String THREAD_DELAY= "can.initial.threads.delay";
	private static final String ID_200_FREQUENCY= "can.id200.message.frequency";
	private static final String ID_201_FREQUENCY= "can.id201.message.frequency";
	private static final String ID_202_FREQUENCY= "can.id202.message.frequency";
	private static final String IS_BIG_ENDIAN= "can.bigendian";

	private static final String   PUBLISH_RATE_PROP_NAME   = "publish.rate";

	private CanConnectionService 	m_canConnection;
	private Map<String,Object>   	m_properties;
	private Thread 					m_listenThread;
	private Thread 					m_sendThread1;
	private Thread 					m_sendThread2;
	private Thread 					m_sendThread3;
	private String					m_ifName;

	private CloudService 			m_cloudService;
	private CloudClient      		m_cloudClient;

	private RechargeInfo m_rechargeInfo;
	private BookingInfo m_bookingInfo;
	private CurrentDateInfo m_currentDateInfo;
	private static int threadsDelay;
	private static int id200Freq;
	private static int id201Freq;
	private static int id202Freq;
	private boolean m_isBigEndian= true;

	private volatile boolean senderRunning = true;
	private volatile boolean m_receiverRunning = true;

	private CSDataSnapshot csReceivedData= new CSDataSnapshot();
	private Thread m_publishThread;
	private int m_publishRate;


	// ----------------------------------------------------------------
	//
	//   Dependencies
	//
	// ----------------------------------------------------------------

	public CanSocketTest() {
		super();
	}

	public void setCanConnectionService(CanConnectionService canConnection) {
		this.m_canConnection = canConnection;
	}

	public void unsetCanConnectionService(CanConnectionService canConnection) {
		this.m_canConnection = null;
	}

	public void setCloudService(CloudService cloudService) {
		m_cloudService = cloudService;
	}

	public void unsetCloudService(CloudService cloudService) {
		m_cloudService = null;
	}


	protected void activate(ComponentContext componentContext, Map<String,Object> properties) {
		m_properties = properties;
		s_logger.info("activating Minigateway can test");
		m_ifName="can0";

		if(m_properties!=null){
			if(m_properties.get("can.name") != null){
				m_ifName = (String) m_properties.get("can.name");
			}

			m_rechargeInfo= new RechargeInfo(m_properties);
			m_bookingInfo= new BookingInfo(m_properties);
			m_currentDateInfo= new CurrentDateInfo(m_properties);
			getDelays();
			m_isBigEndian= (Boolean) m_properties.get(IS_BIG_ENDIAN);
			m_publishRate= (Integer) m_properties.get(PUBLISH_RATE_PROP_NAME);
		}

		// get the mqtt client for this application
		try  {

			// Acquire a Cloud Application Client for this Application 
			s_logger.info("Getting CloudClient for {}...", APP_ID);
			m_cloudClient = m_cloudService.newCloudClient(APP_ID);
			m_cloudClient.addCloudClientListener(this);
		}
		catch (Exception e) {
			s_logger.error("Error during component activation", e);
			throw new ComponentException(e);
		}

		//start threads
		startListeningThread();
		startSendThreads();
		startPublishThread();
	}

	protected void deactivate(ComponentContext componentContext) {
		if(m_listenThread!=null){
			m_listenThread.interrupt();
			try {
				m_listenThread.join(100);
				m_receiverRunning= false;
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		m_listenThread=null;

		stopSendThreads();

		// shutting down the worker and cleaning up the properties
		if(m_publishThread!=null){
			m_listenThread.interrupt();
			try {
				m_publishThread.join(100);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		m_publishThread=null;

		// Releasing the CloudApplicationClient
		s_logger.info("Releasing CloudApplicationClient for {}...", APP_ID);
		m_cloudClient.release();
	}

	public void updated(Map<String,Object> properties) {
		s_logger.debug("updated...");		

		m_properties = properties;
		if(m_properties!=null){
			if(m_properties.get("can.name") != null) 
				m_ifName = (String) m_properties.get("can.name");
			m_rechargeInfo= new RechargeInfo(m_properties);
			m_bookingInfo= new BookingInfo(m_properties);
			m_currentDateInfo= new CurrentDateInfo(m_properties);

			getDelays();
			m_isBigEndian= (Boolean) m_properties.get(IS_BIG_ENDIAN);
		}

		stopSendThreads();
		senderRunning= true;
		startSendThreads();

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
				CSMessage1.parseCanMessage(cm, m_isBigEndian, csReceivedData);
			} else if (canId == 0x101) {
				CSMessage2.parseCanMessage(cm, csReceivedData);
			} else if (canId == 0x102) {
				CSMessage3.parseCanMessage(cm, m_isBigEndian, csReceivedData);
			}
		}
		else{
			s_logger.warn("receive=null");
		}		
	}


	// ----------------------------------------------------------------
	//
	//   Cloud Application Callback Methods
	//
	// ----------------------------------------------------------------

	@Override
	public void onControlMessageArrived(String deviceId, String appTopic,
			KuraPayload msg, int qos, boolean retain) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessageArrived(String deviceId, String appTopic,
			KuraPayload msg, int qos, boolean retain) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionLost() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionEstablished() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessageConfirmed(int messageId, String appTopic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessagePublished(int messageId, String appTopic) {
		// TODO Auto-generated method stub

	}

	// ----------------------------------------------------------------
	//
	//   Private Methods
	//
	// ----------------------------------------------------------------

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
	
	private void startPublishThread() {
		m_publishThread = new Thread(new Runnable() {		
			@Override
			public void run() {
				while(m_receiverRunning){
					s_logger.debug("Publish thread sleeping for: " + m_publishRate);
					try {
						Thread.sleep(m_publishRate);
					} catch (InterruptedException e) {
					}
					doPublish();
				}
			}
		});
		m_publishThread.start();
	}

	private void startListeningThread() {
		if(m_listenThread!=null){
			m_listenThread.interrupt();
			try {
				m_listenThread.join(100);
				m_receiverRunning= false;
			} catch (InterruptedException e) {
				// Ignore
			}
			m_listenThread=null;
		}

		m_listenThread = new Thread(new Runnable() {		
			@Override
			public void run() {
				if(m_canConnection!=null){
					while(m_receiverRunning){
						doReceiveTest();
					}
				}
			}
		});
		m_listenThread.start();
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

	private void doSend1Test() {
		try {
			sendMessage1(m_ifName);
		} catch (Exception e) {
			s_logger.warn("CanConnection Crash!");			
			e.printStackTrace();
		}
	}

	private void doSend2Test() {
		try {
			sendMessage2(m_ifName);
		} catch (Exception e) {
			s_logger.warn("CanConnection Crash!");			
			e.printStackTrace();
		}
	}

	private void doSend3Test() {
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
		
		byte bMessage[] = GWMessage1.createMessage(id, m_rechargeInfo);

		m_canConnection.sendCanMessage(ifName, id, bMessage);
		s_logger.info("Message sent with id: " + id);
	}

	private void sendMessage2(String ifName) throws KuraException, IOException {

		if((m_canConnection==null)) 
			return;
		int id = 0x201;
		
		byte bMessage[] = GWMessage2.createMessage(id, m_bookingInfo, m_isBigEndian);

		m_canConnection.sendCanMessage(ifName, id, bMessage);
		s_logger.info("Message sent with id: " + id);
	}

	private void sendMessage3(String ifName) throws KuraException, IOException {

		if((m_canConnection==null)) 
			return;
		int id = 0x202;
		
		byte bMessage[] = GWMessage3.createMessage(id, m_currentDateInfo, m_isBigEndian);
		
		m_canConnection.sendCanMessage(ifName, id, bMessage);
		s_logger.info("Message sent with id: " + id);
	}


	/**
	 * Called at the configured rate to publish the next aggregated measurement.
	 */
	private void doPublish() {				
		// fetch the publishing configuration from the publishing properties
		String  topic  = "csdata";
		Integer qos    = 0;
		Boolean retain = false;

		// Allocate a new payload
		KuraPayload payload = new KuraPayload();

		// Timestamp the message
		payload.setTimestamp(new Date());


		// Add all the needed data to the payload
		payload.addMetric("Power_Out", csReceivedData.getPowerOut());
		payload.addMetric("Minutes_to_Recharge_Estimated", csReceivedData.getMinutesToRecharge());
		payload.addMetric("Seconds_to_Recharge_Estimated",  csReceivedData.getSecondsToRecharge());
		payload.addMetric("Energy_Out",  csReceivedData.getEnergyOut());
		payload.addMetric("Power_PV",  csReceivedData.getPowerPV());

		payload.addMetric("Recharge_Available", csReceivedData.getRechargeAvailable());
		payload.addMetric("Recharge_In_Progress", csReceivedData.getRechargeInProgress());
		payload.addMetric("PV_System_Active",  csReceivedData.getPvSystemActive());
		payload.addMetric("Aux_Charger_Active",  csReceivedData.getAuxChargerActive());
		payload.addMetric("Storage_Battery_Concactor_Sts",  csReceivedData.getStorageBatterySts());
		payload.addMetric("Converter_Contactor_Sts",  csReceivedData.getConverterContactorSts());
		payload.addMetric("IGBT_Temperature",  csReceivedData.getIgbtTemp());
		payload.addMetric("Storage_Battery_Temperature",  csReceivedData.getStorageBatteryTemp());
		payload.addMetric("Storage_Battery_SOC",  csReceivedData.getStorageBatterySOC());

		payload.addMetric("V_Out ", csReceivedData.getvOut());
		payload.addMetric("Storage_Battery_V", csReceivedData.getStorageBatteryV());
		payload.addMetric("PV_System_V",  csReceivedData.getPvSystemV());
		payload.addMetric("I_Out",  csReceivedData.getiOut());
		payload.addMetric("Storage_Battery_I ",  csReceivedData.getStorageBatteryI());


		csReceivedData.resetData();

		// Publish the message
		try {
			m_cloudClient.publish(topic, payload, qos, retain);
			s_logger.info("Published to {} message: {}", topic, payload);
		} 
		catch (Exception e) {
			s_logger.error("Cannot publish topic: "+topic, e);
		}
	}
}
