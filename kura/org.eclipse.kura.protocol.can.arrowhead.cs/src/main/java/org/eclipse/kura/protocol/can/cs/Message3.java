package org.eclipse.kura.protocol.can.cs;

public class Message3 {
	public final static String V_OUT= "v.out";
	public final static String STORAGE_BATTERY_V= "storage.battery.v";
	public final static String PV_SYSTEM_V= "pv.system.v";
	public final static String I_OUT= "i.out";
	public final static String STORAGE_BATTERY_I = "storage.battery.i";
	
	private int vOut;
	private int storageBatteryV;
	private int pvSystemV;
	private int iOut;
	private int storageBatteryI;
	
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
