package org.eclipse.kura.protocol.can.messages;

import org.eclipse.kura.protocol.can.CanMessage;
import org.eclipse.kura.protocol.can.arrowhead.ArrowheadCanSocketImpl;
import org.eclipse.kura.protocol.can.cs.data.MotoTronDataSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class statically manages 0x401 messages that the Minigateway receives
 * from a Charging Station. It will receive:
 * <li>Local Booking ID</li>
 * <li>Plate characters [1-7]</li>
 *
 */
public class CSMessage0x401 {
    private static final Logger s_logger = LoggerFactory.getLogger(ArrowheadCanSocketImpl.class);

    private CSMessage0x401() {
    }

    public static void parseCanMessage(CanMessage cm, MotoTronDataSnapshot motoTronReceivedData) {
        byte[] b = cm.getData();
        if (b != null && b.length == 8) {
            StringBuilder sb = new StringBuilder("received : ");

            int localBookingId = (char) b[0];
            char plateCharacter1 = (char) b[1];
            char plateCharacter2 = (char) b[2];
            char plateCharacter3 = (char) b[3];
            char plateCharacter4 = (char) b[4];
            char plateCharacter5 = (char) b[5];
            char plateCharacter6 = (char) b[6];
            char plateCharacter7 = (char) b[7];

            sb.append("Local Booking Id: " + localBookingId + ", ");
            sb.append("Plate char1: " + plateCharacter1 + ", ");
            sb.append("Plate char2: " + plateCharacter2 + ", ");
            sb.append("Plate char3: " + plateCharacter3 + ", ");
            sb.append("Plate char4: " + plateCharacter4 + ", ");
            sb.append("Plate char5: " + plateCharacter5 + ", ");
            sb.append("Plate char6: " + plateCharacter6 + ", ");
            sb.append("Plate char7: " + plateCharacter7 + " ");

            sb.append(" on id = ");
            sb.append(cm.getCanId());
            s_logger.debug(sb.toString());

            motoTronReceivedData.setLocalBookingId(localBookingId);
            motoTronReceivedData.setVehiclePlateCharacter1(plateCharacter1);
            motoTronReceivedData.setVehiclePlateCharacter2(plateCharacter2);
            motoTronReceivedData.setVehiclePlateCharacter3(plateCharacter3);
            motoTronReceivedData.setVehiclePlateCharacter4(plateCharacter4);
            motoTronReceivedData.setVehiclePlateCharacter5(plateCharacter5);
            motoTronReceivedData.setVehiclePlateCharacter6(plateCharacter6);
            motoTronReceivedData.setVehiclePlateCharacter7(plateCharacter7);
        }
    }

}
