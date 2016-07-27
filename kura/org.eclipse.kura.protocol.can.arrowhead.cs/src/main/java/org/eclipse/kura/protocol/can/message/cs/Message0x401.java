package org.eclipse.kura.protocol.can.message.cs;

import java.util.Map;

public class Message0x401 {
    public static final String LOCAL_BOOKING_ID = "local.booking.id";
    public static final String PLATE_CHAR_1     = "plate.char.1";
    public static final String PLATE_CHAR_2     = "plate.char.2";
    public static final String PLATE_CHAR_3     = "plate.char.3";
    public static final String PLATE_CHAR_4     = "plate.char.4";
    public static final String PLATE_CHAR_5     = "plate.char.5";
    public static final String PLATE_CHAR_6     = "plate.char.6";
    public static final String PLATE_CHAR_7     = "plate.char.7";

    private int  localBookingId;
    private char plateChar1;
    private char plateChar2;
    private char plateChar3;
    private char plateChar4;
    private char plateChar5;
    private char plateChar6;
    private char plateChar7;

    public int getLocalBookingId() {
        return localBookingId;
    }

    public char getPlateChar1() {
        return plateChar1;
    }

    public char getPlateChar2() {
        return plateChar2;
    }

    public char getPlateChar3() {
        return plateChar3;
    }

    public char getPlateChar4() {
        return plateChar4;
    }

    public char getPlateChar5() {
        return plateChar5;
    }

    public char getPlateChar6() {
        return plateChar6;
    }

    public char getPlateChar7() {
        return plateChar7;
    }

    public void setLocalBookingId(int localBookingId) {
        this.localBookingId = localBookingId;
    }

    public void setPlateChar1(char plateChar1) {
        this.plateChar1 = plateChar1;
    }

    public void setPlateChar2(char plateChar2) {
        this.plateChar2 = plateChar2;
    }

    public void setPlateChar3(char plateChar3) {
        this.plateChar3 = plateChar3;
    }

    public void setPlateChar4(char plateChar4) {
        this.plateChar4 = plateChar4;
    }

    public void setPlateChar5(char plateChar5) {
        this.plateChar5 = plateChar5;
    }

    public void setPlateChar6(char plateChar6) {
        this.plateChar6 = plateChar6;
    }

    public void setPlateChar7(char plateChar7) {
        this.plateChar7 = plateChar7;
    }
    
    public void populateMessageInfo(Map<String, Object> properties) {
        setLocalBookingId(Integer.parseInt((String) properties.get(LOCAL_BOOKING_ID)));
        setPlateChar1((Character) properties.get(PLATE_CHAR_1));
        setPlateChar2((Character) properties.get(PLATE_CHAR_2));
        setPlateChar3((Character) properties.get(PLATE_CHAR_3));
        setPlateChar4((Character) properties.get(PLATE_CHAR_4));
        setPlateChar5((Character) properties.get(PLATE_CHAR_5));
        setPlateChar6((Character) properties.get(PLATE_CHAR_6));
        setPlateChar7((Character) properties.get(PLATE_CHAR_7));
    }
}
