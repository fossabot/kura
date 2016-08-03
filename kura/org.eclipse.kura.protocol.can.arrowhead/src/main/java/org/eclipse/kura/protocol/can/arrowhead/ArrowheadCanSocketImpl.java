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
import org.eclipse.kura.protocol.can.arrowhead.control.ControlMessage;
import org.eclipse.kura.protocol.can.arrowhead.control.ControlMessage.InvalidControlMessageException;
import org.eclipse.kura.protocol.can.arrowhead.control.ControlMessageCodes;
import org.eclipse.kura.protocol.can.arrowhead.control.T312RechargeRequestMessage;
import org.eclipse.kura.protocol.can.cs.data.MotoTronDataSnapshot;
import org.eclipse.kura.protocol.can.cs.data.PrivateCSDataSnapshot;
import org.eclipse.kura.protocol.can.cs.data.PublicCSDataSnapshot;
import org.eclipse.kura.protocol.can.messages.CSMessage0x100;
import org.eclipse.kura.protocol.can.messages.CSMessage0x101;
import org.eclipse.kura.protocol.can.messages.CSMessage0x102;
import org.eclipse.kura.protocol.can.messages.CSMessage0x300;
import org.eclipse.kura.protocol.can.messages.CSMessage0x301;
import org.eclipse.kura.protocol.can.messages.CSMessage0x302;
import org.eclipse.kura.protocol.can.messages.CSMessage0x401;
import org.eclipse.kura.protocol.can.messages.CSMessage0x402;
import org.eclipse.kura.protocol.can.messages.GWMessage0x200;
import org.eclipse.kura.protocol.can.messages.GWMessage0x201;
import org.eclipse.kura.protocol.can.messages.GWMessage0x202;
import org.eclipse.kura.protocol.can.messages.GWMessage0x400;
import org.eclipse.kura.protocol.can.recharge.BookingInfo;
import org.eclipse.kura.protocol.can.recharge.CurrentDateInfo;
import org.eclipse.kura.protocol.can.recharge.RechargeInfo;

/**
 * Main Class. Contains the OSGi entry points for DS and for component
 * activation, deactivation and update. Furthermore, it also contains all the
 * logic for CAN data publishing/fetching and for cloud publishing.
 *
 */
public class ArrowheadCanSocketImpl implements ConfigurableComponent, CloudClientListener {
    private static final Logger s_logger = LoggerFactory.getLogger(ArrowheadCanSocketImpl.class);
    private static final String APP_ID   = "can_publisher";

    private static final String THREAD_DELAY = "can.initial.threads.delay";
    private static final String ID_200_FREQUENCY =
    "can.id200.message.frequency";
    private static final String ID_201_FREQUENCY =
    "can.id201.message.frequency";
    private static final String ID_202_FREQUENCY =
    "can.id202.message.frequency";
    private static final String IS_BIG_ENDIAN = "can.bigendian";
    private static final String MODALITY      = "arrowhead.modality";
    private static final String MODALITY_T311 = "t3.1.1";
    private static final String MODALITY_T312 = "t3.1.2";
    private static final String MODALITY_T32  = "t3.2";
    private static final String ID_OTG        = "arrowhead.t32.idotg";
    private static final String EVSE_ID       = "arrowhead.evse.id";

    private static final String PUBLISH_RATE_PROP_NAME = "publish.rate";
    private static final String CONTROL_TOPIC_NAME     = "control";

    private CanConnectionService canConnection;
    private Map<String, Object>  classProperties;
    private Thread               listenThread;
    private Thread sendThread1;
    private Thread sendThread2;
    private Thread sendThread3;
    private String               ifName;
    private String               chosenModality;
    private String               idOtg;
    private String				 EVSEid;
    
    private CloudService cloudService;
    private CloudClient  cloudClient;

    private RechargeInfo    rechargeInfo;
    private BookingInfo     bookingInfo;
    private CurrentDateInfo currentDateInfo;
    private int threadsDelay;
    private int id200Freq;
    private int id201Freq;
    private int id202Freq;
    private boolean         isBigEndian = true;

    private volatile boolean senderRunning = true;
    private volatile boolean receiverRunning = true;

    private PublicCSDataSnapshot  publicCSReceivedData;
    private PrivateCSDataSnapshot privateCSReceivedData;
    private MotoTronDataSnapshot  motoTronReceivedData;

    private Thread publishThread;
    private int    publishRate;

    private ApplicationLogic applicationLogic;

