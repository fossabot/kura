package org.eclipse.kura.protocol.can.messages;

import org.eclipse.kura.protocol.can.arrowhead.ArrowheadCanSocketImpl;
import org.eclipse.kura.protocol.can.recharge.RechargeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class statically manages 0x200 messages that the Minigateway has to send
 * to a Charging Station. The output will be a byte array containing:
 * <li>Fault_Flag</li>
 * <li>Start_Recharge</li>
 * <li>Recharge is booked?</li>
 * <li>Next day solar radiation level</li>
 * <li>Charging Station Reset</li>
 *
 */
public class GWMessage0x200 {
    private static final Logger s_logger = LoggerFactory.getLogger(ArrowheadCanSocketImpl.class);
    
    private static final int MESSAGE_ID = 0x200;

    public static int getId() {
        return MESSAGE_ID;
    }

    public static byte[] createMessage(RechargeInfo rechargeInfo) {
        StringBuilder sb = new StringBuilder("Trying to create can frame message 1 with value = ");
        byte[] bMessage = new byte[1];
        int startRecharge = rechargeInfo.getStartRecharge(); // start recharge [0,1]
        int isBooked = rechargeInfo.getRechargeBooked() << 1; // Recharge is booked? [0-No;1-Yes]
        int solarIrradiation = rechargeInfo.getSolarRadiationLevel() << 2; // Next Day Solar Radiation Level [0-Low; 1-Medium; 2-High]
        int csReset = rechargeInfo.getCsReset() << 4; // Charging station reset [0-No;1-Yes]

        bMessage[0] = (byte) (startRecharge + isBooked + solarIrradiation + csReset); // Panel PC Start/Stop/Booking/Next day weather forecast/CS Reset

        sb.append(bMessage[0]);
        sb.append(" ");

        sb.append(" and id = ");
        sb.append(MESSAGE_ID);
        s_logger.debug(sb.toString());
        return bMessage;
    }

}
