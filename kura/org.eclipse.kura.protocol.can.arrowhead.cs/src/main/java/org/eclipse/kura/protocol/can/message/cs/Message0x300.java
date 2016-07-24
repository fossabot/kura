package org.eclipse.kura.protocol.can.message.cs;

import java.util.Map;

public class Message0x300 {
    public static final String POWER_OUT                = "power.out";
    public static final String TIME_TO_RECHARGE_HOURS   = "time.to.recharge.hours";
    public static final String TIME_TO_RECHARGE_MINUTES = "time.to.recharge.minutes";
    public static final String ENERGY_OUT               = "energy.out";
    public static final String POWER_PV                 = "power.pv";

    private int powerOut;
    private int timeToRechargeHours;
    private int timeToRechargeMinutes;
    private int energyOut;
    private int powerPV;

    public int getPowerOut() {
        return powerOut;
    }

    public void setPowerOut(int powerOut) {
        this.powerOut = powerOut;
    }

    public int getTimeToRechargeHours() {
        return timeToRechargeHours;
    }
    
    public void setTimeToRechargeHours(int timeToRechargeHours) {
        this.timeToRechargeHours = timeToRechargeHours;
    }

    public int getTimeToRechargeMinutes() {
        return timeToRechargeMinutes;
    }

    public void setTimeToRechargeMinutes(int timeToRechargeMinutes) {
        this.timeToRechargeMinutes = timeToRechargeMinutes;
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
    
    public void populateMessageInfo(Map<String, Object> properties) {
        setPowerOut(Integer.parseInt((String) properties.get(POWER_OUT)));
        setTimeToRechargeMinutes(Integer.parseInt((String) properties.get(TIME_TO_RECHARGE_MINUTES)));
        setTimeToRechargeHours(Integer.parseInt((String) properties.get(TIME_TO_RECHARGE_HOURS)));
        setEnergyOut(Integer.parseInt((String) properties.get(ENERGY_OUT)));
        setPowerPV(Integer.parseInt((String) properties.get(POWER_PV)));
    }
}