    // ----------------------------------------------------------------
    //
    // Dependencies
    //
    // ----------------------------------------------------------------

    public void setCanConnectionService(CanConnectionService canConnection) {
        this.canConnection = canConnection;
    }

    public void unsetCanConnectionService(CanConnectionService canConnection) {
        this.canConnection = null;
    }

    public void setCloudService(CloudService cloudService) {
        this.cloudService = cloudService;
    }

    public void unsetCloudService(CloudService cloudService) {
        this.cloudService = null;
    }

    protected void setStartRechargeFlag(int value) {
        this.rechargeInfo.setStartRecharge(value);
    }

    protected String getEVSEId() {
    	return EVSEid;
    }
    
    protected void setBookingTime(int day, int month, int year, int hour, int minutes) {
    	bookingInfo.setBookingDateDay(day);
    	bookingInfo.setBookingDateMonth(month);
    	bookingInfo.setBookingDateYear(year);
    	bookingInfo.setCurrentTimeHour(hour);
    	bookingInfo.setCurrentTimeMinute(minutes);
    }
    
    private void subscribeToControlTopic() throws KuraException {
        if (cloudClient.isConnected())
            cloudClient.subscribe(CONTROL_TOPIC_NAME, 1);
        s_logger.info("subscribed to control topic");
    }

    protected interface ApplicationLogic {

        public void onPrivateCSMessage(int code, PrivateCSDataSnapshot data);

        public void onPublicCSMessage(int code, PublicCSDataSnapshot snapshot);

        public void onMotoTronCSMessage(int code, MotoTronDataSnapshot snapshot);

        public void onControlMessage(ControlMessage message);
        
        public void onShutdown();

    }

    public void activate(ComponentContext componentContext, Map<String, Object> properties) {
        classProperties = properties;
        s_logger.info("activating Minigateway can test");
        ifName = "can0";

        if (classProperties != null) {
            if (classProperties.get("can.name") != null) {
                ifName = (String) classProperties.get("can.name");
            }

            chosenModality = (String) classProperties.get(MODALITY);

            rechargeInfo = new RechargeInfo(classProperties);
            bookingInfo = new BookingInfo(classProperties);
            currentDateInfo = new CurrentDateInfo(classProperties);
            getDelays();
            isBigEndian = (Boolean) classProperties.get(IS_BIG_ENDIAN);
            publishRate = ((Integer) classProperties.get(PUBLISH_RATE_PROP_NAME)) * 1000;
            idOtg = (String) classProperties.get(ID_OTG);
            EVSEid = (String) classProperties.get(EVSE_ID);
        }

        publicCSReceivedData = new PublicCSDataSnapshot();
        privateCSReceivedData = new PrivateCSDataSnapshot();
        motoTronReceivedData = new MotoTronDataSnapshot(idOtg);

        // get the mqtt client for this application
        try {

            // Acquire a Cloud Application Client for this Application
            s_logger.info("Getting CloudClient for {}...", APP_ID);
            cloudClient = cloudService.newCloudClient(APP_ID);
            cloudClient.addCloudClientListener(this);
            subscribeToControlTopic();
        } catch (Exception e) {
            s_logger.error("Error during component activation", e);
            throw new ComponentException(e);
        }

        try {
            if (chosenModality.equals(MODALITY_T312))
                this.applicationLogic = new T312ApplicationLogic(this);
        } catch (KuraException e) {
            s_logger.error("Failed to instantiate application logic: " + e.getMessage());
        }
        


        // start threads
        startListeningThread();
        startSendThreads();
        startPublishThread();
    }

    public void deactivate(ComponentContext componentContext) {
        stopListenThread();

        stopSendThreads();

        stopPublishThread();

        // Releasing the CloudApplicationClient
        s_logger.info("Releasing CloudApplicationClient for {}...", APP_ID);
        cloudClient.release();
        
        if (applicationLogic != null) {
        	applicationLogic.onShutdown();
        }
    }

