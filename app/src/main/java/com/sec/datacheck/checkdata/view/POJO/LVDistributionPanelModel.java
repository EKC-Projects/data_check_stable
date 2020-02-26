package com.sec.datacheck.checkdata.view.POJO;

public class LVDistributionPanelModel {
    private int type_of_the_LV_panel;
    private int total_no_of_feeders;
    private int total_no_of_used_feeders;
    private int total_no__of_Spare_feeders;
    private int main_cables_type;
    private int number_of_outgoing_cables;
    private int current_Rating;
    private int voltage_of_equipment;
    private int manufacture_of_equipment;
    private int feeders_Panel_distribution;
    private String notes;


    public LVDistributionPanelModel() {
    }


    public int getType_of_the_LV_panel() {
        return type_of_the_LV_panel;
    }

    public void setType_of_the_LV_panel(int type_of_the_LV_panel) {
        this.type_of_the_LV_panel = type_of_the_LV_panel;
    }

    public int getTotal_no_of_feeders() {
        return total_no_of_feeders;
    }

    public void setTotal_no_of_feeders(int total_no_of_feeders) {
        this.total_no_of_feeders = total_no_of_feeders;
    }

    public int getTotal_no_of_used_feeders() {
        return total_no_of_used_feeders;
    }

    public void setTotal_no_of_used_feeders(int total_no_of_used_feeders) {
        this.total_no_of_used_feeders = total_no_of_used_feeders;
    }

    public int getTotal_no__of_Spare_feeders() {
        return total_no__of_Spare_feeders;
    }

    public void setTotal_no__of_Spare_feeders(int total_no__of_Spare_feeders) {
        this.total_no__of_Spare_feeders = total_no__of_Spare_feeders;
    }

    public int getMain_cables_type() {
        return main_cables_type;
    }

    public void setMain_cables_type(int main_cables_type) {
        this.main_cables_type = main_cables_type;
    }

    public int getNumber_of_outgoing_cables() {
        return number_of_outgoing_cables;
    }

    public void setNumber_of_outgoing_cables(int number_of_outgoing_cables) {
        this.number_of_outgoing_cables = number_of_outgoing_cables;
    }

    public int getCurrent_Rating() {
        return current_Rating;
    }

    public void setCurrent_Rating(int current_Rating) {
        this.current_Rating = current_Rating;
    }

    public int getVoltage_of_equipment() {
        return voltage_of_equipment;
    }

    public void setVoltage_of_equipment(int voltage_of_equipment) {
        this.voltage_of_equipment = voltage_of_equipment;
    }

    public int getManufacture_of_equipment() {
        return manufacture_of_equipment;
    }

    public void setManufacture_of_equipment(int manufacture_of_equipment) {
        this.manufacture_of_equipment = manufacture_of_equipment;
    }

    public int getFeeders_Panel_distribution() {
        return feeders_Panel_distribution;
    }

    public void setFeeders_Panel_distribution(int feeders_Panel_distribution) {
        this.feeders_Panel_distribution = feeders_Panel_distribution;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
