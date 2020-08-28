package com.sec.datacheck.checkdata.model.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class OConstants {


//    public static final String LAYER_SERVICE_POINT = "EKC_Collector.DBO.Service_Point";

    public static final String MeterUrl = "http://5.9.13.170:6080/arcgis/rest/services/EKC/NEW_CheckData/FeatureServer/16";
    public static final int MY_LOCATION_REQUEST_CODE = 8;
    public static final int REQUEST_CODE_GALLERY = 1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 2;
    public static final int WRITE_EXTERNAL_STORAGE = 3;
    public static final int READ_EXTERNAL_STORAGE = 4;
    public static final String JPG = "jpg";
    public static final String PNG = "png";
    public static final String MP4 = "mp4";

    public static final String LAYER_Station = "station";
    public static final String LAYER_Substation = "Substation";
    public static final String LAYER_POLE = "POLES";

    public static final String LAYER_Voltage_regulator = "VOLTAGE_REGULATOR";
    public static final String LAYER_Transformer = "TRANSFORMER";
    public static final String LAYER_SWITCHGEAR_AREA = "SWITCHGEAR_AREA";

    public static final String LAYER_RING_MAIN_UNIT = "RING_MAIN_UNIT";
    public static final String LAYER_DISTRIBUTION_BOX= "DISTRIBUTION_BOX";
    public static final String LAYER_DYNAMIC_PROTECTIVE_DEVICE = "DYNAMIC_PROTECTIVE_DEVICE";
    public static final String LAYER_FUSE = "FUSE";
    public static final String LAYER_LV_OH_CABLE = "LV_OH_CABLE";

    public static final String LAYER_SERVICE_POINT = "SERVICE_POINT";
    public static final String LAYER_MV_OH_CABLE = "MV_OH_CABLE";
    public static final String LAYER_LVDB_AREA = "LVDB_AREA";
    public static final String LAYER_SWITCH = "SWITCH";

    public static final String CHECK_DOMAIN_NAME = "Check";
    @Nullable
    public static final String null_ = "Null";

    public static final String IMAGE_FOLDER_NAME = "SEC_Data_Check";
    public static final String IMAGE_FOLDER_NAME_COMPRESSED = "SEC_Data_Check_Compressed";
    @NotNull
    public static final String SITE_VISITE = "Site_Visit";
    @NotNull
    public static final String ROOT_GEO_DATABASE_PATH = "geodatabase";

}
