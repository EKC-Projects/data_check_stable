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

    public static class AutoReCloser {
        public static String X_Y_Coordinates_1_points = "X_Y_Coordinates_1_points";
        public static String Auto_Recloser_No = "Auto_Recloser_No";
        public static String Ratio_Amp = "Ratio_Amp";
        public static String Type_Inside__S_S__Feeder = "Type_Inside__S_S__Feeder";
        public static String Electricity_Status = "Electricity_Status";
        public static String Voltage = "Voltage";
        public static String Notes = "Notes";

    }

    public static class FuseCutOut {
        public static String X_Y_Coordinates_1_points = "X_Y_Coordinates_1_points";
        public static String Fuse_Cut_Out_No = "Fuse_Cut_Out_No";
        public static String Ratio_Amp = "Ratio_Amp";
        public static String Type = "Type";
        public static String Electricity_Status__Open_Close = "Electricity_Status__Open_Close";
        public static String Voltage = "Voltage";
        public static String Notes = "Notes";
    }

    public static class LinkBox {
        public static String X_Y_Coordinates_1_points = "X_Y_Coordinates_1_points";
        public static String Type = "Type";
        public static String Link_Box = "Link_Box";
        public static String Total_no__of_Link_Box_in_the_CK = "Total_no__of_Link_Box_in_the_CK";
        public static String link_box_distribution_Panel = "link_box_distribution_Panel";
        public static String Notes = "Notes";
    }

    public static class LoadBreakerSwitchLBS {
        public static String X_Y_Coordinates_1_points = "X_Y_Coordinates_1_points";
        public static String LBS_No = "LBS_No";
        public static String Ratio_Amp = "Ratio_Amp";
        public static String Type = "Type";
        public static String Electricity_Status__Open_Close = "Electricity_Status__Open_Close";
        public static String Voltage = "Voltage";
        public static String Notes = "Notes";
    }

    public static class LVDistributionBox {
        public static String LV_Distribution_Box = "LV_Distribution_Box";
        public static String LV_Distribution_Panel = "LV_Distribution_Panel";
        public static String Box_Type = "Box_Type";
        public static String Notes = "Notes";
    }

    public static class MCCB_LVP_Circuits_Record {
        public static String Type_of_the_equipment = "Type_of_the_equipment";
        public static String Current_rating_of_each_MCCB = "Current_rating_of_each_MCCB";
        public static String Service_boxes = "Service_boxes";
        public static String Main_cables_type = "Main_cables_type";
        public static String Number_of_outgoing_cables = "Number_of_outgoing_cables";
        public static String Voltage_of_equipment = "Voltage_of_equipment";
        public static String Manufacture_of_equipment = "Electricity_Status__Open_Close";
        public static String Notes = "Notes";
    }

    public static class Meter {
        public static String X_Y_Coordinates_1_points = "X_Y_Coordinates_1_points";
        public static String Serial_No = "Serial_No";
        public static String Subscription_No = "Subscription_No";
        public static String Voltage_Type__MV_LV = "Voltage_Type__MV_LV";
        public static String Meter_Type__Sort_Digital_Mechan = "Meter_Type__Sort_Digital_Mechan";
        public static String Meter_Work_Type__Normal___C_T__ = "Meter_Work_Type__Normal___C_T__";
        public static String Meter_Box_Type__single_double_q = "Meter_Box_Type__single_double_q";
        public static String Substation_No = "Substation_No";
        public static String Substation_Feeder_No = "Substation_Feeder_No";
        public static String C_T_Ratio = "C_T_Ratio";
        public static String Manufacture = "Manufacture";
        public static String Backer__Size = "Backer__Size";
        public static String Smart = "Smart";
        public static String Customer_Type = "Customer_Type";
        public static String Notes = "Notes";
    }

    public static class MiniPillar {
        public static String X_Y_Coordinates_1_points = "X_Y_Coordinates_1_points";
        public static String Type_of_the_MP = "Type_of_the_MP";
        public static String Mini_pillar = "Mini_pillar";
        public static String Substation_Number = "Substation_Number";
        public static String source_of_Supply_feeder = "source_of_Supply_feeder";
        public static String Total_no_of_CKTs = "Total_no_of_CKTs";
        public static String Total_no_of_spare_CKTs = "Total_no_of_spare_CKTs";
        public static String Total_no_of_used_CKTs = "Total_no_of_used_CKTs";
        public static String Connected_to_sub_mini_pillar = "Connected_to_sub_mini_pillar";
        public static String Manufacture_of_equipment = "Manufacture_of_equipment";
        public static String CKTs_distribution = "CKTs_distribution";
        public static String Notes = "Notes";
    }

    public static class MV_Metering {
        public static String Type_of_the_equipment = "Type_of_the_equipment";
        public static String Equipment = "Equipment";
        public static String Manufacture_of_equipment = "Manufacture_of_equipment";
        public static String Notes = "Notes";
    }

    public static class OH_Lines {
        public static String Size = "Size";
        public static String Martial_Type="Martial_Type";
        public static String Voltage = "Voltage";
        public static String Electricity_Status = "Electricity_Status";
        public static String No_of_lines = "No_of_lines";
        public static String Notes = "Notes";
    }

    public static class Pole {
        public static String X_Y_Coordinates_1_points = "X_Y_Coordinates_1_points";
        public static String Pole_No = "Pole_No";
        public static String Location_Type__Section_Middle_E = "Location_Type__Section_Middle_E";
        public static String Martial_Type__Wooden_Steel = "Martial_Type__Wooden_Steel";
        public static String Soil_Type__Rock_Normal_City = "Soil_Type__Rock_Normal_City";
        public static String Pole_height = "Pole_height";
        public static String Notes = "Notes";
    }

    public static class Sectionlizer {
        public static String X_Y_Coordinates_1_points = "X_Y_Coordinates_1_points";
        public static String Sectionlizer_No = "Sectionlizer_No";
        public static String Ratio_Amp = "Ratio_Amp";
        public static String Direction_to_pole_No = "Direction_to_pole_No";
        public static String Electricity_Status = "Electricity_Status";
        public static String Voltage = "Voltage";
        public static String Notes = "Notes";
    }

    public static class SVC_Static_VAR_Compensator {
        public static String X_Y_Coordinates_1_points = "X_Y_Coordinates_1_points";
        public static String No = "No";
        public static String No_of_compensators = "No_of_compensators";
        public static String Type = "Type";
        public static String Electricity_Status__Open_Close = "Electricity_Status__Open_Close";
        public static String Voltage = "Voltage";
        public static String Capacity = "Capacity";
        public static String Manufacture = "Manufacture";
        public static String Notes = "Notes";
    }

    public static class Switchgear {
        public static String Switchgear_No = "Switchgear_No";
        public static String SEC_No = "SEC_No";
        public static String Switchgear_type = "Switchgear_type";
        public static String Left_S_S = "Left_S_S";
        public static String Destination__1 = "Destination__1";
        public static String Destination__2 = "Destination__2";
        public static String Destination__3 = "Destination__3";
        public static String Destination__4 = "Destination__4";
        public static String Destination__5 = "Destination__5";
        public static String of_positions = "of_positions";
        public static String MV_Feeder_Size = "MV_Feeder_Size";
        public static String Manufacture_of_equipment = "Manufacture_of_equipment";
        public static String Notes = "Notes";
    }

    public static class Transformer {
        public static String Type_of_the_Transformer = "Type_of_the_Transformer";
        public static String Transformer_No = "Transformer_No";
        public static String Transformer_serial = "Transformer_serial";
        public static String Transformer_Capacity_KVA = "Transformer_Capacity_KVA";
        public static String Main_cables_type = "Main_cables_type";
        public static String Number_of_outgoing_cables = "Number_of_outgoing_cables";
        public static String Plant_TAG = "Plant_TAG";
        public static String Phases = "Phases";
        public static String Voltage_of_equipment = "Voltage_of_equipment";
        public static String Manufacture_of_equipment = "Manufacture_of_equipment";
        public static String Notes = "Notes";
    }

    public static class VoltageRegulator {
        public static String X_Y_Coordinates_1_points = "X_Y_Coordinates_1_points";
        public static String No = "No";
        public static String Rated_current__for_bypass = "Rated_current__for_bypass";
        public static String Rated_current__for_Votage_regul = "Rated_current__for_Votage_regul";
        public static String Type = "Type";
        public static String Electricity_Status__Open_Close = "Electricity_Status__Open_Close";
        public static String Voltage = "Voltage";
        public static String Manufacture = "Manufacture";
        public static String Notes = "Notes";
    }
}
