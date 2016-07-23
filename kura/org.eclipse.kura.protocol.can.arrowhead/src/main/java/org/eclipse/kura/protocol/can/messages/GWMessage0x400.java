package org.eclipse.kura.protocol.can.messages;

import org.eclipse.kura.protocol.can.arrowhead.ArrowheadCanSocketImpl;
import org.eclipse.kura.protocol.can.cs.data.MotoTronDataSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class statically manages 0x400 messages that the Minigateway has to send
 * to a Charging Station. The output will be a byte array containing:
 * <li>Local booking ID</li>
 *
 */
public class GWMessage0x400 {
    private static final Logger s_logger = LoggerFactory.getLogger(ArrowheadCanSocketImpl.class);

    public static byte[] createMessage(int id, MotoTronDataSnapshot motoTronData) {
        StringBuilder sb = new StringBuilder("Trying to create can frame message 400 with value = ");
        byte[] bMessage = new byte[1];
        int bookingId = motoTronData.getLocalBookingId(); // local booking id [0,255]

        bMessage[0] = (byte) bookingId; // Replies the booking id received from MotoTron

        sb.append(bMessage[0]);
        sb.append(" ");

        sb.append(" and id = ");
        sb.append(id);
        s_logger.debug(sb.toString());
        return bMessage;
    }

}
