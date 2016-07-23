package org.eclipse.kura.protocol.can.arrowhead;

public class CompressorError {
    private short code;  /* 8 bit unsigned */
    private short day;   /* 8 bit unsigned */
    private short month; /* 8 bit unsigned */
    private short year;  /* 8 bit unsigned */

    public short getCode() {
        return code;
    }

    public void setCode(short code) {
        this.code = code;
    }

    public short getDay() {
        return day;
    }

    public void setDay(short day) {
        this.day = day;
    }

    public short getMonth() {
        return month;
    }

    public void setMonth(short month) {
        this.month = month;
    }

    public short getYear() {
        return year;
    }

    public void setYear(short year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "CompressorError [code=" + code + ", day=" + day + ", month="
                + month + ", year=" + year + "]";
    }
}
