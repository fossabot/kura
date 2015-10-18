package org.eclipse.kura.protocol.can.recharge;

import java.util.Map;

public class BookingInfo {
	private final static String BOOKING_TIME_HOUR= "booking.time.hour";
	private final static String BOOKING_TIME_MINUTE= "booking.time.minute";
	private final static String BOOKING_DATE_DAY= "booking.date.day";
	private final static String BOOKING_DATE_MONTH= "booking.date.month";
	private final static String BOOKING_DATE_YEAR= "booking.date.year";
	private final static String CURRENT_TIME_HOUR= "current.time.hour";
	private final static String CURRENT_TIME_MINUTE= "current.time.minute";
	
	private int bookingTimeHour;
	private int bookingTimeMinute;
	private int bookingDateDay;
	private int bookingDateMonth;
	private int bookingDateYear;
	private int currentTimeHour;
	private int currentTimeMinute;
	
	public BookingInfo(Map<String,Object> properties){
		int bookingTimeHour = Integer.parseInt((String) properties.get(BOOKING_TIME_HOUR));
		int bookingTimeMinute = Integer.parseInt((String) properties.get(BOOKING_TIME_MINUTE));
		int bookingDateDay = Integer.parseInt((String) properties.get(BOOKING_DATE_DAY));
		int bookingDateMonth = Integer.parseInt((String) properties.get(BOOKING_DATE_MONTH));
		int bookingDateYear = Integer.parseInt((String) properties.get(BOOKING_DATE_YEAR));
		int currentTimeHour = Integer.parseInt((String) properties.get(CURRENT_TIME_HOUR));
		int currentTimeMinute = Integer.parseInt((String) properties.get(CURRENT_TIME_MINUTE));

		this.setBookingTimeHour(bookingTimeHour);
		this.setBookingTimeMinute(bookingTimeMinute);
		this.setBookingDateDay(bookingDateDay);
		this.setBookingDateMonth(bookingDateMonth);
		this.setBookingDateYear(bookingDateYear);
		this.setCurrentTimeHour(currentTimeHour);
		this.setCurrentTimeMinute(currentTimeMinute);
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
