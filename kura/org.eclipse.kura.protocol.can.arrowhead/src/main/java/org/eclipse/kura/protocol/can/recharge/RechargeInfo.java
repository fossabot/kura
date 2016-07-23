package org.eclipse.kura.protocol.can.recharge;

import java.util.Map;

public class RechargeInfo {
    private final static String START_RECHARGE     = "start.recharge";
    private final static String RECHARGE_IS_BOOKED = "recharge.is.booked";
    private final static String SOLAR_RADIATION    = "solar.level";
    private final static String CS_RESET           = "cs.reset";

    private int startRecharge;
    private int rechargeBooked;
    private int solarRadiationLevel;
    private int csReset;

    public RechargeInfo(Map<String, Object> properties) {
        startRecharge = Integer.parseInt((String) properties.get(START_RECHARGE));
        rechargeBooked = Integer.parseInt((String) properties.get(RECHARGE_IS_BOOKED));
        solarRadiationLevel = Integer.parseInt((String) properties.get(SOLAR_RADIATION));
        csReset = Integer.parseInt((String) properties.get(CS_RESET));
    }

    public int getStartRecharge() {
        return startRecharge;
    }

    public void setStartRecharge(int startRecharge) {
        this.startRecharge = startRecharge;
    }

    public int getRechargeBooked() {
        return rechargeBooked;
    }

    public void setRechargeBooked(int rechargeBooked) {
        this.rechargeBooked = rechargeBooked;
    }

    public int getSolarRadiationLevel() {
        return solarRadiationLevel;
    }

    public void setSolarRadiationLevel(int solarRadiationLevel) {
        this.solarRadiationLevel = solarRadiationLevel;
    }

    public int getCsReset() {
        return csReset;
    }

    public void setCsReset(int csReset) {
        this.csReset = csReset;
    }
}
