package org.eclipse.kura.protocol.can.message.cs;

import java.util.Map;

public class Message0x302 {
    private static final String V_OUT             = "v.out";
    private static final String STORAGE_BATTERY_V = "storage.battery.v";
    private static final String PV_SYSTEM_V       = "pv.system.v";
    private static final String I_OUT             = "i.out";
    private static final String STORAGE_BATTERY_I = "storage.battery.i";

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
    
    public void populateMessageInfo(Map<String, Object>  properties) {
        setvOut(Integer.parseInt((String) properties.get(V_OUT)));
        setStorageBatteryV(Integer.parseInt((String) properties.get(STORAGE_BATTERY_V)));
        setPvSystemV(Integer.parseInt((String) properties.get(PV_SYSTEM_V)));
        setiOut(Integer.parseInt((String) properties.get(I_OUT)));
        setStorageBatteryI(Integer.parseInt((String) properties.get(STORAGE_BATTERY_I)));

    }

}
