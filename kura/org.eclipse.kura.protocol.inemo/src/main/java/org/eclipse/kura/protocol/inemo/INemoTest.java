package org.eclipse.kura.protocol.inemo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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


	private ScheduledExecutorService m_worker;
	private Future<?>           m_handle;

	private Map<String, Object> m_properties;

	// ----------------------------------------------------------------
	//
	//   Dependencies
	//
	// ----------------------------------------------------------------

	public INemoTest(){
		super();
		m_worker = Executors.newSingleThreadScheduledExecutor();
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
		s_logger.info("Activating SECSOC test...");

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

		m_handle.cancel(true);

		// shutting down the worker and cleaning up the properties
		m_worker.shutdownNow();

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
			if (m_handle != null) {
				m_handle.cancel(true);
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

			m_handle = m_worker.submit(new Runnable() {		
				@Override
				public void run() {
					//doSerial();
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

	private void doSerial() {				
		// fetch the publishing configuration from the publishing properties
		String  topic  = (String) m_properties.get(PUBLISH_TOPIC_PROP_NAME);
		Integer qos    = (Integer) m_properties.get(PUBLISH_QOS_PROP_NAME);
		Boolean retain = (Boolean) m_properties.get(PUBLISH_RETAIN_PROP_NAME);

		Boolean echo = (Boolean) m_properties.get(SERIAL_ECHO_PROP_NAME);

		//		if (m_commIs != null) {
		//
		//			try {
		//				int c = -1;
		//				StringBuilder sb = new StringBuilder();
		//
		//				while (m_commIs != null) {
		//
		//					if (m_commIs.available() != 0) {
		//						c = m_commIs.read();
		//					} else {
		//						try {
		//							Thread.sleep(100);
		//							continue;
		//						} catch (InterruptedException e) {
		//							return;
		//						}
		//					}
		//
		//					if (echo && m_commOs != null) {
		//						m_commOs.write((char) c);
		//					}
		//
		//					// on reception of CR, publish the received sentence
		//					if (c==13) {
		//
		//						// Allocate a new payload
		//						KuraPayload payload = new KuraPayload();
		//
		//						// Timestamp the message
		//						payload.setTimestamp(new Date());
		//
		//						payload.addMetric("line", sb.toString());
		//
		//						// Publish the message
		//						try {
		//							m_cloudClient.publish(topic, payload, qos, retain);
		//							s_logger.info("Published to {} message: {}", topic, payload);
		//						} 
		//						catch (Exception e) {
		//							s_logger.error("Cannot publish topic: "+topic, e);
		//						}
		//
		//						sb = new StringBuilder();
		//
		//					} else if (c!=10) {
		//						sb.append((char) c);
		//					}					
		//				}
		//			} catch (IOException e) {
		//				s_logger.error("Cannot read port", e);
		//			} finally {
		//				try {
		//					m_commIs.close();
		//				} catch (IOException e) {
		//					s_logger.error("Cannot close buffered reader", e);
		//				}
		//			}
		//		}
	}
}
