package org.eclipse.kura.protocol.can.message.gw;

import org.eclipse.kura.protocol.can.CanMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Message0x400 {
    private static final Logger s_logger = LoggerFactory.getLogger(Message0x400.class);

    public static void parseGwCanMessage(CanMessage cm) {
        byte[] b = cm.getData();
        if (b != null && b.length == 1) {
            StringBuilder sb = new StringBuilder("received : ");

            int localBookingId = b[0]; // local booking id [0,255]

            sb.append("Local booking id: " + localBookingId + " ");

            sb.append(" on id = ");
            sb.append(cm.getCanId());
            s_logger.info(sb.toString());
        }
    }
}
