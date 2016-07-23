package org.eclipse.kura.protocol.can.recharge;

public class RechargeInfo {
    public static final String START_RECHARGE     = "start.recharge";
    public static final String RECHARGE_IS_BOOKED = "recharge.is.booked";
    public static final String SOLAR_RADIATION    = "solar.level";
    public static final String CS_RESET           = "cs.reset";

    private int startRecharge;
    private int rechargeBooked;
    private int solarRadiationLevel;
    private int csReset;

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
