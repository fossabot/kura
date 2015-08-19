package org.eclipse.kura.protocol.can.recharge;

public class CurrentDateInfo {
	public final static String CURRENT_DATE_DAY= "current.date.day";
	public final static String CURRENT_DATE_MONTH= "current.date.month";
	public final static String CURRENT_DATE_YEAR= "current.date.year";
	
	private int currentDateDay;
	private int currentDateMonth;
	private int currentDateYear;
	
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
