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

    private static final String THREAD_DELAY     = "can.initial.threads.delay";
    private static final String ID_200_FREQUENCY = "can.id200.message.frequency";
    private static final String ID_201_FREQUENCY = "can.id201.message.frequency";
    private static final String ID_202_FREQUENCY = "can.id202.message.frequency";
    private static final String IS_BIG_ENDIAN    = "can.bigendian";

    private static final String PUBLISH_RATE_PROP_NAME = "publish.rate";

    private CanConnectionService canConnection;
    private Map<String, Object>  classProperties;
    private Thread               listenThread;
    private Thread               sendThread1;
    private Thread               sendThread2;
    private Thread               sendThread3;
    private String               ifName;

    private CloudService cloudService;
    private CloudClient  cloudClient;

    private RechargeInfo    rechargeInfo;
    private BookingInfo     bookingInfo;
    private CurrentDateInfo currentDateInfo;
    private int             threadsDelay;
    private int             id200Freq;
    private int             id201Freq;
    private int             id202Freq;
    private boolean         isBigEndian = true;

    private volatile boolean senderRunning   = true;
    private volatile boolean receiverRunning = true;

    private PublicCSDataSnapshot  publicCSReceivedData  = new PublicCSDataSnapshot();
    private PrivateCSDataSnapshot privateCSReceivedData = new PrivateCSDataSnapshot();
    private MotoTronDataSnapshot  motoTronReceivedData  = new MotoTronDataSnapshot();

    private Thread publishThread;
    private int    publishRate;

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

    public void activate(ComponentContext componentContext, Map<String, Object> properties) {
        classProperties = properties;
        s_logger.info("activating Minigateway can test");
        ifName = "can0";

        if (classProperties != null) {
            if (classProperties.get("can.name") != null) {
                ifName = (String) classProperties.get("can.name");
            }

            rechargeInfo = new RechargeInfo(classProperties);
            bookingInfo = new BookingInfo(classProperties);
            currentDateInfo = new CurrentDateInfo(classProperties);
            getDelays();
            isBigEndian = (Boolean) classProperties.get(IS_BIG_ENDIAN);
            publishRate = (Integer) classProperties.get(PUBLISH_RATE_PROP_NAME);
        }

        // get the mqtt client for this application
        try {

            // Acquire a Cloud Application Client for this Application
            s_logger.info("Getting CloudClient for {}...", APP_ID);
            cloudClient = cloudService.newCloudClient(APP_ID);
            cloudClient.addCloudClientListener(this);
        } catch (Exception e) {
            s_logger.error("Error during component activation", e);
            throw new ComponentException(e);
        }

        // start threads
        startListeningThread();
        startSendThreads();
        startPublishThread();
    }

    public void deactivate(ComponentContext componentContext) {
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

        stopSendThreads();

        // shutting down the worker and cleaning up the properties
        if (publishThread != null) {
            listenThread.interrupt();
            try {
                publishThread.join(100);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        publishThread = null;

        // Releasing the CloudApplicationClient
        s_logger.info("Releasing CloudApplicationClient for {}...", APP_ID);
        cloudClient.release();
    }

    public void updated(Map<String, Object> properties) {
        s_logger.debug("updated...");

        classProperties = properties;
        if (classProperties != null) {
            if (classProperties.get("can.name") != null)
                ifName = (String) classProperties.get("can.name");
            rechargeInfo = new RechargeInfo(classProperties);
            bookingInfo = new BookingInfo(classProperties);
            currentDateInfo = new CurrentDateInfo(classProperties);

            getDelays();
            isBigEndian = (Boolean) classProperties.get(IS_BIG_ENDIAN);
        }

        stopSendThreads();
        senderRunning = true;
        startSendThreads();

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
            s_logger.info("Received can message with Id: " + canId);

            if (canId == 0x100) {
                CSMessage0x100.parseCanMessage(cm, isBigEndian, publicCSReceivedData);
            } else if (canId == 0x101) {
                CSMessage0x101.parseCanMessage(cm, publicCSReceivedData);
            } else if (canId == 0x102) {
                CSMessage0x102.parseCanMessage(cm, isBigEndian, publicCSReceivedData);
            } else if (canId == 0x300) {
                CSMessage0x300.parseCanMessage(cm, isBigEndian, privateCSReceivedData);
            } else if (canId == 0x301) {
                CSMessage0x301.parseCanMessage(cm, privateCSReceivedData);
            } else if (canId == 0x302) {
                CSMessage0x302.parseCanMessage(cm, isBigEndian, privateCSReceivedData);
            } else if (canId == 0x401) {
                CSMessage0x401.parseCanMessage(cm, motoTronReceivedData);
            } else if (canId == 0x402) {
                CSMessage0x402.parseCanMessage(cm, motoTronReceivedData);
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
        return;
    }

    @Override
    public void onConnectionLost() {
        return;
    }

    @Override
    public void onConnectionEstablished() {
        return;
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
        threadsDelay = Integer.parseInt((String) classProperties.get(THREAD_DELAY));
        id200Freq = Integer.parseInt((String) classProperties.get(ID_200_FREQUENCY));
        id201Freq = Integer.parseInt((String) classProperties.get(ID_201_FREQUENCY));
        id202Freq = Integer.parseInt((String) classProperties.get(ID_202_FREQUENCY));

    }

    private void startSendThreads() {
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
            sendMessage0x200(ifName);
        } catch (Exception e) {
            s_logger.warn("CanConnection Crash!", e);
        }
    }

    private void doSend2Test() {
        try {
            sendMessage0x201(ifName);
        } catch (Exception e) {
            s_logger.warn("CanConnection Crash!", e);
        }
    }

    private void doSend3Test() {
        try {
            sendMessage0x202(ifName);
        } catch (Exception e) {
            s_logger.warn("CanConnection Crash!", e);
        }
    }

    private void sendMessage0x200(String ifName) throws KuraException, IOException {

        if (canConnection == null) {
            return;
        }
        int id = 0x200;

        byte[] bMessage = GWMessage0x200.createMessage(id, rechargeInfo);

        canConnection.sendCanMessage(ifName, id, bMessage);
        s_logger.info("Message sent with id: " + id);
    }

    private void sendMessage0x201(String ifName) throws KuraException, IOException {

        if (canConnection == null) {
            return;
        }
        int id = 0x201;

        byte[] bMessage = GWMessage0x201.createMessage(id, bookingInfo, isBigEndian);

        canConnection.sendCanMessage(ifName, id, bMessage);
        s_logger.info("Message sent with id: " + id);
    }

    private void sendMessage0x202(String ifName) throws KuraException, IOException {

        if (canConnection == null) {
            return;
        }
        int id = 0x202;

        byte[] bMessage = GWMessage0x202.createMessage(id, currentDateInfo, isBigEndian);

        canConnection.sendCanMessage(ifName, id, bMessage);
        s_logger.info("Message sent with id: " + id);
    }

    /**
     * Called at the configured rate to publish the next aggregated measurement.
     */
    private void doPublish() {
        // fetch the publishing configuration from the publishing properties
        String topic = "privatecsdata";
        Integer qos = 0;
        Boolean retain = false;

        // Allocate a new payload
        KuraPayload payload = new KuraPayload();

        // Timestamp the message
        payload.setTimestamp(new Date());

        // Add all the needed data to the payload
        payload.addMetric("Power_Out", privateCSReceivedData.getPowerOut());
        payload.addMetric("Minutes_to_Recharge_Estimated", privateCSReceivedData.getMinutesToRecharge());
        payload.addMetric("Seconds_to_Recharge_Estimated", privateCSReceivedData.getSecondsToRecharge());
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

        privateCSReceivedData.resetData();

        // Publish the message
        try {
            cloudClient.publish(topic, payload, qos, retain);
            s_logger.info("Published to {} message: {}", topic, payload);
        } catch (Exception e) {
            s_logger.error("Cannot publish topic: " + topic, e);
        }
    }
}
