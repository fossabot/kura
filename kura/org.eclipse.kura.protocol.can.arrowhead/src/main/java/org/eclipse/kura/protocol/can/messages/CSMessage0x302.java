package org.eclipse.kura.protocol.can.messages;

import org.eclipse.kura.protocol.can.CanMessage;
import org.eclipse.kura.protocol.can.arrowhead.ArrowheadCanSocketImpl;
import org.eclipse.kura.protocol.can.cs.data.PrivateCSDataSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class statically manages 0x102 messages received from a Charging Station
 * The output resulting from parsing will consist of the following values in
 * csReceivedData object:
 * <li>Fault_Flag</li>
 * <li>V_Out</li>
 * <li>Storage_Battery_V</li>
 * <li>PV_System_V</li>
 * <li>I_Out</li>
 * <li>Storage_Battery_I</li>
 *
 */
public class CSMessage0x302 {
    private static final Logger s_logger = LoggerFactory.getLogger(ArrowheadCanSocketImpl.class);

    private CSMessage0x302() {
    }

    public static void parseCanMessage(CanMessage cm, boolean isBigEndian, PrivateCSDataSnapshot privateCSReceivedData) {
        byte[] b = cm.getData();
        if (b != null && b.length == 8) {
            StringBuilder sb = new StringBuilder("received 0x302: ");

            int vOut;
            if (isBigEndian) {
                vOut = MessageUtils.buildShort(b[0], b[1]);
            } else {
                vOut = MessageUtils.buildShort(b[1], b[0]);
            }

            int storageBatteryV;
            if (isBigEndian) {
                storageBatteryV = MessageUtils.buildShort(b[2], b[3]);
            } else {
                storageBatteryV = MessageUtils.buildShort(b[3], b[2]);
            }

            int pvSystemV;
            if (isBigEndian) {
                pvSystemV = MessageUtils.buildShort(b[4], b[5]);
            } else {
                pvSystemV = MessageUtils.buildShort(b[5], b[4]);
            }

            int iOut = b[6];
            int storageBatteryI = b[7];

            sb.append("V out: " + vOut + " V, ");
            sb.append("Storage Battery V: " + storageBatteryV + " V, ");
            sb.append("PV System V: " + pvSystemV + " V, ");
            sb.append("I out: " + iOut + " A, ");
            sb.append("Storage Battery I: " + storageBatteryI + " A");

            sb.append(" on id = ");
            sb.append(cm.getCanId());
            s_logger.debug(sb.toString());

            privateCSReceivedData.setVOut(vOut);
            privateCSReceivedData.setStorageBatteryV(storageBatteryV);
            privateCSReceivedData.setPVSystemV(pvSystemV);
            privateCSReceivedData.setIOut(iOut);
            privateCSReceivedData.setStorageBatteryI(storageBatteryI);
            privateCSReceivedData.increaseIndex();
        }
    }

}
