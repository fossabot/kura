package org.eclipse.kura.protocol.can.message.gw;

import org.eclipse.kura.protocol.can.CanMessage;
import org.eclipse.kura.protocol.can.utils.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Message0x201 {
    private static final Logger s_logger = LoggerFactory.getLogger(Message0x201.class);
    
    public static void parseGwCanMessage(CanMessage cm, boolean isBigEndian) {
        byte[] b = cm.getData();
        if (b != null && b.length == 8) {
            StringBuilder sb = new StringBuilder("received 0x201: ");

            int bookingTimeHour = b[0]; // Booking time: hour
            int bookingTimeMinute = b[1]; // Booking time: minute
            int bookingDateDay = b[2]; // Booking date: day
            int bookingDateMonth = b[3]; // Booking date: month

            int bookingDateYear;
            if (isBigEndian) {
                bookingDateYear = MessageUtils.buildShort(b[4], b[5]); // Booking date: year
            } else {
                bookingDateYear = MessageUtils.buildShort(b[5], b[4]); // Booking date: year
            }
            int currentTimeHour = b[6]; // Current time: hour
            int currentTimeMinute = b[7]; // Current time: minute

            sb.append("Booking time: hour " + bookingTimeHour + ", ");
            sb.append("Booking time: minute " + bookingTimeMinute + ", ");
            sb.append("Booking date: day " + bookingDateDay + ", ");
            sb.append("Booking date: month " + bookingDateMonth + ", ");
            sb.append("Booking date: year " + bookingDateYear + ", ");
            sb.append("Current time: hour " + currentTimeHour + ", ");
            sb.append("Current time: minute " + currentTimeMinute);

            sb.append(" on id = ");
            sb.append(cm.getCanId());
            // s_logger.info(sb.toString());
        }
    }

}
