package org.eclipse.kura.protocol.can.messages;

import org.eclipse.kura.protocol.can.arrowhead.ArrowheadCanSocketImpl;
import org.eclipse.kura.protocol.can.recharge.BookingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class statically manages 0x201 messages that the Minigateway has to send
 * to a Charging Station. The output will be a byte array containing:
 * <li>Booking time</li>
 * <li>Booking date</li>
 * <li>Current time</li>
 *
 */
public class GWMessage0x201 {
    private static final Logger s_logger = LoggerFactory.getLogger(ArrowheadCanSocketImpl.class);
    
    private static final int MESSAGE_ID = 0x201;

    public static int getId() {
        return MESSAGE_ID;
    }

    public static byte[] createMessage(BookingInfo bookingInfo, boolean isBigEndian) {
        StringBuilder sb = new StringBuilder("Trying to create can frame message 2 with value = ");
        byte[] bMessage = new byte[8];

        int bookingTimeHour = bookingInfo.getBookingTimeHour();
        int bookingTimeMinute = bookingInfo.getBookingTimeMinute();
        int bookingDateDay = bookingInfo.getBookingDateDay();
        int bookingDateMonth = bookingInfo.getBookingDateMonth();
        int bookingDateYear = bookingInfo.getBookingDateYear();
        int currentTimeHour = bookingInfo.getCurrentTimeHour();
        int currentTimeMinute = bookingInfo.getCurrentTimeMinute();

        bMessage[0] = (byte) bookingTimeHour; // Booking time: hour
        bMessage[1] = (byte) bookingTimeMinute; // Booking time: minute
        bMessage[2] = (byte) bookingDateDay; // Booking date: day
        bMessage[3] = (byte) bookingDateMonth; // Booking date: month

        if (isBigEndian) {
            bMessage[4] = (byte) ((bookingDateYear >> 8) & 0xFF); // Booking
                                                                  // date: year
            bMessage[5] = (byte) (bookingDateYear & 0xFF); // Booking date: year
        } else {
            bMessage[4] = (byte) (bookingDateYear & 0xFF); // Booking date: year
            bMessage[5] = (byte) ((bookingDateYear >> 8) & 0xFF); // Booking
                                                                  // date: year
        }

        bMessage[6] = (byte) currentTimeHour; // Current time: hour
        bMessage[7] = (byte) currentTimeMinute; // Current time: minute

        sb.append("Booking time: hour " + bMessage[0] + ", ");
        sb.append("Booking time: minute " + bMessage[1] + ", ");
        sb.append("Booking date: day " + bMessage[2] + ", ");
        sb.append("Booking date: month " + bMessage[3] + ", ");
        if (isBigEndian) {
            sb.append("Booking date: year " + MessageUtils.buildShort(bMessage[4], bMessage[5]) + ", ");
        } else {
            sb.append("Booking date: year " + MessageUtils.buildShort(bMessage[5], bMessage[4]) + ", ");
        }
        sb.append("Current time: hour " + bMessage[6] + ", ");
        sb.append("Current time: minute " + bMessage[7]);

        sb.append(" and id = ");
        sb.append(MESSAGE_ID);
        s_logger.debug(sb.toString());

        return bMessage;
    }

}
