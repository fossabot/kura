package org.eclipse.kura.protocol.can.messages;

import org.eclipse.kura.protocol.can.CanMessage;
import org.eclipse.kura.protocol.can.arrowhead.ArrowheadCanSocketImpl;
import org.eclipse.kura.protocol.can.cs.data.PublicCSDataSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class statically manages 0x101 messages received from a Charging Station
 * The output resulting from parsing will consist of the following values in
 * csReceivedData object:
 * <li>Fault_Flag</li>
 * <li>Recharge_Available</li>
 * <li>Recharge_In_Progress</li>
 * <li>PV_System_Active</li>
 * <li>Aux_Charger_Active</li>
 * <li>Storage_Battery_Concactor_Sts</li>
 * <li>Converter_Contactor_Sts</li>
 * <li>Fault string</li>
 * <li>IGBT_Temperature</li>
 * <li>Storage_Battery_Temperature</li>
 * <li>Storage_Battery_SOC</li>
 *
 */
public class CSMessage0x101 {
    private static final Logger s_logger = LoggerFactory.getLogger(ArrowheadCanSocketImpl.class);

    private CSMessage0x101() {
    }

    public static void parseCanMessage(CanMessage cm, PublicCSDataSnapshot publicCSReceivedData) {
        byte[] b = cm.getData();
        if (b != null && b.length == 5) {
            StringBuilder sb = new StringBuilder("received : ");

            int faultFlag = b[0] & 0x01;
            int rechargeAvailable = (b[0] & 0x02) >> 1;
            int rechargeInProgress = (b[0] & 0x04) >> 2;
            int pvSystemActive = (b[0] & 0x08) >> 3;
            int auxChargerActive = (b[0] & 0x10) >> 4;
            int storageBatteryConcactorSts = (b[0] & 0x20) >> 5;
            int converterConcactorSts = (b[0] & 0x40) >> 6;

            int faultString = b[1];
            int igbtTemperature = b[2];
            int storageBatteryTemperature = b[3];
            int storageBatterySOC = b[4];

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

            publicCSReceivedData.setRechargeAvail(rechargeAvailable);
            publicCSReceivedData.setRechargeInProgress(rechargeInProgress);
            publicCSReceivedData.setPVSystemActive(pvSystemActive);
            publicCSReceivedData.setAuxChargerActive(auxChargerActive);
            publicCSReceivedData.setStorageBatterySts(storageBatteryConcactorSts);
            publicCSReceivedData.setConverterContactorSts(converterConcactorSts);
            publicCSReceivedData.setIGBTTemp(igbtTemperature);
            publicCSReceivedData.setStorageBatteryTemp(storageBatteryTemperature);
            publicCSReceivedData.setStorageBatterySOC(storageBatterySOC);
        }
    }

}