    public void updated(Map<String, Object> properties) {
        s_logger.debug("updated...");

        classProperties = properties;
        if (classProperties != null) {
            if (classProperties.get("can.name") != null) {
                ifName = (String) classProperties.get("can.name");
            }

            chosenModality = (String) classProperties.get(MODALITY);

            rechargeInfo = new RechargeInfo(classProperties);
            bookingInfo = new BookingInfo(classProperties);
            currentDateInfo = new CurrentDateInfo(classProperties);

            getDelays();
            isBigEndian = (Boolean) classProperties.get(IS_BIG_ENDIAN);
            publishRate = ((Integer) classProperties.get(PUBLISH_RATE_PROP_NAME)) * 1000;
            idOtg = (String) classProperties.get(ID_OTG);
            
            EVSEid = (String) classProperties.get(EVSE_ID);
        }

        stopListenThread();

        stopSendThreads();

        stopPublishThread();
        senderRunning = true;

        startListeningThread();
        startSendThreads();
        startPublishThread();
    }

    private void doReceive() {
        CanMessage cm = null;
        s_logger.debug("Waiting for a request");
        try {
            cm = canConnection.receiveCanMessage(-1, 0x7FF);
        } catch (KuraException e) {
            s_logger.warn("CanConnection Crash! -> KuraException", e);
        } catch (IOException e) {
            s_logger.warn("CanConnection Crash! -> IOException", e);
        }

        if (cm != null) {
            int canId = cm.getCanId();
             s_logger.info("Received can message with Id: 0x" + Integer.toHexString(canId));

            if (applicationLogic == null) {
                return;
            }

            if (canId == 0x100) {
                CSMessage0x100.parseCanMessage(cm, isBigEndian, publicCSReceivedData);
            } else if (canId == 0x101) {
                CSMessage0x101.parseCanMessage(cm, publicCSReceivedData);
                applicationLogic.onPublicCSMessage(canId, publicCSReceivedData);
            } else if (canId == 0x102) {
                CSMessage0x102.parseCanMessage(cm, isBigEndian, publicCSReceivedData);
            } else if (canId == 0x300) {
                CSMessage0x300.parseCanMessage(cm, isBigEndian, privateCSReceivedData);
                applicationLogic.onPrivateCSMessage(canId, privateCSReceivedData);
            } else if (canId == 0x301) {
                CSMessage0x301.parseCanMessage(cm, privateCSReceivedData);
            } else if (canId == 0x302) {
                CSMessage0x302.parseCanMessage(cm, isBigEndian, privateCSReceivedData);
            } else if (canId == 0x401) {
                CSMessage0x401.parseCanMessage(cm, motoTronReceivedData);
            } else if (canId == 0x402) {
                CSMessage0x402.parseCanMessage(cm, motoTronReceivedData);

            }

            if (motoTronReceivedData.getVehiclePlate() != null) {
                applicationLogic.onMotoTronCSMessage(canId, motoTronReceivedData);
            }
        } else {
            s_logger.warn("receive=null");
        }
    }

    // ----------------------------------------------------------------
    //
    // Cloud Application Callback Methods
    //
    // ----------------------------------------------------------------

    @Override
    public void onControlMessageArrived(String deviceId, String appTopic,
            KuraPayload msg, int qos, boolean retain) {
        return;
    }

    @Override
    public void onMessageArrived(String deviceId, String appTopic,
            KuraPayload msg, int qos, boolean retain) {

        s_logger.info("control message received on topic: " + appTopic);

        if (!appTopic.equals(CONTROL_TOPIC_NAME)) {
            return;
        }

        try {

            int messageType = (Integer) msg.getMetric(ControlMessageCodes.MESSAGE_TYPE_METRIC_NAME);

            if ((messageType & ControlMessageCodes.T312_RECHARGE_REQUEST_MASK) != 0) {
                T312RechargeRequestMessage message = new T312RechargeRequestMessage(messageType, msg);
                if (applicationLogic != null)
                    applicationLogic.onControlMessage(message);
            }

        } catch (Exception e) {
            s_logger.info("Received invalid control message, ", e);
            s_logger.info(msg.toString());
        } catch (InvalidControlMessageException e) {
            s_logger.info("Message exception!", e);
        }
    }

    @Override
    public void onConnectionLost() {
        try {
            cloudClient.unsubscribe(CONTROL_TOPIC_NAME);
        } catch (KuraException e) {
            s_logger.info("failed to unsubscribe to control topic", e);
        }
    }

    @Override
    public void onConnectionEstablished() {
        try {
            subscribeToControlTopic();
        } catch (KuraException e) {
            s_logger.error("Failed to subscribe to control topic", e);
        }
    }

    @Override
    public void onMessageConfirmed(int messageId, String appTopic) {
        return;
    }

    @Override
    public void onMessagePublished(int messageId, String appTopic) {
        return;
    }

