package org.eclipse.kura.protocol.can.cs;

public class Message3 {
    public static final String V_OUT             = "v.out";
    public static final String STORAGE_BATTERY_V = "storage.battery.v";
    public static final String PV_SYSTEM_V       = "pv.system.v";
    public static final String I_OUT             = "i.out";
    public static final String STORAGE_BATTERY_I = "storage.battery.i";

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
