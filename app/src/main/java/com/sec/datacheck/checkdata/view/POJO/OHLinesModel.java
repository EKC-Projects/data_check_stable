package com.sec.datacheck.checkdata.view.POJO;

public class OHLinesModel {
    private int size;
    private int martialType;
    private int voltage;
    private int electricityStatus;
    private int noOfLines;
    private String notes;

    public OHLinesModel() {
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getMartialType() {
        return martialType;
    }

    public void setMartialType(int martialType) {
        this.martialType = martialType;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getElectricityStatus() {
        return electricityStatus;
    }

    public void setElectricityStatus(int electricityStatus) {
        this.electricityStatus = electricityStatus;
    }

    public int getNoOfLines() {
        return noOfLines;
    }

    public void setNoOfLines(int noOfLines) {
        this.noOfLines = noOfLines;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
