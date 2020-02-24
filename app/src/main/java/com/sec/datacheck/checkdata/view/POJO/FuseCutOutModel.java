package com.sec.datacheck.checkdata.view.POJO;

public class FuseCutOutModel {


    private int X_Y_Coordinates_1_points;
    private int Fuse_Cut_Out_No;
    private int Ratio_Amp;
    private int Type;
    private int Electricity_Status__Open_Close ;
    private int Voltage ;
    private String Notes;

    public FuseCutOutModel() {
    }

    public int getX_Y_Coordinates_1_points() {
        return X_Y_Coordinates_1_points;
    }

    public void setX_Y_Coordinates_1_points(int x_Y_Coordinates_1_points) {
        X_Y_Coordinates_1_points = x_Y_Coordinates_1_points;
    }

    public int getFuse_Cut_Out_No() {
        return Fuse_Cut_Out_No;
    }

    public void setFuse_Cut_Out_No(int fuse_Cut_Out_No) {
        Fuse_Cut_Out_No = fuse_Cut_Out_No;
    }

    public int getRatio_Amp() {
        return Ratio_Amp;
    }

    public void setRatio_Amp(int ratio_Amp) {
        Ratio_Amp = ratio_Amp;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getElectricity_Status__Open_Close() {
        return Electricity_Status__Open_Close;
    }

    public void setElectricity_Status__Open_Close(int electricity_Status__Open_Close) {
        Electricity_Status__Open_Close = electricity_Status__Open_Close;
    }

    public int getVoltage() {
        return Voltage;
    }

    public void setVoltage(int voltage) {
        Voltage = voltage;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }
}
