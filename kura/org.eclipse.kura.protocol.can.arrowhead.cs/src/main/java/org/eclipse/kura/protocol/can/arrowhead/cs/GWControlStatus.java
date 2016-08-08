package org.eclipse.kura.protocol.can.arrowhead.cs;

public class GWControlStatus {

	private boolean startRecharge = false;
	private boolean rechargeIsBooked = false;
	private int nextDaySolarLevel = 3;
	private boolean chargingStationReset = false;
	
	public boolean isStartRecharge() {
		return startRecharge;
	}
	public void setStartRecharge(boolean startRecharge) {
		this.startRecharge = startRecharge;
	}
	public boolean isRechargeIsBooked() {
		return rechargeIsBooked;
	}
	public void setRechargeIsBooked(boolean rechargeIsBooked) {
		this.rechargeIsBooked = rechargeIsBooked;
	}
	public int getNextDaySolarLevel() {
		return nextDaySolarLevel;
	}
	public void setNextDaySolarLevel(int nextDaySolarLevel) {
		this.nextDaySolarLevel = nextDaySolarLevel;
	}
	public boolean isChargingStationReset() {
		return chargingStationReset;
	}
	public void setChargingStationReset(boolean chargingStationReset) {
		this.chargingStationReset = chargingStationReset;
	}
	
}
