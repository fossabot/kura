package org.eclipse.kura.protocol.can.recharge;

import java.util.Map;

public class BookingInfo {
    private static final String BOOKING_TIME_HOUR   = "booking.time.hour";
    private static final String BOOKING_TIME_MINUTE = "booking.time.minute";
    private static final String BOOKING_DATE_DAY    = "booking.date.day";
    private static final String BOOKING_DATE_MONTH  = "booking.date.month";
    private static final String BOOKING_DATE_YEAR   = "booking.date.year";
    private static final String CURRENT_TIME_HOUR   = "current.time.hour";
    private static final String CURRENT_TIME_MINUTE = "current.time.minute";

    private int bookingTimeHour;
    private int bookingTimeMinute;
    private int bookingDateDay;
    private int bookingDateMonth;
    private int bookingDateYear;
    private int currentTimeHour;
    private int currentTimeMinute;

    public BookingInfo(Map<String, Object> properties) {
        bookingTimeHour = Integer.parseInt((String) properties.get(BOOKING_TIME_HOUR));
        bookingTimeMinute = Integer.parseInt((String) properties.get(BOOKING_TIME_MINUTE));
        bookingDateDay = Integer.parseInt((String) properties.get(BOOKING_DATE_DAY));
        bookingDateMonth = Integer.parseInt((String) properties.get(BOOKING_DATE_MONTH));
        bookingDateYear = Integer.parseInt((String) properties.get(BOOKING_DATE_YEAR));
        currentTimeHour = Integer.parseInt((String) properties.get(CURRENT_TIME_HOUR));
        currentTimeMinute = Integer.parseInt((String) properties.get(CURRENT_TIME_MINUTE));
    }

    public int getBookingTimeHour() {
        return bookingTimeHour;
    }

    public void setBookingTimeHour(int bookingTimeHour) {
        this.bookingTimeHour = bookingTimeHour;
    }

    public int getBookingTimeMinute() {
        return bookingTimeMinute;
    }

    public void setBookingTimeMinute(int bookingTimeMinute) {
        this.bookingTimeMinute = bookingTimeMinute;
    }

    public int getBookingDateDay() {
        return bookingDateDay;
    }

    public void setBookingDateDay(int bookingDateDay) {
        this.bookingDateDay = bookingDateDay;
    }

    public int getBookingDateMonth() {
        return bookingDateMonth;
    }

    public void setBookingDateMonth(int bookingDateMonth) {
        this.bookingDateMonth = bookingDateMonth;
    }

    public int getBookingDateYear() {
        return bookingDateYear;
    }

    public void setBookingDateYear(int bookingDateYear) {
        this.bookingDateYear = bookingDateYear;
    }

    public int getCurrentTimeHour() {
        return currentTimeHour;
    }

    public void setCurrentTimeHour(int currentTimeHour) {
        this.currentTimeHour = currentTimeHour;
    }

    public int getCurrentTimeMinute() {
        return currentTimeMinute;
    }

    public void setCurrentTimeMinute(int currentTimeMinute) {
        this.currentTimeMinute = currentTimeMinute;
    }
}
