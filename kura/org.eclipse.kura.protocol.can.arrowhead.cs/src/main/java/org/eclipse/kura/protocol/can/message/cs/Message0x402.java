package org.eclipse.kura.protocol.can.message.cs;

public class Message0x402 {
    public static final String LOCAL_BOOKING_ID = "local.booking.id";
    public static final String PLATE_CHAR_8     = "plate.char.8";
    public static final String PLATE_CHAR_9     = "plate.char.9";
    public static final String PLATE_CHAR_10    = "plate.char.10";
    public static final String PLATE_CHAR_11    = "plate.char.11";
    public static final String PLATE_CHAR_12    = "plate.char.12";

    private int  localBookingId;
    private char plateChar8;
    private char plateChar9;
    private char plateChar10;
    private char plateChar11;
    private char plateChar12;
    
    public int getLocalBookingId() {
        return localBookingId;
    }
    public char getPlateChar8() {
        return plateChar8;
    }
    public char getPlateChar9() {
        return plateChar9;
    }
    public char getPlateChar10() {
        return plateChar10;
    }
    public char getPlateChar11() {
        return plateChar11;
    }
    public char getPlateChar12() {
        return plateChar12;
    }
    public void setLocalBookingId(int localBookingId) {
        this.localBookingId = localBookingId;
    }
    public void setPlateChar8(char plateChar8) {
        this.plateChar8 = plateChar8;
    }
    public void setPlateChar9(char plateChar9) {
        this.plateChar9 = plateChar9;
    }
    public void setPlateChar10(char plateChar10) {
        this.plateChar10 = plateChar10;
    }
    public void setPlateChar11(char plateChar11) {
        this.plateChar11 = plateChar11;
    }
    public void setPlateChar12(char plateChar12) {
        this.plateChar12 = plateChar12;
    }
}