    // ----------------------------------------------------------------
    //
    // Private Methods
    //
    // ----------------------------------------------------------------

     private void getDelays() {
     threadsDelay = Integer.parseInt((String)
     classProperties.get(THREAD_DELAY));
     id200Freq = Integer.parseInt((String)
     classProperties.get(ID_200_FREQUENCY));
     id201Freq = Integer.parseInt((String)
     classProperties.get(ID_201_FREQUENCY));
     id202Freq = Integer.parseInt((String)
     classProperties.get(ID_202_FREQUENCY));
    
     }
    
     private void startSendThreads() {
     if (MODALITY_T311.equals(chosenModality)) {
     return;
     }
    
     if (sendThread1 != null) {
     sendThread1.interrupt();
     try {
     sendThread1.join(100);
     senderRunning = false;
     } catch (InterruptedException e) {
     // Ignore
     }
     sendThread1 = null;
     }
    
     sendThread1 = new Thread(new Runnable() {
     @Override
     public void run() {
     try {
     Thread.sleep(threadsDelay * 0);
     } catch (InterruptedException e) {}
     if (canConnection != null) {
     while (senderRunning) {
     doSend1Test();
     s_logger.debug("Thread1 sleeping for: " + id200Freq);
     try {
     Thread.sleep(id200Freq);
     } catch (InterruptedException e) {}
     }
     }
     }
     });
     sendThread1.start();
    
     if (sendThread2 != null) {
     sendThread2.interrupt();
     try {
     sendThread2.join(100);
     senderRunning = false;
     } catch (InterruptedException e) {
     // Ignore
     }
     sendThread2 = null;
     }
    
     sendThread2 = new Thread(new Runnable() {
     @Override
     public void run() {
     try {
     Thread.sleep(threadsDelay * 1);
     } catch (InterruptedException e) {}
     if (canConnection != null) {
     while (senderRunning) {
     doSend2Test();
     s_logger.debug("Thread2 sleeping for: " + id201Freq);
     try {
     Thread.sleep(id201Freq);
     } catch (InterruptedException e) {}
     }
     }
     }
     });
     sendThread2.start();
    
     if (sendThread3 != null) {
     sendThread3.interrupt();
     try {
     sendThread3.join(100);
     senderRunning = false;
     } catch (InterruptedException e) {
     // Ignore
     }
     sendThread3 = null;
     }
    
     sendThread3 = new Thread(new Runnable() {
     @Override
     public void run() {
     try {
     Thread.sleep(threadsDelay * 2);
     } catch (InterruptedException e) {}
     if (canConnection != null) {
     while (senderRunning) {
     doSend3Test();
     s_logger.debug("Thread3 sleeping for: " + id202Freq);
     try {
     Thread.sleep(id202Freq);
     } catch (InterruptedException e) {}
     }
     }
     }
     });
     sendThread3.start();
     }

