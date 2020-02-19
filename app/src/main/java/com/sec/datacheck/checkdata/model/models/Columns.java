package com.sec.datacheck.checkdata.model.models;

public class Columns {


    public final static String Type = "Type";

    public final static String Device_No = "Device_No";

    public final static String Code = "Code";

    public static String GlobalID = "GlobalID";

    public static String ObjectID = "OBJECTID";


    public static class SUBSTATION {
        public static String X_Y_Coordinates_2_points = "X_Y_Coordinates_2_points";
        public static String Substation = "Substation";
        public static String Substation_type = "Substation_type";
        public static String Unit_Substation_serial = "Unit_Substation_serial";
        public static String No_of_transformers = "No_of_transformers";
        public static String No_of_switchgears = "No_of_switchgears";
        public static String No_of_LVDB = "No_of_LVDB";
        public static String Substation_room_type = "Substation_room_type";
        public static String Left_S_S = "Left_S_S";
        public static String Right_S_S = "Right_S_S";
        public static String Voltage_of_equipment__primary_s = "Voltage_of_equipment__primary_s";
        public static String Total_KVA = "Total_KVA";
        public static String Manufacture_of_equipment = "Manufacture_of_equipment";
        public static String Notes = "Notes";
    }

    public static class STATION {
        public static String X_Y_Coordinates_4_points = "X_Y_Coordinates_4_points";
        public static String Grid_Station = "Grid_Station";
        public static String Grid_Station_Name = "Grid_Station_Name";
        public static String Voltage_Level__132_33__132_13_8 = "Voltage_Level__132_33__132_13_8";
        public static String Notes = "Notes";
    }

    public static class AutoReCloser{
        public static String X_Y_Coordinates_1_points = "X_Y_Coordinates_1_points";
        public static String Auto_Recloser_No = "Auto_Recloser_No";
        public static String Ratio_Amp = "Ratio_Amp";
        public static String Type_Inside__S_S__Feeder = "Type_Inside__S_S__Feeder";
        public static String Electricity_Status = "Electricity_Status";
        public static String Voltage = "Voltage";
        public static String Notes = "Notes";

    }
}
