package com.sec.datacheck.checkdata.view.POJO;

public class MvMeteringModel {

    private int Type_of_the_equipment;
    private  int Equipment;
    private  int Manufacture_of_equipment;
    private  String Notes;

    public MvMeteringModel() {
    }

    public int getType_of_the_equipment() {
        return Type_of_the_equipment;
    }

    public void setType_of_the_equipment(int type_of_the_equipment) {
        Type_of_the_equipment = type_of_the_equipment;
    }

    public int getEquipment() {
        return Equipment;
    }

    public void setEquipment(int equipment) {
        Equipment = equipment;
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