    private void startPublishThread() {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (receiverRunning) {
                    s_logger.debug("Publish thread sleeping for: " + publishRate);
                    try {
                        Thread.sleep(publishRate);
                    } catch (InterruptedException e) {}
                    doPublish();
                }
            }
        });
        publishThread.start();
    }

    private void stopPublishThread() {
        // shutting down the worker and cleaning up the properties
        if (publishThread != null) {
            publishThread.interrupt();
            try {
                publishThread.join(100);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        publishThread = null;
    }

    private void stopListenThread() {
        if (listenThread != null) {
            listenThread.interrupt();
            try {
                listenThread.join(100);
                receiverRunning = false;
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        listenThread = null;
    }

    private void startListeningThread() {
        if (listenThread != null) {
            listenThread.interrupt();
            try {
                listenThread.join(100);
                receiverRunning = false;
            } catch (InterruptedException e) {
                // Ignore
            }
            listenThread = null;
        }

        listenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (canConnection != null) {
                    receiverRunning = true;
                    while (receiverRunning) {
                        doReceive();
                    }
                }
            }
        });
        listenThread.start();
    }

     private void stopSendThreads() {
     if (sendThread1 != null) {
     sendThread1.interrupt();
     try {
     sendThread1.join(100);
     senderRunning = false;
     } catch (InterruptedException e) {
     // Ignore
     }
     }
     sendThread1 = null;
    
     if (sendThread2 != null) {
     sendThread2.interrupt();
     try {
     sendThread2.join(100);
     senderRunning = false;
     } catch (InterruptedException e) {
     // Ignore
     }
     }
     sendThread2 = null;
    
     if (sendThread3 != null) {
     sendThread3.interrupt();
     try {
     sendThread3.join(100);
     senderRunning = false;
     } catch (InterruptedException e) {
     // Ignore
     }
     }
     sendThread3 = null;
     }

    private void doSend1Test() {
        try {
            if (MODALITY_T312.equals(chosenModality)) {
                sendMessage0x200();
            }

            if (MODALITY_T32.equals(chosenModality) && motoTronReceivedData.getVehiclePlate() != null) {
                sendMessage0x400();
            }
        } catch (Exception e) {
            s_logger.warn("CanConnection Crash!", e);
        }
    }

    private void doSend2Test() {
        try {
            if (MODALITY_T312.equals(chosenModality) || MODALITY_T32.equals(chosenModality)) {
                sendMessage0x201();
            }
        } catch (Exception e) {
            s_logger.warn("CanConnection Crash!", e);
        }
    }

    private void doSend3Test() {
        try {
            if (MODALITY_T312.equals(chosenModality) || MODALITY_T32.equals(chosenModality)) {
                sendMessage0x202();
            }
        } catch (Exception e) {
            s_logger.warn("CanConnection Crash!", e);
        }
    }

    private void sendMessage0x200() throws KuraException, IOException {

        if (canConnection == null) {
            return;
        }

        byte[] bMessage = GWMessage0x200.createMessage(rechargeInfo);

        canConnection.sendCanMessage(ifName, GWMessage0x200.getId(), bMessage);
        // s_logger.info("Message sent with id: 0x" + Integer.toHexString(GWMessage0x200.getId()));
    }

    private void sendMessage0x201() throws KuraException, IOException {

        if (canConnection == null) {
            return;
        }

        byte[] bMessage = GWMessage0x201.createMessage(bookingInfo, isBigEndian);

        canConnection.sendCanMessage(ifName, GWMessage0x201.getId(), bMessage);
        // s_logger.info("Message sent with id: 0x" + Integer.toHexString(GWMessage0x201.getId()));
    }

    private void sendMessage0x202() throws KuraException, IOException {

        if (canConnection == null) {
            return;
        }

        byte[] bMessage = GWMessage0x202.createMessage(currentDateInfo, isBigEndian);

        canConnection.sendCanMessage(ifName, GWMessage0x202.getId(), bMessage);
        // s_logger.info("Message sent with id: 0x" + Integer.toHexString(GWMessage0x202.getId()));
    }

    private void sendMessage0x400() throws KuraException, IOException {
        if (canConnection == null) {
            return;
        }

        byte[] bMessage = GWMessage0x400.createMessage(motoTronReceivedData);

        canConnection.sendCanMessage(ifName, GWMessage0x400.getId(), bMessage);
        // s_logger.info("Message sent with id: 0x" + Integer.toHexString(GWMessage0x400.getId()));
    }

    /**
     * Called at the configured rate to publish the next aggregated measurement.
     */
    private void doPublish() {
        String topic = null;
        Integer qos = 0;
        Boolean retain = false;
        KuraPayload payload = new KuraPayload();

        if (MODALITY_T312.equals(chosenModality)) {
            // fetch the publishing configuration from the publishing properties
            topic = "publiccsdata";
            qos = 0;
            retain = false;

            // Timestamp the message
            payload.setTimestamp(new Date());

            // Add all the needed data to the payload
            payload.addMetric("Power_Out", publicCSReceivedData.getPowerOut());
            payload.addMetric("Minutes_to_Recharge_Estimated", publicCSReceivedData.getMinutesToRecharge());
            payload.addMetric("Seconds_to_Recharge_Estimated", publicCSReceivedData.getSecondsToRecharge());
            payload.addMetric("Energy_Out", publicCSReceivedData.getEnergyOut());
            payload.addMetric("Power_PV", publicCSReceivedData.getPowerPV());

            payload.addMetric("Recharge_Available", publicCSReceivedData.getRechargeAvailable());
            payload.addMetric("Recharge_In_Progress", publicCSReceivedData.getRechargeInProgress());
            payload.addMetric("PV_System_Active", publicCSReceivedData.getPvSystemActive());
            payload.addMetric("Aux_Charger_Active", publicCSReceivedData.getAuxChargerActive());
            payload.addMetric("Storage_Battery_Concactor_Sts", publicCSReceivedData.getStorageBatterySts());
            payload.addMetric("Converter_Contactor_Sts", publicCSReceivedData.getConverterContactorSts());
            payload.addMetric("IGBT_Temperature", publicCSReceivedData.getIgbtTemp());
            payload.addMetric("Storage_Battery_Temperature", publicCSReceivedData.getStorageBatteryTemp());
            payload.addMetric("Storage_Battery_SOC", publicCSReceivedData.getStorageBatterySOC());
            payload.addMetric("Status_of_Storage_Battery_charger", publicCSReceivedData.getStorageBatteryChargerStatus());
            
            payload.addMetric("V_Out ", publicCSReceivedData.getvOut());
            payload.addMetric("Storage_Battery_V", publicCSReceivedData.getStorageBatteryV());
            payload.addMetric("PV_System_V", publicCSReceivedData.getPvSystemV());
            payload.addMetric("I_Out", publicCSReceivedData.getiOut());
            payload.addMetric("Storage_Battery_I ", publicCSReceivedData.getStorageBatteryI());

        } else if (MODALITY_T311.equals(chosenModality)) {
            // fetch the publishing configuration from the publishing properties
            topic = "privatecsdata";
            qos = 0;
            retain = false;

            // Timestamp the message
            payload.setTimestamp(new Date());

            // Add all the needed data to the payload
            payload.addMetric("Power_Out", privateCSReceivedData.getPowerOut());
            payload.addMetric("Hours_to_Recharge_Estimated", privateCSReceivedData.getHoursToRecharge());
            payload.addMetric("Minutes_to_Recharge_Estimated", privateCSReceivedData.getMinutesToRecharge());
            payload.addMetric("Energy_Out", privateCSReceivedData.getEnergyOut());
            payload.addMetric("Power_PV", privateCSReceivedData.getPowerPV());

            payload.addMetric("Recharge_Available", privateCSReceivedData.getRechargeAvailable());
            payload.addMetric("Recharge_In_Progress", privateCSReceivedData.getRechargeInProgress());
            payload.addMetric("PV_System_Active", privateCSReceivedData.getPvSystemActive());
            payload.addMetric("Aux_Charger_Active", privateCSReceivedData.getAuxChargerActive());
            payload.addMetric("Storage_Battery_Concactor_Sts", privateCSReceivedData.getStorageBatterySts());
            payload.addMetric("Converter_Contactor_Sts", privateCSReceivedData.getConverterContactorSts());
            payload.addMetric("IGBT_Temperature", privateCSReceivedData.getIgbtTemp());
            payload.addMetric("Storage_Battery_Temperature", privateCSReceivedData.getStorageBatteryTemp());
            payload.addMetric("Storage_Battery_SOC", privateCSReceivedData.getStorageBatterySOC());

            payload.addMetric("V_Out ", privateCSReceivedData.getvOut());
            payload.addMetric("Storage_Battery_V", privateCSReceivedData.getStorageBatteryV());
            payload.addMetric("PV_System_V", privateCSReceivedData.getPvSystemV());
            payload.addMetric("I_Out", privateCSReceivedData.getiOut());
            payload.addMetric("Storage_Battery_I ", privateCSReceivedData.getStorageBatteryI());

        } else {
            // fetch the publishing configuration from the publishing properties
            topic = "chargeonthego";
            qos = 0;
            retain = false;

            // Timestamp the message
            payload.setTimestamp(new Date());
            payload.addMetric("ID_OTM", motoTronReceivedData.getIdOtm());
            payload.addMetric("Fault_flag", publicCSReceivedData.getFaultFlag());
            payload.addMetric("Recharge_Available", publicCSReceivedData.getRechargeAvailable());
            payload.addMetric("Recharge_In_Progress", publicCSReceivedData.getRechargeInProgress());
            payload.addMetric("Fault_string", publicCSReceivedData.getFaultString());
            if (motoTronReceivedData.getVehiclePlate() != null) {
                payload.addMetric("Plate_ID", motoTronReceivedData.getVehiclePlate());
                // recharge time and date
                payload.addMetric("Energy_Out", publicCSReceivedData.getEnergyOut());
            }
        }

        publicCSReceivedData.resetData();
        privateCSReceivedData.resetData();
        motoTronReceivedData.resetData();

        // Publish the message
        try {
            cloudClient.publish(topic, payload, qos, retain);
            s_logger.info("Published to {} message: {}", topic, payload);
        } catch (Exception e) {
            s_logger.error("Cannot publish topic: " + topic, e);
        }
    }
}
