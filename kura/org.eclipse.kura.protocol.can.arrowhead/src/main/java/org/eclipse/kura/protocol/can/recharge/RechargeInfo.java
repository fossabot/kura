package org.eclipse.kura.protocol.can.recharge;

public class RechargeInfo {
	public final static String START_RECHARGE= "start.recharge";
	public final static String RECHARGE_IS_BOOKED= "recharge.is.booked";
	public final static String SOLAR_RADIATION= "solar.level";
	public final static String CS_RESET= "cs.reset";
	
	private int startRecharge;
	private int rechargeBooked;
	private int solarRadiationLevel;
	private int csReset;
	
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
	public int getCsReset() {
		return csReset;
	}
	public void setCsReset(int csReset) {
		this.csReset = csReset;
	}

}
