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

    private static final int MESSAGE_ID = 0x200;

    public static int getId() {
        return MESSAGE_ID;
    }
    
    public static byte[] createMessage(MotoTronDataSnapshot motoTronData) {
        StringBuilder sb = new StringBuilder("Trying to create can frame message 400 with value = ");
        byte[] bMessage = new byte[2];

        int startRecharge = motoTronData.getStartRecharge(); // start recharge
                                                             // [0,1]
        int bookingId = motoTronData.getLocalBookingId(); // local booking id
                                                          // [0,255]

        bMessage[0] = (byte) startRecharge;
        bMessage[1] = (byte) bookingId; // Replies the booking id received from
                                        // MotoTron

        sb.append(bMessage[0]);
        sb.append(" ");

        sb.append(" and id = ");
        sb.append(MESSAGE_ID);
        s_logger.debug(sb.toString());
        return bMessage;
    }

}
