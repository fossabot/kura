package com.eurotech.example.modbus.slave;

public class ModbusSlavePreferences {
	private int powerOut;
	private int timeToRecharge;
	private int energyOut;
	private int powerPV;
	private boolean faultFlag;
	private boolean rechargeAvailable;
	private boolean rechargeInProgress;
	private boolean pvSystemActive;
	private boolean auxChargerActive;
	private boolean storageBatteryContactorStatus;
	private boolean converterContactorStatus;
	private int faultString1;
	private int faultString2;
	private int igbtTemp;
	private int storageTemp;
	private int storageBatterySoc;
	private int vOut;
	private int storageBatteryV;
	private int pvSystemV;
	private int iOut;
	private int storageBatteryI;
	
	public int getPowerOut() {
		return powerOut;
	}
	public void setPowerOut(int powerOut) {
		this.powerOut = powerOut;
	}
	public int getTimeToRecharge() {
		return timeToRecharge;
	}
	public void setTimeToRecharge(int timeToRecharge) {
		this.timeToRecharge = timeToRecharge;
	}
	public int getEnergyOut() {
		return energyOut;
	}
	public void setEnergyOut(int energyOut) {
		this.energyOut = energyOut;
	}
	public int getPowerPV() {
		return powerPV;
	}
	public void setPowerPV(int powerPV) {
		this.powerPV = powerPV;
	}
	public boolean isFaultFlag() {
		return faultFlag;
	}
	public void setFaultFlag(boolean faultFlag) {
		this.faultFlag = faultFlag;
	}
	public boolean isRechargeAvailable() {
		return rechargeAvailable;
	}
	public void setRechargeAvailable(boolean rechargeAvailable) {
		this.rechargeAvailable = rechargeAvailable;
	}
	public boolean isRechargeInProgress() {
		return rechargeInProgress;
	}
	public void setRechargeInProgress(boolean rechargeInProgress) {
		this.rechargeInProgress = rechargeInProgress;
	}
	public boolean isPvSystemActive() {
		return pvSystemActive;
	}
	public void setPvSystemActive(boolean pvSystemActive) {
		this.pvSystemActive = pvSystemActive;
	}
	public boolean isAuxChargerActive() {
		return auxChargerActive;
	}
	public void setAuxChargerActive(boolean auxChargerActive) {
		this.auxChargerActive = auxChargerActive;
	}
	public boolean isStorageBatteryContactorStatus() {
		return storageBatteryContactorStatus;
	}
	public void setStorageBatteryContactorStatus(
			boolean storageBatteryContactorStatus) {
		this.storageBatteryContactorStatus = storageBatteryContactorStatus;
	}
	public boolean isConverterContactorStatus() {
		return converterContactorStatus;
	}
	public void setConverterContactorStatus(boolean converterContactorStatus) {
		this.converterContactorStatus = converterContactorStatus;
	}
	public int getFaultString1() {
		return faultString1;
	}
	public void setFaultString1(int faultString1) {
		this.faultString1 = faultString1;
	}
	public int getFaultString2() {
		return faultString2;
	}
	public void setFaultString2(int faultString2) {
		this.faultString2 = faultString2;
	}
	public int getIgbtTemp() {
		return igbtTemp;
	}
	public void setIgbtTemp(int igbtTemp) {
		this.igbtTemp = igbtTemp;
	}
	public int getStorageTemp() {
		return storageTemp;
	}
	public void setStorageTemp(int storageTemp) {
		this.storageTemp = storageTemp;
	}
	public int getStorageBatterySoc() {
		return storageBatterySoc;
	}
	public void setStorageBatterySoc(int storageBatterySoc) {
		this.storageBatterySoc = storageBatterySoc;
	}
	public int getvOut() {
		return vOut;
	}
	public void setvOut(int vOut) {
		this.vOut = vOut;
	}
	public int getStorageBatteryV() {
		return storageBatteryV;
	}
	public void setStorageBatteryV(int storageBatteryV) {
		this.storageBatteryV = storageBatteryV;
	}
	public int getPvSystemV() {
		return pvSystemV;
	}
	public void setPvSystemV(int pvSystemV) {
		this.pvSystemV = pvSystemV;
	}
	public int getiOut() {
		return iOut;
	}
	public void setiOut(int iOut) {
		this.iOut = iOut;
	}
	public int getStorageBatteryI() {
		return storageBatteryI;
	}
	public void setStorageBatteryI(int storageBatteryI) {
		this.storageBatteryI = storageBatteryI;
	}

}
