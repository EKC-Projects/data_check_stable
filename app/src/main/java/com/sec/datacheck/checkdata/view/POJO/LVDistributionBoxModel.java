package com.sec.datacheck.checkdata.view.POJO;

public class LVDistributionBoxModel {

    private int LV_Distribution_Box;
    private int LV_Distribution_Panel;
    private int Box_Type;
    private String Notes;

    public LVDistributionBoxModel() {
    }

    public int getLV_Distribution_Box() {
        return LV_Distribution_Box;
    }

    public void setLV_Distribution_Box(int LV_Distribution_Box) {
        this.LV_Distribution_Box = LV_Distribution_Box;
    }

    public int getLV_Distribution_Panel() {
        return LV_Distribution_Panel;
    }

    public void setLV_Distribution_Panel(int LV_Distribution_Panel) {
        this.LV_Distribution_Panel = LV_Distribution_Panel;
    }

    public int getBox_Type() {
        return Box_Type;
    }

    public void setBox_Type(int box_Type) {
        Box_Type = box_Type;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }
}
