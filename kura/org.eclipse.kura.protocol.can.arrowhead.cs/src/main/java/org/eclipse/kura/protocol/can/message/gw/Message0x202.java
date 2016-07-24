package org.eclipse.kura.protocol.can.message.gw;

import org.eclipse.kura.protocol.can.CanMessage;
import org.eclipse.kura.protocol.can.utils.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Message0x202 {
    private static final Logger s_logger = LoggerFactory.getLogger(Message0x202.class);
    
    public static void parseGwCanMessage3(CanMessage cm, boolean isBigEndian) {
        byte[] b = cm.getData();
        if (b != null && b.length == 4) {
            StringBuilder sb = new StringBuilder("received : ");

            int currentDateDay = b[0];
            int currentDateMonth = b[1];

            int currentDateYear;
            if (isBigEndian) {
                currentDateYear = MessageUtils.buildShort(b[2], b[3]);
            } else {
                currentDateYear = MessageUtils.buildShort(b[3], b[2]);
            }

            sb.append("Current date: day " + currentDateDay + ", ");
            sb.append("Current date: month " + currentDateMonth + ", ");
            sb.append("Current date: year " + currentDateYear);

            sb.append(" on id = ");
            sb.append(cm.getCanId());
            s_logger.info(sb.toString());
        }
    }

}
