package com.sec.datacheck.checkdata.model

object Enums {

    enum class MapType {
        DEFAULT_MAP, GOOGLE_MAP, OPEN_STREET_MAP
    }

    enum class SHAPE {
        POINT,
        POLYLINE,
        POLYGON
    }

    enum class LayerType {
        STATION,
        SUBSTATION,
        DistributionBox,
        DynamicProtectiveDevice,
        Fuse,
        Pole,
        LvOhCable,
        MvOhCable,
        LvdbArea,
        SwitchgearArea,
        TransFormers,
        RingMainUnit,
        VoltageRegulator,
        ServicePoint,
        Switch
    }

    enum class FieldType(val type: Int) {
        DomainWithDataField(1),
        DataField(2),
        DomainWithNoDataField(3)
    }
}