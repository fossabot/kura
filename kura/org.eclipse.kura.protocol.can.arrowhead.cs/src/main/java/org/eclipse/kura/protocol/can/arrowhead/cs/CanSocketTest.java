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
import org.eclipse.kura.protocol.can.message.cs.Message0x100;
import org.eclipse.kura.protocol.can.message.cs.Message0x101;
import org.eclipse.kura.protocol.can.message.cs.Message0x102;
import org.eclipse.kura.protocol.can.message.cs.Message0x300;
import org.eclipse.kura.protocol.can.message.cs.Message0x301;
import org.eclipse.kura.protocol.can.message.cs.Message0x302;
import org.eclipse.kura.protocol.can.message.cs.Message0x401;
import org.eclipse.kura.protocol.can.message.cs.Message0x402;
import org.eclipse.kura.protocol.can.message.gw.Message0x200;
import org.eclipse.kura.protocol.can.message.gw.Message0x201;
import org.eclipse.kura.protocol.can.message.gw.Message0x202;
import org.eclipse.kura.protocol.can.message.gw.Message0x400;
import org.eclipse.kura.protocol.can.utils.MessageUtils;

public class CanSocketTest implements ConfigurableComponent {
    private static final Logger s_logger      = LoggerFactory.getLogger(CanSocketTest.class);
    private static final String IS_BIG_ENDIAN = "can.bigendian";
    private static final String MODALITY      = "arrowhead.modality";
    private static final String MODALITY_T311 = "t3.1.1";
    private static final String MODALITY_T312 = "t3.1.2";
    private static final String MODALITY_T32  = "t3.2";

    private CanConnectionService m_canConnection;
    private Map<String, Object>  m_properties;
    private Thread               m_listenThread;
    private Thread               m_sendThread;
    private String               m_ifName;
    private int                  m_nextMessageIndex;
    private boolean              isBigEndian = true;
    private String               modality;

    private Message0x100 message0x100Info;
    private Message0x101 message0x101Info;
    private Message0x102 message0x102Info;

    private Message0x300 message0x300Info;
    private Message0x301 message0x301Info;
    private Message0x302 message0x302Info;

    private Message0x401 message0x401Info;
    private Message0x402 message0x402Info;

    private int counter = 0;

    public void setCanConnectionService(CanConnectionService canConnection) {
        this.m_canConnection = canConnection;
    }

    public void unsetCanConnectionService(CanConnectionService canConnection) {
        this.m_canConnection = null;
    }

