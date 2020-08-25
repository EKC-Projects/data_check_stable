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

    enum class TableId(val id: Long) {

        Station(1),
        SubStation(2),
        DistributionBox(6),
        DynamicProtectiveDevice(11),
        Fuse(10),
        Pole(5),
        LvOhCable(13),
        MvOhCable(12),
        LvdbArea(14),
        SwitchgearArea(15),
        TransFormers(3),
        RingMainUnit(4),
        VoltageRegulator(7),
        ServicePoint(8),
        Switch(9),
        OCLMeter(16)
    }
}