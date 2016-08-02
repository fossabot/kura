package org.eclipse.kura.protocol.can.cs.data;

public class PublicCSDataSnapshot {

    // Message 0x100
    private int powerOut;
    private int minutesToRecharge;
    private int secondsToRecharge;
    private int energyOut;
    private int powerPV;

    // Message 0x101
    private int faultFlag;
    private int rechargeAvailable;
    private int rechargeInProgress;
    private int pvSystemActive;
    private int auxChargerActive;
    private int storageBatterySts;
    private int converterContactorSts;
    private int storageBatteryChargerStatus;
    private int faultString;
    private int igbtTemp;
    private int storageBatteryTemp;
    private int storageBatterySOC;

    // Message 0x102
    private int vOut;
    private int storageBatteryV;
    private int pvSystemV;
    private int iOut;
    private int storageBatteryI;
    private int index;

    public PublicCSDataSnapshot() {
        powerOut = 0;
        minutesToRecharge = 0;
        secondsToRecharge = 0;
        energyOut = 0;
        powerPV = 0;
        faultFlag = 0;
        rechargeAvailable = 0;
        rechargeInProgress = 0;
        pvSystemActive = 0;
        auxChargerActive = 0;
        storageBatterySts = 0;
        converterContactorSts = 0;
        storageBatteryChargerStatus = 0;
        faultString = 0;
        igbtTemp = 0;
        storageBatteryTemp = 0;
        storageBatterySOC = 0;
        vOut = 0;
        storageBatteryV = 0;
        pvSystemV = 0;
        iOut = 0;
        storageBatteryI = 0;
        index = 0;
    }

    public void resetData() {
        powerOut = 0;
        minutesToRecharge = 0;
        secondsToRecharge = 0;
        energyOut = 0;
        powerPV = 0;
        faultFlag = 0;
        rechargeAvailable = 0;
        rechargeInProgress = 0;
        pvSystemActive = 0;
        auxChargerActive = 0;
        storageBatterySts = 0;
        converterContactorSts = 0;
        storageBatteryChargerStatus = 0;
        faultString = 0;
        igbtTemp = 0;
        storageBatteryTemp = 0;
        storageBatterySOC = 0;
        vOut = 0;
        storageBatteryV = 0;
        pvSystemV = 0;
        iOut = 0;
        storageBatteryI = 0;
        index = 0;
    }

    public void setFaultFlag(int faultFlag) {
        this.faultFlag = faultFlag;
    }

    public void setPowerOut(int powerOut) {
        this.powerOut = Math.max(this.powerOut, powerOut);
    }

    public void setTimeToRecharge(int minutes, int seconds) {
        if (minutes < this.minutesToRecharge) {
            return;
        } else if (minutes == this.minutesToRecharge) {
            if (seconds <= this.secondsToRecharge) {
                return;
            } else {
                this.secondsToRecharge = seconds;
            }
        } else {
            this.minutesToRecharge = minutes;
            this.secondsToRecharge = seconds;
        }
    }

    public void setEnergy(int energy) {
        this.energyOut = Math.max(this.energyOut, energy);
    }

    public void setPowerPV(int power) {
        this.powerPV = Math.max(this.powerPV, power);
    }

    public void setRechargeAvail(int recAvail) {
        this.rechargeAvailable = recAvail;
    }

    public void setRechargeInProgress(int recInProgress) {
        this.rechargeInProgress = recInProgress;
    }

    public void setPVSystemActive(int pvSystemActive) {
        this.pvSystemActive = pvSystemActive;
    }

    public void setAuxChargerActive(int auxChargerActive) {
        this.auxChargerActive = auxChargerActive;
    }

    public void setStorageBatterySts(int storageBatterySts) {
        this.storageBatterySts = storageBatterySts;
    }

    public void setConverterContactorSts(int converterContactorSts) {
        this.converterContactorSts = converterContactorSts;
    }
    
    public void setStorageBatteryChargerStatus(int storageBatteryChargerStatus) {
    	this.storageBatteryChargerStatus = storageBatteryChargerStatus;
    }
    
    public void setFaultString(int faultString) {
        this.faultString = faultString;
    }

    public void setIGBTTemp(int iGBTTemp) {
        this.igbtTemp = Math.max(this.igbtTemp, iGBTTemp);
    }

    public void setStorageBatteryTemp(int storageBatteryTemp) {
        this.storageBatteryTemp = Math.max(this.storageBatteryTemp, storageBatteryTemp);
    }

    public void setStorageBatterySOC(int storageBatterySOC) {
        this.storageBatterySOC = Math.max(this.storageBatterySOC, storageBatterySOC);
    }

    public void setVOut(int vOut) {
        this.vOut = this.vOut + vOut;
    }

    public void setStorageBatteryV(int storageBatteryV) {
        this.storageBatteryV = storageBatteryV;
    }

    public void setPVSystemV(int pvSystemV) {
        this.pvSystemV = Math.max(this.pvSystemV, pvSystemV);
    }

    public void setIOut(int iOut) {
        this.iOut = this.iOut + iOut;
    }

    public void setStorageBatteryI(int storageBatteryI) {
        this.storageBatteryI = this.storageBatteryI + storageBatteryI;
    }
    
    public int getFaultFlag() {
        return faultFlag;
    }

    public int getPowerOut() {
        return powerOut;
    }

    public int getMinutesToRecharge() {
        return minutesToRecharge;
    }

    public int getSecondsToRecharge() {
        return secondsToRecharge;
    }

    public int getEnergyOut() {
        return energyOut;
    }

    public int getPowerPV() {
        return powerPV;
    }

    public int getRechargeAvailable() {
        return rechargeAvailable;
    }

    public int getRechargeInProgress() {
        return rechargeInProgress;
    }

    public int getPvSystemActive() {
        return pvSystemActive;
    }

    public int getAuxChargerActive() {
        return auxChargerActive;
    }

    public int getStorageBatterySts() {
        return storageBatterySts;
    }

    public int getConverterContactorSts() {
        return converterContactorSts;
    }
    
    public int getStorageBatteryChargerStatus() {
    	return storageBatteryChargerStatus;
    }

    public int getFaultString() {
        return faultString;
    }
    
    public int getIgbtTemp() {
        return igbtTemp;
    }

    public int getStorageBatteryTemp() {
        return storageBatteryTemp;
    }

    public int getStorageBatterySOC() {
        return storageBatterySOC;
    }

    public int getvOut() {
        if (index != 0) {
            return Math.round(vOut / index);
        }
        return 0;
    }

    public int getStorageBatteryV() {
        return storageBatteryV;
    }

    public int getPvSystemV() {
        return pvSystemV;
    }

    public int getiOut() {
        if (index != 0) {
            return Math.round(iOut / index);
        }
        return 0;
    }

    public int getStorageBatteryI() {
        if (index != 0) {
            return Math.round(storageBatteryI / index);
        }
        return 0;
    }

    public void increaseIndex() {
        index++;
    }

}
