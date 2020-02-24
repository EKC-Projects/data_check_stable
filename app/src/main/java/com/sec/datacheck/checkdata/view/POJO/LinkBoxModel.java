package com.sec.datacheck.checkdata.view.POJO;

public class LinkBoxModel {


    private int X_Y_Coordinates_1_points;
    private int Type ;
    private int Link_Box ;
    private int Total_no__of_Link_Box_in_the_CK ;
    private int link_box_distribution_Panel ;
    private String Notes;

    public LinkBoxModel() {
    }

    public int getX_Y_Coordinates_1_points() {
        return X_Y_Coordinates_1_points;
    }

    public void setX_Y_Coordinates_1_points(int x_Y_Coordinates_1_points) {
        X_Y_Coordinates_1_points = x_Y_Coordinates_1_points;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getLink_Box() {
        return Link_Box;
    }

    public void setLink_Box(int link_Box) {
        Link_Box = link_Box;
    }

    public int getTotal_no__of_Link_Box_in_the_CK() {
        return Total_no__of_Link_Box_in_the_CK;
    }

    public void setTotal_no__of_Link_Box_in_the_CK(int total_no__of_Link_Box_in_the_CK) {
        Total_no__of_Link_Box_in_the_CK = total_no__of_Link_Box_in_the_CK;
    }

    public int getLink_box_distribution_Panel() {
        return link_box_distribution_Panel;
    }

    public void setLink_box_distribution_Panel(int link_box_distribution_Panel) {
        this.link_box_distribution_Panel = link_box_distribution_Panel;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }
}
