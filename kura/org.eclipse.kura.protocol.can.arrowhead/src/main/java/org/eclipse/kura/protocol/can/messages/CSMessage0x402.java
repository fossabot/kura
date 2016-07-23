package org.eclipse.kura.protocol.can.messages;

import org.eclipse.kura.protocol.can.CanMessage;
import org.eclipse.kura.protocol.can.arrowhead.ArrowheadCanSocketImpl;
import org.eclipse.kura.protocol.can.cs.data.MotoTronDataSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class statically manages 0x402 messages that the Minigateway receives
 * from MotoTron. The output will be a byte array containing:
 * <li>Plate Character [8-12]</li>
 *
 */
public class CSMessage0x402 {
    private static final Logger s_logger = LoggerFactory.getLogger(ArrowheadCanSocketImpl.class);

    private CSMessage0x402() {
    }

    public static void parseCanMessage(CanMessage cm, MotoTronDataSnapshot motoTronReceivedData) {
        byte[] b = cm.getData();
        if (b != null && b.length == 6) {
            StringBuilder sb = new StringBuilder("received : ");

            int localBookingId = (char) b[0];
            char plateCharacter8 = (char) b[1];
            char plateCharacter9 = (char) b[2];
            char plateCharacter10 = (char) b[3];
            char plateCharacter11 = (char) b[4];
            char plateCharacter12 = (char) b[5];

            sb.append("Local Booking Id: " + localBookingId + ", ");
            sb.append("Plate char8: " + plateCharacter8 + ", ");
            sb.append("Plate char9: " + plateCharacter9 + ", ");
            sb.append("Plate char10: " + plateCharacter10 + ", ");
            sb.append("Plate char11: " + plateCharacter11 + ", ");
            sb.append("Plate char12: " + plateCharacter12 + " ");

            sb.append(" on id = ");
            sb.append(cm.getCanId());
            s_logger.debug(sb.toString());

            motoTronReceivedData.setLocalBookingId(localBookingId);
            motoTronReceivedData.setVehiclePlateCharacter8(plateCharacter8);
            motoTronReceivedData.setVehiclePlateCharacter9(plateCharacter9);
            motoTronReceivedData.setVehiclePlateCharacter10(plateCharacter10);
            motoTronReceivedData.setVehiclePlateCharacter11(plateCharacter11);
            motoTronReceivedData.setVehiclePlateCharacter12(plateCharacter12);
        }
    }

}
