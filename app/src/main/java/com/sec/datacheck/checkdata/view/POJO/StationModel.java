package com.sec.datacheck.checkdata.view.POJO;

public class StationModel {

    private int X_Y_Coordinates_4_points;
    private int Grid_Station;
    private int Grid_Station_Name;
    private int Voltage_Level__132_33__132_13_8;
    private String Notes;

    public StationModel() {
    }

    public int getX_Y_Coordinates_4_points() {
        return X_Y_Coordinates_4_points;
    }

    public void setX_Y_Coordinates_4_points(int x_Y_Coordinates_4_points) {
        X_Y_Coordinates_4_points = x_Y_Coordinates_4_points;
    }

    public int getGrid_Station() {
        return Grid_Station;
    }

    public void setGrid_Station(int grid_Station) {
        Grid_Station = grid_Station;
    }

    public int getGrid_Station_Name() {
        return Grid_Station_Name;
    }

    public void setGrid_Station_Name(int grid_Station_Name) {
        Grid_Station_Name = grid_Station_Name;
    }

    public int getVoltage_Level__132_33__132_13_8() {
        return Voltage_Level__132_33__132_13_8;
    }

    public void setVoltage_Level__132_33__132_13_8(int voltage_Level__132_33__132_13_8) {
        Voltage_Level__132_33__132_13_8 = voltage_Level__132_33__132_13_8;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }
}
