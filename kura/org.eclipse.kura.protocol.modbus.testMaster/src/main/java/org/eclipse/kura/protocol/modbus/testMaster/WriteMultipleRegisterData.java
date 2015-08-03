package org.eclipse.kura.protocol.modbus.testMaster;

public class WriteMultipleRegisterData {
	public final static String START_RECHARGE= "start.recharge";
	public final static String RECHARGE_IS_BOOKED= "recharge.is.booked";
	public final static String SOLAR_RADIATION= "solar.level";
	public final static String BOOKING_TIME_HOUR= "booking.time.hour";
	public final static String BOOKING_TIME_MINUTE= "booking.time.minute";
	public final static String BOOKING_DATE_DAY= "booking.date.day";
	public final static String BOOKING_DATE_MONTH= "booking.date.month";
	public final static String BOOKING_DATE_YEAR= "booking.date.year";
	public final static String CURRENT_TIME_HOUR= "current.time.hour";
	public final static String CURRENT_TIME_MINUTE= "current.time.minute";
	public final static String CURRENT_DATE_DAY= "current.date.day";
	public final static String CURRENT_DATE_MONTH= "current.date.month";
	public final static String CURRENT_DATE_YEAR= "current.date.year";
	
	private int startRecharge;
	private int rechargeBooked;
	private int solarRadiationLevel;
	private int bookingTimeHour;
	private int bookingTimeMinute;
	private int bookingDateDay;
	private int bookingDateMonth;
	private int bookingDateYear;
	private int currentTimeHour;
	private int currentTimeMinute;
	private int currentDateDay;
	private int currentDateMonth;
	private int currentDateYear;
	public int getStartRecharge() {
		return startRecharge;
	}
	public void setStartRecharge(int startRecharge) {
		this.startRecharge = startRecharge;
	}
	public int getRechargeBooked() {
		return rechargeBooked;
	}
	public void setRechargeBooked(int rechargeBooked) {
		this.rechargeBooked = rechargeBooked;
	}
	public int getSolarRadiationLevel() {
		return solarRadiationLevel;
	}
	public void setSolarRadiationLevel(int solarRadiationLevel) {
		this.solarRadiationLevel = solarRadiationLevel;
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
	public int getCurrentDateDay() {
		return currentDateDay;
	}
	public void setCurrentDateDay(int currentDateDay) {
		this.currentDateDay = currentDateDay;
	}
	public int getCurrentDateMonth() {
		return currentDateMonth;
	}
	public void setCurrentDateMonth(int currentDateMonth) {
		this.currentDateMonth = currentDateMonth;
	}
	public int getCurrentDateYear() {
		return currentDateYear;
	}
	public void setCurrentDateYear(int currentDateYear) {
		this.currentDateYear = currentDateYear;
	}
}
