package org.eclipse.kura.protocol.can.message.cs;

import java.util.Map;

public class Message0x101 {
    private static final String FAULT_FLAG                       = "fault.flag";
    private static final String RECHARGE_AVAILABLE               = "recharge.available";
    private static final String RECHARGE_IN_PROGRESS             = "recharge.in.progress";
    private static final String PV_SYSTEM_ACTIVE                 = "pv.system.active";
    private static final String AUX_CHARGER_ACTIVE               = "aux.charger.active";
    private static final String STORAGE_BATTERY_CONTACTOR_STATUS = "storage.battery.contactor.status";
    private static final String CONVERTER_CONTACTOR_STATUS       = "converter.contactor.status";
    private static final String FAULT_STRING                     = "fault.string";
    private static final String IGBT_TEMP                        = "igbt.temp";
    private static final String STORAGE_TEMP                     = "storage.temp";
    private static final String STORAGE_BATTERY_SOC              = "storage.battery.soc";

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
    
    
    public void populateMessageInfo(Map<String, Object> properties) {
        setFaultFlag(Integer.parseInt((String) properties.get(FAULT_FLAG)));
        setRechargeAvailable(Integer.parseInt((String) properties.get(RECHARGE_AVAILABLE)));
        setRechargeInProgress(Integer.parseInt((String) properties.get(RECHARGE_IN_PROGRESS)));
        setPvSystemActive(Integer.parseInt((String) properties.get(PV_SYSTEM_ACTIVE)));
        setAuxChargerActive(Integer.parseInt((String) properties.get(AUX_CHARGER_ACTIVE)));
        setStorageBatteryContactorStatus(Integer.parseInt((String) properties.get(STORAGE_BATTERY_CONTACTOR_STATUS)));
        setConverterContactorStatus(Integer.parseInt((String) properties.get(CONVERTER_CONTACTOR_STATUS)));
        setFaultString(Integer.parseInt((String) properties.get(FAULT_STRING)));
        setIgbtTemp(Integer.parseInt((String) properties.get(IGBT_TEMP)));
        setStorageTemp(Integer.parseInt((String) properties.get(STORAGE_TEMP)));
        setStorageBatterySoc(Integer.parseInt((String) properties.get(STORAGE_BATTERY_SOC)));
    }

}