    protected void activate(ComponentContext componentContext, Map<String, Object> properties) {
        m_properties = properties;
        s_logger.info("activating CS can test");
        m_ifName = "can0";

        if (m_properties != null) {
            if (m_properties.get("can.name") != null) {
                m_ifName = (String) m_properties.get("can.name");
            }

            modality = (String) m_properties.get(MODALITY);

            message0x100Info = new Message0x100();
            message0x100Info.populateMessageInfo(m_properties);

            message0x101Info = new Message0x101();
            message0x101Info.populateMessageInfo(m_properties);

            message0x102Info = new Message0x102();
            message0x102Info.populateMessageInfo(m_properties);

            message0x300Info = new Message0x300();
            message0x300Info.populateMessageInfo(m_properties);

            message0x301Info = new Message0x301();
            message0x301Info.populateMessageInfo(m_properties);

            message0x302Info = new Message0x302();
            message0x302Info.populateMessageInfo(m_properties);

            message0x401Info = new Message0x401();
            message0x401Info.populateMessageInfo(m_properties);

            message0x402Info = new Message0x402();
            message0x402Info.populateMessageInfo(m_properties);

            isBigEndian = (Boolean) m_properties.get(IS_BIG_ENDIAN);
        }

        if (m_listenThread != null) {
            m_listenThread.interrupt();
            try {
                m_listenThread.join(100);
            } catch (InterruptedException e) {
                // Ignore
            }
            m_listenThread = null;
        }

        m_listenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (m_canConnection != null) {
                    while (true) {
                        doReceiveTest();
                    }
                }
            }
        });
        m_listenThread.start();

        if (m_sendThread != null) {
            m_sendThread.interrupt();
            try {
                m_sendThread.join(100);
            } catch (InterruptedException e) {
                // Ignore
            }
            m_sendThread = null;
        }

        m_sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (m_canConnection != null) {
                    while (true) {
                        doSendTest();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {}
                    }
                }
            }
        });
        m_sendThread.start();
    }

    protected void deactivate(ComponentContext componentContext) {
        if (m_listenThread != null) {
            m_listenThread.interrupt();
            try {
                m_listenThread.join(100);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        m_listenThread = null;

        if (m_sendThread != null) {
            m_sendThread.interrupt();
            try {
                m_sendThread.join(100);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        m_sendThread = null;
    }

    public void updated(Map<String, Object> properties) {
        s_logger.debug("updated...");

        m_properties = properties;
        if (m_properties != null) {
            if (m_properties.get("can.name") != null) {
                m_ifName = (String) m_properties.get("can.name");
            }

            modality = (String) m_properties.get(MODALITY);

            message0x100Info = new Message0x100();
            message0x100Info.populateMessageInfo(m_properties);

            message0x101Info = new Message0x101();
            message0x101Info.populateMessageInfo(m_properties);

            message0x102Info = new Message0x102();
            message0x102Info.populateMessageInfo(m_properties);

            message0x300Info = new Message0x300();
            message0x300Info.populateMessageInfo(m_properties);

            message0x301Info = new Message0x301();
            message0x301Info.populateMessageInfo(m_properties);

            message0x302Info = new Message0x302();
            message0x302Info.populateMessageInfo(m_properties);

            message0x401Info = new Message0x401();
            message0x401Info.populateMessageInfo(m_properties);

            message0x402Info = new Message0x402();
            message0x402Info.populateMessageInfo(m_properties);

            isBigEndian = (Boolean) m_properties.get(IS_BIG_ENDIAN);
        }
    }

    public void doReceiveTest() {
        CanMessage cm = null;
        s_logger.info("Wait for a request");
        try {
            cm = m_canConnection.receiveCanMessage(-1, 0x7FF);
        } catch (KuraException e) {
            s_logger.warn("CanConnection Crash! -> KuraException");
            e.printStackTrace();
        } catch (IOException e) {
            s_logger.warn("CanConnection Crash! -> IOException");
            e.printStackTrace();
        }

        if (cm != null) {
            int canId = cm.getCanId();
            s_logger.info("Received can message with Id: " + canId);

            if (canId == 0x200) {
                Message0x200.parseGwCanMessage(cm);
            } else if (canId == 0x201) {
                Message0x201.parseGwCanMessage(cm, isBigEndian);
            } else if (canId == 0x202) {
                Message0x202.parseGwCanMessage3(cm, isBigEndian);
            } else if (canId == 0x400) {
                Message0x400.parseGwCanMessage(cm);
            }
        } else {
            s_logger.warn("receive=null");
        }
    }

    public void doSendTest() {

        if (MODALITY_T311.equals(modality)) {
            doSendT311();
        } else if (MODALITY_T312.equals(modality)) {
            doSendT312();
        } else {
            doSendT32();
        }
    }

    private void doSendT311() {
        try {
            if (counter != 0) {
                sendMessage0x300(m_ifName);
            } else {
                if (m_nextMessageIndex == 0) {
                    sendMessage0x301(m_ifName);
                } else if (m_nextMessageIndex == 1) {
                    sendMessage0x302(m_ifName);
                }
                m_nextMessageIndex++;
                m_nextMessageIndex = m_nextMessageIndex % 2;
            }
        } catch (Exception e) {
            s_logger.warn("CanConnection Crash!", e);
        }
        counter++;
        counter = counter % 10;
    }

    private void doSendT312() {
        try {
            if (counter != 0) {
                sendMessage0x100(m_ifName);
            } else {
                if (m_nextMessageIndex == 0) {
                    sendMessage0x101(m_ifName);
                } else if (m_nextMessageIndex == 1) {
                    sendMessage0x102(m_ifName);
                }
                m_nextMessageIndex++;
                m_nextMessageIndex = m_nextMessageIndex % 2;
            }
        } catch (Exception e) {
            s_logger.warn("CanConnection Crash!", e);
        }
        counter++;
        counter = counter % 10;
    }

    private void doSendT32() {
        doSendT312();
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        try {
            if (m_nextMessageIndex == 0) {
                sendMessage0x401(m_ifName);
            } else if (m_nextMessageIndex == 1) {
                sendMessage0x402(m_ifName);
            }
            m_nextMessageIndex++;
            m_nextMessageIndex = m_nextMessageIndex % 2;
        } catch (Exception e) {
            s_logger.warn("CanConnection Crash!", e);
        }

    }

    private void sendMessage0x100(String ifName) throws KuraException, IOException {

        if ((m_canConnection == null))
            return;
        int id = 0x100;
        StringBuilder sb = new StringBuilder("Trying to send message 0x100 can frame with message = ");
        byte bMessage[] = new byte[8];
        int powerOut = message0x100Info.getPowerOut(); // Power Out [W]
        int timeToRechargeMinutes = message0x100Info.getTimeToRechargeMinutes(); // Time
        // to
        // recharge
        // [minutes]
        int timeToRechargeSeconds = message0x100Info.getTimeToRechargeSeconds(); // Time
        // to
        // recharge
        // [seconds]
        int energyOut = message0x100Info.getEnergyOut(); // Energy Out [Wh]
        int powerPV = message0x100Info.getPowerPV(); // Power PV Out [W]

        if (isBigEndian) {
            bMessage[0] = (byte) ((powerOut >> 8) & 0xFF);
            bMessage[1] = (byte) (powerOut & 0xFF);
        } else {
            bMessage[0] = (byte) (powerOut & 0xFF);
            bMessage[1] = (byte) ((powerOut >> 8) & 0xFF);
        }

        bMessage[2] = (byte) timeToRechargeMinutes;
        bMessage[3] = (byte) timeToRechargeSeconds;

        if (isBigEndian) {
            bMessage[4] = (byte) ((energyOut >> 8) & 0xFF);
            bMessage[5] = (byte) (energyOut & 0xFF);
        } else {
            bMessage[4] = (byte) (energyOut & 0xFF);
            bMessage[5] = (byte) ((energyOut >> 8) & 0xFF);
        }

        if (isBigEndian) {
            bMessage[6] = (byte) ((powerPV >> 8) & 0xFF);
            bMessage[7] = (byte) (powerPV & 0xFF);
        } else {
            bMessage[6] = (byte) (powerPV & 0xFF);
            bMessage[7] = (byte) ((powerPV >> 8) & 0xFF);
        }

        if (isBigEndian) {
            sb.append("Power Out " + MessageUtils.buildShort(bMessage[0], bMessage[1]) + " W, ");
        } else {
            sb.append("Power Out " + MessageUtils.buildShort(bMessage[1], bMessage[0]) + " W, ");
        }
        sb.append("Time to Recharge " + bMessage[2] + " minutes, ");
        sb.append("Time to Recharge " + bMessage[3] + " s, ");
        if (isBigEndian) {
            sb.append("Energy Out " + MessageUtils.buildShort(bMessage[4], bMessage[5]) + " Wh, ");
        } else {
            sb.append("Energy Out " + MessageUtils.buildShort(bMessage[5], bMessage[4]) + " Wh, ");
        }
        if (isBigEndian) {
            sb.append("Power PV " + MessageUtils.buildShort(bMessage[6], bMessage[7]) + " W");
        } else {
            sb.append("Power PV " + MessageUtils.buildShort(bMessage[7], bMessage[6]) + " W");
        }

        sb.append(" and id = ");
        sb.append(id);
        s_logger.debug(sb.toString());

        m_canConnection.sendCanMessage(ifName, id, bMessage);
        s_logger.info("Message sent with id: " + id);
    }

    private void sendMessage0x101(String ifName) throws KuraException, IOException {

        if ((m_canConnection == null))
            return;
        int id = 0x101;
        StringBuilder sb = new StringBuilder("Trying to send message 0x101 can frame with message = ");
        byte bMessage[] = new byte[5];

        int faultFlag = message0x101Info.isFaultFlag(); // fault flag [0,1]
        int rechargeAvailable = (message0x101Info.isRechargeAvailable() << 1); // recharge
        // available
        // [0,1]
        int rechargeInProgress = (message0x101Info.isRechargeInProgress() << 2); // recharge
        // in
        // progress
        // [0,1]
        int pvSystemActive = (message0x101Info.isPvSystemActive() << 3); // Pv
        // System
        // Active
        // [0,1]
        int auxChargerActive = (message0x101Info.isAuxChargerActive() << 4); // Aux
        // Charger
        // Active
        // [0,1]
        int storageBatteryContractorStatus = (message0x101Info.isStorageBatteryContactorStatus() << 5); // Storage
        // Battery
        // Contactor
        // Status
        // [0,1]
        int converterContractorStatus = (message0x101Info.isConverterContactorStatus() << 6); // Converter
        // Contactor
        // Status
        // [0,1]

        int faultString = message0x101Info.getFaultString();
        int igbtTemp = message0x101Info.getIgbtTemp();
        int storageBatteryTemperature = message0x101Info.getStorageTemp();
        int storageBatterySOC = message0x101Info.getStorageBatterySoc();

        bMessage[0] = (byte) (faultFlag +
                rechargeAvailable +
                rechargeInProgress +
                pvSystemActive +
                auxChargerActive +
                storageBatteryContractorStatus +
                converterContractorStatus);

        bMessage[1] = (byte) faultString; // Fault String
        bMessage[2] = (byte) igbtTemp; // IGBT_temp [°C]
        bMessage[3] = (byte) storageBatteryTemperature; // Storage temp [°C]
        bMessage[4] = (byte) storageBatterySOC; // Storage battery SOC [%]

        sb.append("fault flag: " + message0x101Info.isFaultFlag() + ", ");
        sb.append("recharge available: " + message0x101Info.isRechargeAvailable() + ", ");
        sb.append("recharge in progress: " + message0x101Info.isRechargeInProgress() + ", ");
        sb.append("Pv System Active: " + message0x101Info.isPvSystemActive() + ", ");
        sb.append("Aux Charger Active: " + message0x101Info.isAuxChargerActive() + ", ");
        sb.append("Storage Battery Contactor Status: " + message0x101Info.isStorageBatteryContactorStatus() + ", ");
        sb.append("Converter Contactor Status: " + message0x101Info.isConverterContactorStatus() + ", ");

        sb.append("Fault String  " + bMessage[1] + ", ");
        sb.append("IGBT_temp " + bMessage[2] + ", ");
        sb.append("Storage temp " + bMessage[3] + ", ");
        sb.append("Storage battery SOC " + bMessage[4]);

        sb.append(" and id = ");
        sb.append(id);
        s_logger.debug(sb.toString());

        m_canConnection.sendCanMessage(ifName, id, bMessage);
        s_logger.info("Message sent with id: " + id);
    }

    private void sendMessage0x102(String ifName) throws KuraException, IOException {

        if ((m_canConnection == null))
            return;
        int id = 0x102;
        StringBuilder sb = new StringBuilder("Trying to send message 0x102 can frame with message = ");
        byte bCurrentDate[] = new byte[8];

        int vOut = message0x102Info.getvOut();
        int storageBatteryV = message0x102Info.getStorageBatteryV();
        int pvSystemV = message0x102Info.getPvSystemV();
        int iOut = message0x102Info.getiOut();
        int storageBatteryI = message0x102Info.getStorageBatteryI();

        if (isBigEndian) {
            bCurrentDate[0] = (byte) ((vOut >> 8) & 0xFF);
            bCurrentDate[1] = (byte) ((vOut) & 0xFF); // V Out
        } else {
            bCurrentDate[0] = (byte) ((vOut) & 0xFF); // V Out
            bCurrentDate[1] = (byte) ((vOut >> 8) & 0xFF);
        }

        if (isBigEndian) {
            bCurrentDate[2] = (byte) ((storageBatteryV >> 8) & 0xFF); // Storage_Battery_V
            // [V]
            bCurrentDate[3] = (byte) (storageBatteryV & 0xFF); // Storage_Battery_V
            // [V]
        } else {
            bCurrentDate[2] = (byte) (storageBatteryV & 0xFF); // Storage_Battery_V
            // [V]
            bCurrentDate[3] = (byte) ((storageBatteryV >> 8) & 0xFF); // Storage_Battery_V
            // [V]
        }

        if (isBigEndian) {
            bCurrentDate[4] = (byte) ((pvSystemV >> 8) & 0xFF); // PV_System_V
            // [V]
            bCurrentDate[5] = (byte) (pvSystemV & 0xFF); // PV_System_V [V]
        } else {
            bCurrentDate[4] = (byte) (pvSystemV & 0xFF); // PV_System_V [V]
            bCurrentDate[5] = (byte) ((pvSystemV >> 8) & 0xFF); // PV_System_V
            // [V]
        }

        bCurrentDate[6] = (byte) iOut; // I_Out [A]

        bCurrentDate[7] = (byte) storageBatteryI; // Storage_Battery_I [A]

        if (isBigEndian) {
            sb.append("V Out: " + MessageUtils.buildShort(bCurrentDate[0], bCurrentDate[1]) + ", ");
        } else {
            sb.append("V Out: " + MessageUtils.buildShort(bCurrentDate[1], bCurrentDate[0]) + ", ");
        }

        if (isBigEndian) {
            sb.append("Storage_Battery_V: " + MessageUtils.buildShort(bCurrentDate[2], bCurrentDate[3]) + ", ");
        } else {
            sb.append("Storage_Battery_V: " + MessageUtils.buildShort(bCurrentDate[3], bCurrentDate[2]) + ", ");
        }

        if (isBigEndian) {
            sb.append("PV_System_V: " + MessageUtils.buildShort(bCurrentDate[4], bCurrentDate[5]) + ", ");
        } else {
            sb.append("PV_System_V: " + MessageUtils.buildShort(bCurrentDate[5], bCurrentDate[4]) + ", ");
        }
        sb.append("I_Out: " + bCurrentDate[6] + ", ");
        sb.append("Storage_Battery_I: " + bCurrentDate[7]);

        sb.append(" and id = ");
        sb.append(id);
        s_logger.debug(sb.toString());

        m_canConnection.sendCanMessage(ifName, id, bCurrentDate);
        s_logger.info("Message sent with id: " + id);
    }

    //
    // 0x30x messages
    private void sendMessage0x300(String ifName) throws KuraException, IOException {

        if ((m_canConnection == null))
            return;
        int id = 0x300;
        StringBuilder sb = new StringBuilder("Trying to send message 0x300 can frame with message = ");
        byte bMessage[] = new byte[8];
        int powerOut = message0x300Info.getPowerOut(); // Power Out [W]
        int timeToRechargeMinutes = message0x300Info.getTimeToRechargeMinutes(); // Time
        // to
        // recharge
        // [minutes]
        int timeToRechargeHours = message0x300Info.getTimeToRechargeHours(); // Time
        // to
        // recharge
        // [hours]
        int energyOut = message0x300Info.getEnergyOut(); // Energy Out [Wh]
        int powerPV = message0x300Info.getPowerPV(); // Power PV Out [W]

        if (isBigEndian) {
            bMessage[0] = (byte) ((powerOut >> 8) & 0xFF);
            bMessage[1] = (byte) (powerOut & 0xFF);
        } else {
            bMessage[0] = (byte) (powerOut & 0xFF);
            bMessage[1] = (byte) ((powerOut >> 8) & 0xFF);
        }

        bMessage[2] = (byte) timeToRechargeHours;
        bMessage[3] = (byte) timeToRechargeMinutes;

        if (isBigEndian) {
            bMessage[4] = (byte) ((energyOut >> 8) & 0xFF);
            bMessage[5] = (byte) (energyOut & 0xFF);
        } else {
            bMessage[4] = (byte) (energyOut & 0xFF);
            bMessage[5] = (byte) ((energyOut >> 8) & 0xFF);
        }

        if (isBigEndian) {
            bMessage[6] = (byte) ((powerPV >> 8) & 0xFF);
            bMessage[7] = (byte) (powerPV & 0xFF);
        } else {
            bMessage[6] = (byte) (powerPV & 0xFF);
            bMessage[7] = (byte) ((powerPV >> 8) & 0xFF);
        }

        if (isBigEndian) {
            sb.append("Power Out " + MessageUtils.buildShort(bMessage[0], bMessage[1]) + " W, ");
        } else {
            sb.append("Power Out " + MessageUtils.buildShort(bMessage[1], bMessage[0]) + " W, ");
        }
        sb.append("Time to Recharge " + bMessage[2] + " hours, ");
        sb.append("Time to Recharge " + bMessage[3] + " minutes, ");
        if (isBigEndian) {
            sb.append("Energy Out " + MessageUtils.buildShort(bMessage[4], bMessage[5]) + " Wh, ");
        } else {
            sb.append("Energy Out " + MessageUtils.buildShort(bMessage[5], bMessage[4]) + " Wh, ");
        }
        if (isBigEndian) {
            sb.append("Power PV " + MessageUtils.buildShort(bMessage[6], bMessage[7]) + " W");
        } else {
            sb.append("Power PV " + MessageUtils.buildShort(bMessage[7], bMessage[6]) + " W");
        }

        sb.append(" and id = ");
        sb.append(id);
        s_logger.debug(sb.toString());

        m_canConnection.sendCanMessage(ifName, id, bMessage);
        s_logger.info("Message sent with id: " + id);
    }

    private void sendMessage0x301(String ifName) throws KuraException, IOException {

        if ((m_canConnection == null))
            return;
        int id = 0x301;
        StringBuilder sb = new StringBuilder("Trying to send message 0x301 can frame with message = ");
        byte bMessage[] = new byte[5];

        int faultFlag = message0x301Info.isFaultFlag(); // fault flag [0,1]
        int rechargeAvailable = (message0x301Info.isRechargeAvailable() << 1); // recharge
        // available
        // [0,1]
        int rechargeInProgress = (message0x301Info.isRechargeInProgress() << 2); // recharge
        // in
        // progress
        // [0,1]
        int pvSystemActive = (message0x301Info.isPvSystemActive() << 3); // Pv
        // System
        // Active
        // [0,1]
        int auxChargerActive = (message0x301Info.isAuxChargerActive() << 4); // Aux
        // Charger
        // Active
        // [0,1]
        int storageBatteryContractorStatus = (message0x301Info.isStorageBatteryContactorStatus() << 5); // Storage
        // Battery
        // Contactor
        // Status
        // [0,1]
        int converterContractorStatus = (message0x301Info.isConverterContactorStatus() << 6); // Converter
        // Contactor
        // Status
        // [0,1]

        int faultString = message0x301Info.getFaultString();
        int igbtTemp = message0x301Info.getIgbtTemp();
        int storageBatteryTemperature = message0x301Info.getStorageTemp();
        int storageBatterySOC = message0x301Info.getStorageBatterySoc();

        bMessage[0] = (byte) (faultFlag +
                rechargeAvailable +
                rechargeInProgress +
                pvSystemActive +
                auxChargerActive +
                storageBatteryContractorStatus +
                converterContractorStatus);

        bMessage[1] = (byte) faultString; // Fault String
        bMessage[2] = (byte) igbtTemp; // IGBT_temp [°C]
        bMessage[3] = (byte) storageBatteryTemperature; // Storage temp [°C]
        bMessage[4] = (byte) storageBatterySOC; // Storage battery SOC [%]

        sb.append("fault flag: " + message0x301Info.isFaultFlag() + ", ");
        sb.append("recharge available: " + message0x301Info.isRechargeAvailable() + ", ");
        sb.append("recharge in progress: " + message0x301Info.isRechargeInProgress() + ", ");
        sb.append("Pv System Active: " + message0x301Info.isPvSystemActive() + ", ");
        sb.append("Aux Charger Active: " + message0x301Info.isAuxChargerActive() + ", ");
        sb.append("Storage Battery Contactor Status: " + message0x301Info.isStorageBatteryContactorStatus() + ", ");
        sb.append("Converter Contactor Status: " + message0x301Info.isConverterContactorStatus() + ", ");

        sb.append("Fault String  " + bMessage[1] + ", ");
        sb.append("IGBT_temp " + bMessage[2] + ", ");
        sb.append("Storage temp " + bMessage[3] + ", ");
        sb.append("Storage battery SOC " + bMessage[4]);

        sb.append(" and id = ");
        sb.append(id);
        s_logger.debug(sb.toString());

        m_canConnection.sendCanMessage(ifName, id, bMessage);
        s_logger.info("Message sent with id: " + id);
    }

    private void sendMessage0x302(String ifName) throws KuraException, IOException {

        if ((m_canConnection == null))
            return;
        int id = 0x302;
        StringBuilder sb = new StringBuilder("Trying to send message 0x302 can frame with message = ");
        byte bCurrentDate[] = new byte[8];

        int vOut = message0x302Info.getvOut();
        int storageBatteryV = message0x302Info.getStorageBatteryV();
        int pvSystemV = message0x302Info.getPvSystemV();
        int iOut = message0x302Info.getiOut();
        int storageBatteryI = message0x302Info.getStorageBatteryI();

        if (isBigEndian) {
            bCurrentDate[0] = (byte) ((vOut >> 8) & 0xFF);
            bCurrentDate[1] = (byte) ((vOut) & 0xFF); // V Out
        } else {
            bCurrentDate[0] = (byte) ((vOut) & 0xFF); // V Out
            bCurrentDate[1] = (byte) ((vOut >> 8) & 0xFF);
        }

        if (isBigEndian) {
            bCurrentDate[2] = (byte) ((storageBatteryV >> 8) & 0xFF); // Storage_Battery_V
            // [V]
            bCurrentDate[3] = (byte) (storageBatteryV & 0xFF); // Storage_Battery_V
            // [V]
        } else {
            bCurrentDate[2] = (byte) (storageBatteryV & 0xFF); // Storage_Battery_V
            // [V]
            bCurrentDate[3] = (byte) ((storageBatteryV >> 8) & 0xFF); // Storage_Battery_V
            // [V]
        }

        if (isBigEndian) {
            bCurrentDate[4] = (byte) ((pvSystemV >> 8) & 0xFF); // PV_System_V
            // [V]
            bCurrentDate[5] = (byte) (pvSystemV & 0xFF); // PV_System_V [V]
        } else {
            bCurrentDate[4] = (byte) (pvSystemV & 0xFF); // PV_System_V [V]
            bCurrentDate[5] = (byte) ((pvSystemV >> 8) & 0xFF); // PV_System_V
            // [V]
        }

        bCurrentDate[6] = (byte) iOut; // I_Out [A]

        bCurrentDate[7] = (byte) storageBatteryI; // Storage_Battery_I [A]

        if (isBigEndian) {
            sb.append("V Out: " + MessageUtils.buildShort(bCurrentDate[0], bCurrentDate[1]) + ", ");
        } else {
            sb.append("V Out: " + MessageUtils.buildShort(bCurrentDate[1], bCurrentDate[0]) + ", ");
        }

        if (isBigEndian) {
            sb.append("Storage_Battery_V: " + MessageUtils.buildShort(bCurrentDate[2], bCurrentDate[3]) + ", ");
        } else {
            sb.append("Storage_Battery_V: " + MessageUtils.buildShort(bCurrentDate[3], bCurrentDate[2]) + ", ");
        }

        if (isBigEndian) {
            sb.append("PV_System_V: " + MessageUtils.buildShort(bCurrentDate[4], bCurrentDate[5]) + ", ");
        } else {
            sb.append("PV_System_V: " + MessageUtils.buildShort(bCurrentDate[5], bCurrentDate[4]) + ", ");
        }
        sb.append("I_Out: " + bCurrentDate[6] + ", ");
        sb.append("Storage_Battery_I: " + bCurrentDate[7]);

        sb.append(" and id = ");
        sb.append(id);
        s_logger.debug(sb.toString());

        m_canConnection.sendCanMessage(ifName, id, bCurrentDate);
        s_logger.info("Message sent with id: " + id);
    }

    private void sendMessage0x401(String ifName) throws KuraException, IOException {

        if ((m_canConnection == null))
            return;
        int id = 0x401;
        StringBuilder sb = new StringBuilder("Trying to send message 0x401 can frame with message = ");
        byte[] bMessage = new byte[8];

        int localBookingId = message0x401Info.getLocalBookingId();
        char plateChar1 = message0x401Info.getPlateChar1();
        char plateChar2 = message0x401Info.getPlateChar2();
        char plateChar3 = message0x401Info.getPlateChar3();
        char plateChar4 = message0x401Info.getPlateChar4();
        char plateChar5 = message0x401Info.getPlateChar5();
        char plateChar6 = message0x401Info.getPlateChar6();
        char plateChar7 = message0x401Info.getPlateChar7();

        bMessage[0] = (byte) localBookingId;
        bMessage[1] = (byte) plateChar1;
        bMessage[2] = (byte) plateChar2;
        bMessage[3] = (byte) plateChar3;
        bMessage[4] = (byte) plateChar4;
        bMessage[5] = (byte) plateChar5;
        bMessage[6] = (byte) plateChar6;
        bMessage[7] = (byte) plateChar7;

        sb.append("LocalBooking id: " + bMessage[0] + ", ");
        sb.append("plateChar1: " + bMessage[1] + ", ");
        sb.append("plateChar2: " + bMessage[2] + ", ");
        sb.append("plateChar3: " + bMessage[3] + ", ");
        sb.append("plateChar4: " + bMessage[4] + ", ");
        sb.append("plateChar5: " + bMessage[5] + ", ");
        sb.append("plateChar6: " + bMessage[6] + ", ");
        sb.append("plateChar7: " + bMessage[7] + ", ");

        sb.append(" and id = ");
        sb.append(id);
        s_logger.debug(sb.toString());

        m_canConnection.sendCanMessage(ifName, id, bMessage);
        s_logger.info("Message sent with id: " + id);
    }

    private void sendMessage0x402(String ifName) throws KuraException, IOException {

        if ((m_canConnection == null))
            return;
        int id = 0x402;
        StringBuilder sb = new StringBuilder("Trying to send message 0x402 can frame with message = ");
        byte bMessage[] = new byte[6];

        int localBookingId = message0x402Info.getLocalBookingId();
        char plateChar8 = message0x402Info.getPlateChar8();
        char plateChar9 = message0x402Info.getPlateChar9();
        char plateChar10 = message0x402Info.getPlateChar10();
        char plateChar11 = message0x402Info.getPlateChar11();
        char plateChar12 = message0x402Info.getPlateChar12();

        bMessage[0] = (byte) localBookingId;
        bMessage[1] = (byte) plateChar8;
        bMessage[2] = (byte) plateChar9;
        bMessage[3] = (byte) plateChar10;
        bMessage[4] = (byte) plateChar11;
        bMessage[5] = (byte) plateChar12;

        sb.append("LocalBooking id: " + bMessage[0] + ", ");
        sb.append("plateChar8: " + bMessage[1] + ", ");
        sb.append("plateChar9: " + bMessage[2] + ", ");
        sb.append("plateChar10: " + bMessage[3] + ", ");
        sb.append("plateChar11: " + bMessage[4] + ", ");
        sb.append("plateChar12: " + bMessage[5] + ", ");

        sb.append(" and id = ");
        sb.append(id);
        s_logger.debug(sb.toString());

        m_canConnection.sendCanMessage(ifName, id, bMessage);
        s_logger.info("Message sent with id: " + id);
    }
}
