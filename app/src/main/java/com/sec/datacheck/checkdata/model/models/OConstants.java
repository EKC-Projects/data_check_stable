package com.sec.datacheck.checkdata.model.models;

import java.util.HashMap;

public class OConstants {

    public static final String LAYER_DISTRIBUTION_BOX = "EKC_Collector.DBO.FCL_DISTRIBUTIONBOX";

    public static final String LAYER_RMU = "EKC_Collector.DBO.FCL_RMU";

    public static final String LAYER_POLES = "EKC_Collector.DBO.FCL_POLES";

    public static final String LAYER_SUB_STATION = "EKC_Collector.DBO.FCL_Substation";

    public static final String LAYER_OCL_METER = "EKC_Collector.DBO.OCL_METER";

    public static final String LAYER_SERVICE_POINT = "EKC_Collector.DBO.Service_Point";

    public static final String LAYER_AutoReCloser = "Auto_Re_closer";
    public static final String LAYER_FuseCutOut = "Fuse_Cut_Out";
    public static final String LAYER_LinkBox = "Link_Box";
    public static final String LAYER_Load_Breaker_Switch_LBS__33__13_8__KV = "Load_Breaker_Switch_LBS__33__13_8__KV";
    public static final String LAYER_LV_Distribution_Box = "LV_Distribution_Box";
    public static final String LAYER_LV_Distribution_Panel = "LV_Distribution_Panel";
    public static final String LAYER_MCCB_LVP_Circuits_Record = "MCCB_LVP_Circuits_Record";
    public static final String LAYER_Meter = "Meter";
    public static final String LAYER_Mini_pillar = "Mini_pillar";
    public static final String LAYER_MV_Metering = "MV_Metering";
    public static final String LAYER_OH_LINES = "OH_Lines";
    public static final String LAYER_POLE = "Pole";
    public static final String LAYER_Sectionlizer = "Sectionlizer";
    public static final String LAYER_Station = "Station";
    public static final String LAYER_Substation = "Substation";
    public static final String LAYER_SVC__Static_VAR_Compensator = "SVC__Static_VAR_Compensator";
    public static final String LAYER_Switchgear = "Switchgear";
    public static final String LAYER_Transformer = "Transformer";
    public static final String LAYER_Voltage_regulator = "Voltage_regulator";

    public static HashMap<String, String> dist_box_domain, fcl_poles_domain, fcl_rmu_domain, fcl_substation_domain, ocl_meter_domain, service_point_domain;

    public static HashMap<String, String> getDistBoxDomain() {

        dist_box_domain = new HashMap<>();
        dist_box_domain.put("0", "النوع");
        dist_box_domain.put("MB", "Mini_Pillar");
        dist_box_domain.put("EXB", "EXTENSIBLE");
        dist_box_domain.put("MC", "MCCB");

        return dist_box_domain;
    }

    public static HashMap<String, String> getFCLPOLESDomain() {

        fcl_poles_domain = new HashMap<>();
        fcl_poles_domain.put("0", "النوع");
        fcl_poles_domain.put("LV", "عمود ضغط منخفض");
        fcl_poles_domain.put("MV", "عمود ضغط متوسط");
        fcl_poles_domain.put("ST", "انارة شوارع");

        return fcl_poles_domain;
    }

    public static HashMap<String, String> getFCLRMUDomain() {

        fcl_rmu_domain = new HashMap<>();
        fcl_rmu_domain.put("0", "النوع");
        fcl_rmu_domain.put("RMU", "RMU");

        return fcl_rmu_domain;
    }

    public static HashMap<String, String> getFCLSubStationDomain() {

        fcl_substation_domain = new HashMap<>();
        fcl_substation_domain.put("0", "النوع");
        fcl_substation_domain.put("PL", "محول هوائي");
        fcl_substation_domain.put("PKG", "محطة مغلقة تحتوى على معدات (LVDB-SG-TS)");
        fcl_substation_domain.put("UNT", "محطة خارجية تحتوى على (LVDB-SG-TS)");
        fcl_substation_domain.put("SW", "Switch");

        return fcl_substation_domain;
    }

    public static HashMap<String, String> getOCLMeterDomain() {

        ocl_meter_domain = new HashMap<>();
        ocl_meter_domain.put("0", "النوع");
        ocl_meter_domain.put("HC", "Meter");

        return ocl_meter_domain;
    }

    public static HashMap<String, String> getServicePointDomain() {

        service_point_domain = new HashMap<>();
        service_point_domain.put("0", "النوع");
        service_point_domain.put("SB", "SERVICE BOX");
        return service_point_domain;
    }

    public static HashMap<String, String> getDomain(String layerName) {
        switch (layerName) {
            case LAYER_DISTRIBUTION_BOX:
                return getDistBoxDomain();
            case LAYER_POLES:
                return getFCLPOLESDomain();
            case LAYER_RMU:
                return getFCLRMUDomain();
            case LAYER_SUB_STATION:
                return getFCLSubStationDomain();
            case LAYER_OCL_METER:
                return getOCLMeterDomain();
            default:
                return getServicePointDomain();
        }
    }
}
