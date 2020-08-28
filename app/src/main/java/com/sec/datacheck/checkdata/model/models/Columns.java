package com.sec.datacheck.checkdata.model.models;

import org.jetbrains.annotations.NotNull;

public class Columns {


    public final static String Type = "Type";

    public final static String Device_No = "Device_No";

    public final static String Code = "Code";

    public static String GlobalID = "GlobalID";

    public static String ObjectID = "OBJECTID";

    public static String Notes = "Note";

    public static final String SiteVisit = "Site_Visit";

    public static class SERVICE_POINT {
        public static final String SERVICE_POINT_NO = "SERVICEPOINTNO";
    }

    public static class OCL_METER{
        public static final String OCL_METER_FOREIGN_KEY = "REL_EDSP";
    }
}
