package org.eclipse.kura.protocol.can.recharge;

public class BookingInfo {

    public static final String BOOKING_TIME_HOUR   = "booking.time.hour";
    public static final String BOOKING_TIME_MINUTE = "booking.time.minute";
    public static final String BOOKING_DATE_DAY    = "booking.date.day";
    public static final String BOOKING_DATE_MONTH  = "booking.date.month";
    public static final String BOOKING_DATE_YEAR   = "booking.date.year";
    public static final String CURRENT_TIME_HOUR   = "current.time.hour";
    public static final String CURRENT_TIME_MINUTE = "current.time.minute";

    private int bookingTimeHour;
    private int bookingTimeMinute;
    private int bookingDateDay;
    private int bookingDateMonth;
    private int bookingDateYear;
    private int currentTimeHour;
    private int currentTimeMinute;

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
