package org.eclipse.kura.protocol.can.cs;

public class Message2 {
    public static final String FAULT_FLAG                       = "fault.flag";
    public static final String RECHARGE_AVAILABLE               = "recharge.available";
    public static final String RECHARGE_IN_PROGRESS             = "recharge.in.progress";
    public static final String PV_SYSTEM_ACTIVE                 = "pv.system.active";
    public static final String AUX_CHARGER_ACTIVE               = "aux.charger.active";
    public static final String STORAGE_BATTERY_CONTACTOR_STATUS = "storage.battery.contactor.status";
    public static final String CONVERTER_CONTACTOR_STATUS       = "converter.contactor.status";
    public static final String FAULT_STRING                     = "fault.string";
    public static final String IGBT_TEMP                        = "igbt.temp";
    public static final String STORAGE_TEMP                     = "storage.temp";
    public static final String STORAGE_BATTERY_SOC              = "storage.battery.soc";

    private int faultFlag;
    private int rechargeAvailable;
    private int rechargeInProgress;
    private int pvSystemActive;
    private int auxChargerActive;
    private int storageBatteryContactorStatus;
    private int converterContactorStatus;
    private int faultString;
    private int igbtTemp;
    private int storageTemp;
    private int storageBatterySoc;

    public int isFaultFlag() {
        return faultFlag;
    }

    public void setFaultFlag(int faultFlag) {
        this.faultFlag = faultFlag;
    }

    public int isRechargeAvailable() {
        return rechargeAvailable;
    }

    public void setRechargeAvailable(int rechargeAvailable) {
        this.rechargeAvailable = rechargeAvailable;
    }

    public int isRechargeInProgress() {
        return rechargeInProgress;
    }

    public void setRechargeInProgress(int rechargeInProgress) {
        this.rechargeInProgress = rechargeInProgress;
    }

    public int isPvSystemActive() {
        return pvSystemActive;
    }

    public void setPvSystemActive(int pvSystemActive) {
        this.pvSystemActive = pvSystemActive;
    }

    public int isAuxChargerActive() {
        return auxChargerActive;
    }

    public void setAuxChargerActive(int auxChargerActive) {
        this.auxChargerActive = auxChargerActive;
    }

    public int isStorageBatteryContactorStatus() {
        return storageBatteryContactorStatus;
    }

    public void setStorageBatteryContactorStatus(int storageBatteryContactorStatus) {
        this.storageBatteryContactorStatus = storageBatteryContactorStatus;
    }

    public int isConverterContactorStatus() {
        return converterContactorStatus;
    }

    public void setConverterContactorStatus(int converterContactorStatus) {
        this.converterContactorStatus = converterContactorStatus;
    }

    public int getFaultString() {
        return faultString;
    }

    public void setFaultString(int faultString) {
        this.faultString = faultString;
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

}
