package com.sec.datacheck.checkdata.view.POJO;

public class MCCB_LVP_Circuits_RecordModel {

    private int Type_of_the_equipment;
    private int Current_rating_of_each_MCCB;
    private int Service_boxes;
    private int Main_cables_type;
    private int Number_of_outgoing_cables;
    private int Voltage_of_equipment;
    private int Manufacture_of_equipment;
    private String Notes;

    public MCCB_LVP_Circuits_RecordModel() {
    }

    public int getType_of_the_equipment() {
        return Type_of_the_equipment;
    }

    public void setType_of_the_equipment(int type_of_the_equipment) {
        Type_of_the_equipment = type_of_the_equipment;
    }

    public int getCurrent_rating_of_each_MCCB() {
        return Current_rating_of_each_MCCB;
    }

    public void setCurrent_rating_of_each_MCCB(int current_rating_of_each_MCCB) {
        Current_rating_of_each_MCCB = current_rating_of_each_MCCB;
    }

    public int getService_boxes() {
        return Service_boxes;
    }

    public void setService_boxes(int service_boxes) {
        Service_boxes = service_boxes;
    }

    public int getMain_cables_type() {
        return Main_cables_type;
    }

    public void setMain_cables_type(int main_cables_type) {
        Main_cables_type = main_cables_type;
    }

    public int getNumber_of_outgoing_cables() {
        return Number_of_outgoing_cables;
    }

    public void setNumber_of_outgoing_cables(int number_of_outgoing_cables) {
        Number_of_outgoing_cables = number_of_outgoing_cables;
    }

    public int getVoltage_of_equipment() {
        return Voltage_of_equipment;
    }

    public void setVoltage_of_equipment(int voltage_of_equipment) {
        Voltage_of_equipment = voltage_of_equipment;
    }

    public int getManufacture_of_equipment() {
        return Manufacture_of_equipment;
    }

    public void setManufacture_of_equipment(int manufacture_of_equipment) {
        Manufacture_of_equipment = manufacture_of_equipment;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }
}
