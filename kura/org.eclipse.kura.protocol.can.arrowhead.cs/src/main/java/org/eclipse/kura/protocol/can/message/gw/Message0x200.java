package org.eclipse.kura.protocol.can.message.gw;

import org.eclipse.kura.protocol.can.CanMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Message0x200 {
    private static final Logger s_logger = LoggerFactory.getLogger(Message0x200.class);

    public static void parseGwCanMessage(CanMessage cm) {
        byte[] b = cm.getData();
        if (b != null && b.length == 1) {
            StringBuilder sb = new StringBuilder("received 0x200: ");

            int startRecharge = b[0] & 0x01; // start recharge [0,1]
            int isBooked = (b[0] & 0x02) >> 1; // Recharge is booked?
                                               // [0-No;1-Yes]
            int solarIrradiation = (b[0] & 0x0C) >> 2; // Next Day Solar
                                                       // Radiation Level
                                                       // [0-Low; 1-Medium;
                                                       // 2-High]
            int csReset = (b[0] & 0x10) >> 4; // Charging station reset
                                              // [0-No;1-Yes]

            sb.append("start recharge: " + startRecharge + ", ");
            sb.append("Recharge is booked?: " + isBooked + ", ");
            sb.append("Next Day Solar Radiation Level: " + solarIrradiation + ", ");
            sb.append("Charging station reset: " + csReset);

            sb.append(" on id = ");
            sb.append(cm.getCanId());
            s_logger.info(sb.toString());
        }
    }

}
