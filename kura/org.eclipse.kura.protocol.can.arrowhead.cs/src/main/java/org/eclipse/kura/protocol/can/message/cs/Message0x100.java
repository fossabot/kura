package org.eclipse.kura.protocol.can.message.cs;

import java.util.Map;

public class Message0x100 {
    private static final String POWER_OUT                = "power.out";
    private static final String TIME_TO_RECHARGE_MINUTES = "time.to.recharge.minutes";
    private static final String TIME_TO_RECHARGE_SECONDS = "time.to.recharge.seconds";
    private static final String ENERGY_OUT               = "energy.out";
    private static final String POWER_PV                 = "power.pv";

    private int powerOut;
    private int timeToRechargeMinutes;
    private int timeToRechargeSeconds;
    private int energyOut;
    private int powerPV;

    public int getPowerOut() {
        return powerOut;
    }

    public void setPowerOut(int powerOut) {
        this.powerOut = powerOut;
    }

    public int getTimeToRechargeMinutes() {
        return timeToRechargeMinutes;
    }

    public void setTimeToRechargeMinutes(int timeToRechargeMinutes) {
        this.timeToRechargeMinutes = timeToRechargeMinutes;
    }

    public int getTimeToRechargeSeconds() {
        return timeToRechargeSeconds;
    }

    public void setTimeToRechargeSeconds(int timeToRechargeSeconds) {
        this.timeToRechargeSeconds = timeToRechargeSeconds;
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
        setTimeToRechargeSeconds(Integer.parseInt((String) properties.get(TIME_TO_RECHARGE_SECONDS)));
        setEnergyOut(Integer.parseInt((String) properties.get(ENERGY_OUT)));
        setPowerPV(Integer.parseInt((String) properties.get(POWER_PV)));
    }
}
