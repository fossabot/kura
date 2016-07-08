package org.eclipse.kura.protocol.inemo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.CloudClient;
import org.eclipse.kura.cloud.CloudClientListener;
import org.eclipse.kura.cloud.CloudService;
import org.eclipse.kura.comm.CommURI;
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.eclipse.kura.message.KuraPayload;
import org.eclipse.kura.protocol.inemo.comm.INemoConnectionService;
import org.eclipse.kura.protocol.inemo.comm.INemoConnectionServiceImpl;
import org.eclipse.kura.protocol.inemo.comm.SerialInterfaceParameters;
import org.eclipse.kura.protocol.inemo.message.GetDurationMessage;
import org.eclipse.kura.protocol.inemo.message.GetDurationRequestMessage;
import org.eclipse.kura.protocol.inemo.message.GetEventInfoMessage;
import org.eclipse.kura.protocol.inemo.message.GetEventInfoRequestMessage;
import org.eclipse.kura.protocol.inemo.message.GetSnapshotMessage;
import org.eclipse.kura.protocol.inemo.message.GetSnapshotRequestMessage;
import org.eclipse.kura.protocol.inemo.message.GetThresholdMessage;
import org.eclipse.kura.protocol.inemo.message.GetThresholdRequestMessage;
import org.eclipse.kura.protocol.inemo.message.INemoMessage;
import org.eclipse.kura.protocol.inemo.message.PingMessage;
import org.eclipse.kura.protocol.inemo.message.PingRequestMessage;
import org.eclipse.kura.protocol.inemo.message.SetDurationMessage;
import org.eclipse.kura.protocol.inemo.message.SetDurationRequestMessage;
import org.eclipse.kura.protocol.inemo.message.SetThresholdMessage;
import org.eclipse.kura.protocol.inemo.message.SetThresholdRequestMessage;
import org.eclipse.kura.protocol.inemo.message.SyncPollingMessage;
import org.eclipse.kura.protocol.inemo.message.SyncPollingRequestMessage;
import org.eclipse.kura.protocol.inemo.message.TakeSnapshotMessage;
import org.eclipse.kura.protocol.inemo.message.TakeSnapshotRequestMessage;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentException;
import org.osgi.service.io.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class INemoTest implements ConfigurableComponent, CloudClientListener {
	private static final Logger s_logger = LoggerFactory.getLogger(INemoTest.class);

	// Cloud Application identifier
	private static final String APP_ID = "INEMO_TEST";

	// Publishing Property Names
	private static final String   PUBLISH_TOPIC_PROP_NAME  = "publish.semanticTopic";
	private static final String   PUBLISH_QOS_PROP_NAME    = "publish.qos";
	private static final String   PUBLISH_RETAIN_PROP_NAME = "publish.retain";

	private static final String   SERIAL_DEVICE_PROP_NAME= "serial.device";
	private static final String   SERIAL_BAUDRATE_PROP_NAME= "serial.baudrate";
	private static final String   SERIAL_DATA_BITS_PROP_NAME= "serial.data-bits";
	private static final String   SERIAL_PARITY_PROP_NAME= "serial.parity";
	private static final String   SERIAL_STOP_BITS_PROP_NAME= "serial.stop-bits";

	private static final String   SERIAL_ECHO_PROP_NAME= "serial.echo";
	private static final String   SERIAL_CLOUD_ECHO_PROP_NAME= "serial.cloud-echo";


	private CloudService m_cloudService;
	private CloudClient m_cloudClient;

	private INemoConnectionService m_connService;

	private ConnectionFactory m_connectionFactory;


	private ScheduledExecutorService m_writerWorker;
	private ScheduledExecutorService m_listenerWorker;
	private Future<?>           m_writerHandle;
	private Future<?>           m_listenerHandle;

	private Map<String, Object> m_properties;

	private int m_lastEventIndex = -1;

	// ----------------------------------------------------------------
	//
	//   Dependencies
	//
	// ----------------------------------------------------------------

	public INemoTest(){
		super();
		m_writerWorker = Executors.newSingleThreadScheduledExecutor();
		m_listenerWorker = Executors.newSingleThreadScheduledExecutor();
	}

	public void setCloudService(CloudService cloudService) {
		m_cloudService = cloudService;
	}

	public void unsetCloudService(CloudService cloudService) {
		m_cloudService = null;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.m_connectionFactory = connectionFactory;
	}

	public void unsetConnectionFactory(ConnectionFactory connectionFactory) {
		this.m_connectionFactory = null;
	}

	// ----------------------------------------------------------------
	//
	//   Activation APIs
	//
	// ----------------------------------------------------------------

	protected void activate(ComponentContext componentContext, Map<String,Object> properties) {
		s_logger.info("Activating iNemo test...");

		m_properties = new HashMap<String, Object>();

		// get the mqtt client for this application
		try  {

			// Acquire a Cloud Application Client for this Application 
			s_logger.info("Getting CloudApplicationClient for {}...", APP_ID);
			m_cloudClient = m_cloudService.newCloudClient(APP_ID);
			m_cloudClient.addCloudClientListener(this);

			// Don't subscribe because these are handled by the default 
			// subscriptions and we don't want to get messages twice			
			doUpdate(properties);
		}
		catch (Exception e) {
			s_logger.error("Error during component activation", e);
			throw new ComponentException(e);
		}
		s_logger.info("Activating INEMO test... Done.");
	}

	protected void deactivate(ComponentContext componentContext){
		s_logger.info("Deactivating INEMO test...");

		m_listenerHandle.cancel(true);
		m_writerHandle.cancel(true);

		// shutting down the worker and cleaning up the properties
		m_writerWorker.shutdownNow();
		m_listenerWorker.shutdownNow();

		// Releasing the CloudApplicationClient
		s_logger.info("Releasing CloudApplicationClient for {}...", APP_ID);
		m_cloudClient.release();

		closePort();

		s_logger.info("Deactivating INEMO test... Done.");
	}	

	public void updated(Map<String,Object> properties){
		s_logger.info("Updated INEMO test...");

		// try to kick off a new job
		doUpdate(properties);
		s_logger.info("Updated INEMO test... Done.");
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

	/**
	 * Called after a new set of properties has been configured on the service
	 */
	private void doUpdate(Map<String, Object> properties) {
		try {

			for (String s : properties.keySet()) {
				s_logger.info("Update - "+s+": "+properties.get(s));
			}

			// cancel a current worker handle if one if active
			if (m_listenerHandle != null) {
				m_listenerHandle.cancel(true);
			}
			if (m_writerHandle != null) {
				m_writerHandle.cancel(true);
			}

			String topic = (String) m_properties.get(PUBLISH_TOPIC_PROP_NAME);
			if (topic != null) {
				try {
					m_cloudClient.unsubscribe(topic);
				} catch (KuraException e) {
					s_logger.error("Unsubscribe failed", e);
				}
			}

			m_connService= new INemoConnectionServiceImpl();
			closePort();

			m_properties.clear();
			m_properties.putAll(properties);

			openPort();

			Boolean cloudEcho = (Boolean) m_properties.get(SERIAL_CLOUD_ECHO_PROP_NAME);
			if (cloudEcho) {
				try {
					m_cloudClient.subscribe(topic, 0);
				} catch (KuraException e) {
					s_logger.error("Subscribe failed", e);
				}
			}

			m_writerHandle = m_writerWorker.submit(new Runnable() {		
				@Override
				public void run() {
					try {
						doCalibration();
						
						while (true) {
							doEventPoll();
							Thread.sleep(1000);
						}
					} catch (KuraException e) {
						// TODO Auto-generated catch block
						s_logger.warn("Exception while performing calibration: ", e);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} catch (Throwable t) {
			s_logger.error("Unexpected Throwable", t);
		}
	}

	private void openPort() {
		String port = (String) m_properties.get(SERIAL_DEVICE_PROP_NAME);

		if (port == null) {
			s_logger.info("Port name not configured");
			return;
		}

		int baudRate = Integer.valueOf((String) m_properties.get(SERIAL_BAUDRATE_PROP_NAME));
		int dataBits = Integer.valueOf((String) m_properties.get(SERIAL_DATA_BITS_PROP_NAME)); 
		int stopBits = Integer.valueOf((String) m_properties.get(SERIAL_STOP_BITS_PROP_NAME));

		String sParity = (String) m_properties.get(SERIAL_PARITY_PROP_NAME);

		int parity = CommURI.PARITY_NONE;
		if (sParity.equals("none")) {
			parity = CommURI.PARITY_NONE;
		} else if (sParity.equals("odd")) {
			parity = CommURI.PARITY_ODD;	
		} else if (sParity.equals("even")) {
			parity = CommURI.PARITY_EVEN;
		}

		int timeout= 2000;

		SerialInterfaceParameters sip= new SerialInterfaceParameters();
		sip.setDevice(port);
		sip.setBaudrate(baudRate);
		sip.setDataBits(dataBits);
		sip.setStopBits(stopBits);
		sip.setParity(parity);
		sip.setTimeout(timeout);

		try {
			m_connService.openConnection(sip, m_connectionFactory);

			s_logger.info(port+" open");
		} catch (IOException e) {
			s_logger.error("Failed to open port", e);
			cleanupPort();
		}
	}

	private void cleanupPort() {
		try {
			s_logger.info("Closing connection...");
			m_connService.closeConnection();
			s_logger.info("Closed connection");
		} catch (IOException e) {
			s_logger.error("Cannot close connection", e);
		}
	}

	private void closePort() {
		cleanupPort();
	}

	private void doCalibration() throws KuraException {				
		s_logger.debug("Before ping...");
		PingRequestMessage pingRequestMessage= new PingRequestMessage();
		sendMessage(pingRequestMessage);
		receiveMessage();
		s_logger.debug("After ping.");
		
		s_logger.debug("Before takeSnapshotRequestMessage...");
		TakeSnapshotRequestMessage takeSnapshotRequestMessage= new TakeSnapshotRequestMessage();
		sendMessage(takeSnapshotRequestMessage);
		receiveMessage();
		s_logger.debug("After takeSnapshotRequestMessage.");
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		s_logger.debug("Before getSnapshotRequestMessage...");
		GetSnapshotRequestMessage getSnapshotRequestMessage= new GetSnapshotRequestMessage();
		sendMessage(getSnapshotRequestMessage);
		receiveMessage();
		s_logger.debug("After getSnapshotRequestMessage.");
		
		s_logger.debug("Before setThresholdRequestMessage...");
		SetThresholdRequestMessage setThresholdRequestMessage= new SetThresholdRequestMessage();
		sendMessage(setThresholdRequestMessage);
		receiveMessage();
		s_logger.debug("After setThresholdRequestMessage.");
		
		s_logger.debug("Before getThresholdRequestMessage...");
		GetThresholdRequestMessage getThresholdRequestMessage= new GetThresholdRequestMessage();
		sendMessage(getThresholdRequestMessage);
		receiveMessage();
		s_logger.debug("After getThresholdRequestMessage.");
		
		s_logger.debug("Before setDurationRequestMessage...");
		SetDurationRequestMessage setDurationRequestMessage= new SetDurationRequestMessage();
		sendMessage(setDurationRequestMessage);
		receiveMessage();
		s_logger.debug("After setDurationRequestMessage.");
		
		s_logger.debug("Before getDurationRequestMessage...");
		GetDurationRequestMessage getDurationRequestMessage= new GetDurationRequestMessage();
		sendMessage(getDurationRequestMessage);
		receiveMessage();
		s_logger.debug("After getDurationRequestMessage.");
	}

	private void sendMessage(INemoMessage iNemoMessage) {
		try {
			m_connService.sendMessage(iNemoMessage.getMessageAsByteArray());
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void receiveMessage() throws KuraException {
		Callable<INemoMessage> callable = new Callable<INemoMessage>() {
	        @Override
	        public INemoMessage call() {
	            try {
					return m_connService.receiveMessage();
				} catch (KuraException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
	        }
	    };
		m_listenerHandle = m_listenerWorker.submit(callable);
		try {
			INemoMessage result= (INemoMessage) m_listenerHandle.get();
			messageCallback(result);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void messageCallback(INemoMessage message) throws KuraException {
		if (message instanceof PingMessage) {
			PingMessage pingMessage= (PingMessage) message;
			s_logger.info("Received ping: {}", pingMessage.getStatus());
		} else if (message instanceof TakeSnapshotMessage) {
			TakeSnapshotMessage tempMessage = (TakeSnapshotMessage) message;
			s_logger.info("Received takeSnapshotMessage response: {}", tempMessage.getStatus());
		} else if (message instanceof GetSnapshotMessage) {
			GetSnapshotMessage tempMessage = (GetSnapshotMessage) message;
			s_logger.info("Received GetSnapshotRequestMessage response: {}", tempMessage.getStatus());
		} else if (message instanceof SetThresholdMessage) {
			SetThresholdMessage tempMessage = (SetThresholdMessage) message;
			s_logger.info("Received SetThresholdRequestMessage response: {}", tempMessage.getStatus());
		} else if (message instanceof GetThresholdMessage) {
			GetThresholdMessage tempMessage = (GetThresholdMessage) message;
			s_logger.info("Received GetThresholdRequestMessage response: {}", tempMessage.getStatus());
		} else if (message instanceof SetDurationMessage) {
			SetDurationMessage tempMessage = (SetDurationMessage) message;
			s_logger.info("Received SetDurationRequestMessage response: {}", tempMessage.getStatus());
		} else if (message instanceof GetDurationMessage) {
			GetDurationMessage tempMessage = (GetDurationMessage) message;
			s_logger.info("Received GetDurationRequestMessage response: {}", tempMessage.getStatus());
		} else if (message instanceof SyncPollingMessage) {
			SyncPollingMessage tempMessage = (SyncPollingMessage) message;
			s_logger.info("Received SyncPollingMessage response: {}", tempMessage.getStatus());
			if (m_lastEventIndex == -1) {
				m_lastEventIndex = tempMessage.getEventIndex();
			} else if (m_lastEventIndex != tempMessage.getEventIndex()) {
				m_lastEventIndex= tempMessage.getEventIndex();
				crashDetected();
			}
		} else if (message instanceof GetEventInfoMessage) {
			GetEventInfoMessage tempMessage = (GetEventInfoMessage) message;
			s_logger.info("Received GetEventInfoMessage response: {}", tempMessage.getStatus());
			
			s_logger.info("Received GetEventInfoMessage threshold: {}", tempMessage.getThreshold());
			s_logger.info("Received GetEventInfoMessage xmax: {}", tempMessage.getXMax());
			s_logger.info("Received GetEventInfoMessage ymax: {}", tempMessage.getYMax());
			s_logger.info("Received GetEventInfoMessage zmax: {}", tempMessage.getZMax());
			s_logger.info("Received GetEventInfoMessage xave: {}", tempMessage.getXAve());
			s_logger.info("Received GetEventInfoMessage yave: {}", tempMessage.getYAve());
			s_logger.info("Received GetEventInfoMessage zave: {}", tempMessage.getZAve());
			s_logger.info("Received GetEventInfoMessage range: {}", tempMessage.getRange());
			s_logger.info("Received GetEventInfoMessage xsnap: {}", tempMessage.getXSnap());
			s_logger.info("Received GetEventInfoMessage ysnap: {}", tempMessage.getYSnap());
			s_logger.info("Received GetEventInfoMessage zsnap: {}", tempMessage.getZSnap());
			s_logger.info("Received GetEventInfoMessage phi: {}", tempMessage.getPhi());
			s_logger.info("Received GetEventInfoMessage psi: {}", tempMessage.getPsi());
			
		}
	}

	private void doEventPoll() throws KuraException {
		SyncPollingRequestMessage syncPollingRequestMessage= new SyncPollingRequestMessage();
		sendMessage(syncPollingRequestMessage);
		receiveMessage();
	}
	
	private void crashDetected() throws KuraException { //to check the thread that executes this
		s_logger.info("Crash detected!");
		GetEventInfoRequestMessage getEventInfoRequestMessage= new GetEventInfoRequestMessage(m_lastEventIndex);
		sendMessage(getEventInfoRequestMessage);
		receiveMessage();
	}
}
