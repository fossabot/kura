package org.eclipse.kura.protocol.can.cs.data;

public class MotoTronDataSnapshot {

    private int accessIndex;

    //Parameters for MotoTron
    private int localBookingId;
    private char vehiclePlateCharacter1;
    private char vehiclePlateCharacter2;
    private char vehiclePlateCharacter3;
    private char vehiclePlateCharacter4;
    private char vehiclePlateCharacter5;
    private char vehiclePlateCharacter6;
    private char vehiclePlateCharacter7;
    private char vehiclePlateCharacter8;
    private char vehiclePlateCharacter9;
    private char vehiclePlateCharacter10;
    private char vehiclePlateCharacter11;
    private char vehiclePlateCharacter12;


    public int getLocalBookingId() {
        return localBookingId;
    }

    public void setLocalBookingId(int localBookingId) {
        this.localBookingId = localBookingId;
        accessIndex++;
    }

    public char getVehiclePlateCharacter1() {
        return vehiclePlateCharacter1;
    }

    public char getVehiclePlateCharacter2() {
        return vehiclePlateCharacter2;
    }

    public char getVehiclePlateCharacter3() {
        return vehiclePlateCharacter3;
    }

    public char getVehiclePlateCharacter4() {
        return vehiclePlateCharacter4;
    }

    public char getVehiclePlateCharacter5() {
        return vehiclePlateCharacter5;
    }

    public char getVehiclePlateCharacter6() {
        return vehiclePlateCharacter6;
    }

    public char getVehiclePlateCharacter7() {
        return vehiclePlateCharacter7;
    }

    public char getVehiclePlateCharacter8() {
        return vehiclePlateCharacter8;
    }

    public char getVehiclePlateCharacter9() {
        return vehiclePlateCharacter9;
    }

    public char getVehiclePlateCharacter10() {
        return vehiclePlateCharacter10;
    }

    public char getVehiclePlateCharacter11() {
        return vehiclePlateCharacter11;
    }

    public char getVehiclePlateCharacter12() {
        return vehiclePlateCharacter12;
    }

    public void setVehiclePlateCharacter1(char vehiclePlateCharacter1) {
        this.vehiclePlateCharacter1 = vehiclePlateCharacter1;
    }

    public void setVehiclePlateCharacter2(char vehiclePlateCharacter2) {
        this.vehiclePlateCharacter2 = vehiclePlateCharacter2;
    }

    public void setVehiclePlateCharacter3(char vehiclePlateCharacter3) {
        this.vehiclePlateCharacter3 = vehiclePlateCharacter3;
    }

    public void setVehiclePlateCharacter4(char vehiclePlateCharacter4) {
        this.vehiclePlateCharacter4 = vehiclePlateCharacter4;
    }

    public void setVehiclePlateCharacter5(char vehiclePlateCharacter5) {
        this.vehiclePlateCharacter5 = vehiclePlateCharacter5;
    }

    public void setVehiclePlateCharacter6(char vehiclePlateCharacter6) {
        this.vehiclePlateCharacter6 = vehiclePlateCharacter6;
    }

    public void setVehiclePlateCharacter7(char vehiclePlateCharacter7) {
        this.vehiclePlateCharacter7 = vehiclePlateCharacter7;
    }

    public void setVehiclePlateCharacter8(char vehiclePlateCharacter8) {
        this.vehiclePlateCharacter8 = vehiclePlateCharacter8;
    }

    public void setVehiclePlateCharacter9(char vehiclePlateCharacter9) {
        this.vehiclePlateCharacter9 = vehiclePlateCharacter9;
    }

    public void setVehiclePlateCharacter10(char vehiclePlateCharacter10) {
        this.vehiclePlateCharacter10 = vehiclePlateCharacter10;
    }

    public void setVehiclePlateCharacter11(char vehiclePlateCharacter11) {
        this.vehiclePlateCharacter11 = vehiclePlateCharacter11;
    }

    public void setVehiclePlateCharacter12(char vehiclePlateCharacter12) {
        this.vehiclePlateCharacter12 = vehiclePlateCharacter12;
    }

    public String getVehiclePlate() {
        if (accessIndex == 1) {
            StringBuilder sb= new StringBuilder();
            sb.append(vehiclePlateCharacter1);
            sb.append(vehiclePlateCharacter2);
            sb.append(vehiclePlateCharacter3);
            sb.append(vehiclePlateCharacter4);
            sb.append(vehiclePlateCharacter5);
            sb.append(vehiclePlateCharacter6);
            sb.append(vehiclePlateCharacter7);
            sb.append(vehiclePlateCharacter8);
            sb.append(vehiclePlateCharacter9);
            sb.append(vehiclePlateCharacter10);
            sb.append(vehiclePlateCharacter11);
            sb.append(vehiclePlateCharacter12);
            return sb.toString();
        } 
        return null;
    }

    //    public void setVehiclePlate(String vehiclePlate) {
    //        this.vehiclePlate = vehiclePlate;
    //    }


    public MotoTronDataSnapshot() {
        localBookingId = 0;
        accessIndex = 0;
    }

    public void resetData() {
        localBookingId = 0;
        accessIndex = 0;
    }
}
