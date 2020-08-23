package com.sec.datacheck.checkdata.view.activities.map

import android.app.Application
import android.content.Context
import android.graphics.*
import android.os.Environment
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.*
import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.view.MapView
import com.sec.datacheck.R
import com.sec.datacheck.checkdata.model.Enums
import com.sec.datacheck.checkdata.model.QueryConfig
import com.sec.datacheck.checkdata.model.models.Columns
import com.sec.datacheck.checkdata.model.models.OConstants
import com.sec.datacheck.checkdata.model.models.OnlineQueryResult
import com.sec.datacheck.checkdata.view.POJO.FieldModel
import com.sec.datacheck.checkdata.view.fragments.updateFragment.UpdateFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MapViewModel"
    lateinit var currentOfflineVersionTitle: String
    var currentOfflineVersion: Int = -1
    lateinit var shapeType: Enums.SHAPE
    lateinit var mOnlineQueryResults: ArrayList<OnlineQueryResult>
    var queryFinished = MutableLiveData<Boolean>()
    val layers: ArrayList<OnlineQueryResult> = ArrayList()
    var selectedPointOnMap: Point? = null
    var selectedResult: OnlineQueryResult? = null
    var onlineData = true
    var fields: ArrayList<FieldModel> = ArrayList()
    val featuresList: ArrayList<OnlineQueryResult> = ArrayList()
    val liveDataFields = MutableLiveData<ArrayList<FieldModel>>()
    val imagesList: ArrayList<File> = ArrayList()
    lateinit var selectedFeature: Feature
    lateinit var selectedLayer: FeatureLayer
    lateinit var selectedTable: ServiceFeatureTable
    lateinit var selectedOfflineFeatureTable: GeodatabaseFeatureTable
    lateinit var objectID: String
    lateinit var mFileTemp: File

    fun prepareQueryResult() {
        mOnlineQueryResults = ArrayList()
    }

    fun prepareOnlineLayers(baseMap: ArcGISMap, mapView: MapView, context: Context) {

        try {// create the feature layer using the service feature table
            stationTable = ServiceFeatureTable(context.getString(R.string.stations))
            stationLayer = FeatureLayer(stationTable)

            substationTable = ServiceFeatureTable(context.getString(R.string.substations))
            substationLayer = FeatureLayer(substationTable)

            FCL_DistributionBoxTable = ServiceFeatureTable(context.getString(R.string.FCL_DISTRIBUTIONBOX))
            FCL_DistributionBoxLayer = FeatureLayer(FCL_DistributionBoxTable)

            DynamicProtectiveDeviceTable = ServiceFeatureTable(context.getString(R.string.DYNAMIC_PROTECTIVE_DEVICE))
            DynamicProtectiveDeviceLayer = FeatureLayer(DynamicProtectiveDeviceTable)

            FuseTable = ServiceFeatureTable(context.getString(R.string.FUSE))
            FuseLayer = FeatureLayer(FuseTable)

            FCL_POLESTable = ServiceFeatureTable(context.getString(R.string.FCL_POLES))
            FCL_POLES_Layer = FeatureLayer(FCL_POLESTable)

            LvOhCableTable = ServiceFeatureTable(context.getString(R.string.lv_oh_cable))
            LvOhCableLayer = FeatureLayer(LvOhCableTable)

            MvOhCableTable = ServiceFeatureTable(context.getString(R.string.mv_oh_cable))
            MvOhCableLayer = FeatureLayer(MvOhCableTable)

            LvdbAreaTable = ServiceFeatureTable(context.getString(R.string.lvdb_area))
            LvdbAreaLayer = FeatureLayer(LvdbAreaTable)

            SwitchgearAreaTable = ServiceFeatureTable(context.getString(R.string.switch_gear_area))
            SwitchgearAreaLayer = FeatureLayer(SwitchgearAreaTable)

            TransFormersTable = ServiceFeatureTable(context.getString(R.string.TRANSFORMER))
            TransFormersLayer = FeatureLayer(TransFormersTable)

            RingMainUnitTable = ServiceFeatureTable(context.getString(R.string.RING_MAIN_UNIT))
            RingMainUnitLayer = FeatureLayer(RingMainUnitTable)

            VoltageRegulatorTable = ServiceFeatureTable(context.getString(R.string.VOLTAGE_REGULATOR))
            VoltageRegulatorLayer = FeatureLayer(VoltageRegulatorTable)

            ServicePointTable = ServiceFeatureTable(context.getString(R.string.SERVICE_POINT))
            ServicePointLayer = FeatureLayer(ServicePointTable)

            SwitchTable = ServiceFeatureTable(context.getString(R.string.SWITCH))
            SwitchLayer = FeatureLayer(SwitchTable)

            // add the layer to the map
            mapView.map.operationalLayers.add(LvdbAreaLayer)
            baseMap.operationalLayers.add(SwitchgearAreaLayer)

            baseMap.operationalLayers.add(LvOhCableLayer)
            baseMap.operationalLayers.add(MvOhCableLayer)

            baseMap.operationalLayers.add(stationLayer)
            baseMap.operationalLayers.add(substationLayer)
            baseMap.operationalLayers.add(FCL_DistributionBoxLayer)
            baseMap.operationalLayers.add(DynamicProtectiveDeviceLayer)

            baseMap.operationalLayers.add(FuseLayer)
            baseMap.operationalLayers.add(FCL_POLES_Layer)
            baseMap.operationalLayers.add(TransFormersLayer)
            baseMap.operationalLayers.add(RingMainUnitLayer)

            baseMap.operationalLayers.add(VoltageRegulatorLayer)
            baseMap.operationalLayers.add(ServicePointLayer)
            baseMap.operationalLayers.add(SwitchLayer)

            val station = OnlineQueryResult()
            station.featureLayer = stationLayer
            station.serviceFeatureTable = stationTable
            station.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_station, context.resources.newTheme())
            station.layerType = Enums.LayerType.STATION
            val subStation = OnlineQueryResult()
            subStation.featureLayer = substationLayer
            subStation.serviceFeatureTable = substationTable
            subStation.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_substation, context.resources.newTheme())
            subStation.layerType = Enums.LayerType.SUBSTATION

            val distributionBox = OnlineQueryResult()
            distributionBox.featureLayer = FCL_DistributionBoxLayer
            distributionBox.serviceFeatureTable = FCL_DistributionBoxTable
            distributionBox.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_dist_box, context.resources.newTheme())
            distributionBox.layerType = Enums.LayerType.DistributionBox

            val dynamicProtectiveDevice = OnlineQueryResult()
            dynamicProtectiveDevice.featureLayer = DynamicProtectiveDeviceLayer
            dynamicProtectiveDevice.serviceFeatureTable = DynamicProtectiveDeviceTable
            dynamicProtectiveDevice.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_dynamic_protection_device, context.resources.newTheme())
            dynamicProtectiveDevice.layerType = Enums.LayerType.DynamicProtectiveDevice

            val fuse = OnlineQueryResult()
            fuse.featureLayer = FuseLayer
            fuse.serviceFeatureTable = FuseTable
            fuse.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_meter, context.resources.newTheme())
            fuse.layerType = Enums.LayerType.Fuse

            val poles = OnlineQueryResult()
            poles.featureLayer = FCL_POLES_Layer
            poles.serviceFeatureTable = FCL_POLESTable
            poles.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_rmu, context.resources.newTheme())
            poles.layerType = Enums.LayerType.Pole

            val lvOhCable = OnlineQueryResult()
            lvOhCable.featureLayer = LvOhCableLayer
            lvOhCable.serviceFeatureTable = LvOhCableTable
            lvOhCable.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_lv_oh_cable, context.resources.newTheme())
            lvOhCable.layerType = Enums.LayerType.LvOhCable

            val mvOhCable = OnlineQueryResult()
            mvOhCable.featureLayer = MvOhCableLayer
            mvOhCable.serviceFeatureTable = MvOhCableTable
            mvOhCable.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_mv_oh_cable, context.resources.newTheme())
            mvOhCable.layerType = Enums.LayerType.MvOhCable

            val lvDbArea = OnlineQueryResult()
            lvDbArea.featureLayer = LvdbAreaLayer
            lvDbArea.serviceFeatureTable = LvdbAreaTable
            lvDbArea.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_lvdb_area, context.resources.newTheme())
            lvDbArea.layerType = Enums.LayerType.LvdbArea

            val switchGearArea = OnlineQueryResult()
            switchGearArea.featureLayer = SwitchgearAreaLayer
            switchGearArea.serviceFeatureTable = SwitchgearAreaTable
            switchGearArea.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_swtich_gear_area, context.resources.newTheme())
            switchGearArea.layerType = Enums.LayerType.SwitchgearArea

            val transformers = OnlineQueryResult()
            transformers.featureLayer = TransFormersLayer
            transformers.serviceFeatureTable = TransFormersTable
            transformers.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_transformer, context.resources.newTheme())
            transformers.layerType = Enums.LayerType.TransFormers

            val ringMainUnit = OnlineQueryResult()
            ringMainUnit.featureLayer = RingMainUnitLayer
            ringMainUnit.serviceFeatureTable = RingMainUnitTable
            ringMainUnit.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_ring_main_unit, context.resources.newTheme())
            ringMainUnit.layerType = Enums.LayerType.RingMainUnit

            val voltageRegulator = OnlineQueryResult()
            voltageRegulator.featureLayer = VoltageRegulatorLayer
            voltageRegulator.serviceFeatureTable = VoltageRegulatorTable
            voltageRegulator.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_voltage_regulator, context.resources.newTheme())
            voltageRegulator.layerType = Enums.LayerType.VoltageRegulator

            val servicePoint = OnlineQueryResult()
            servicePoint.featureLayer = ServicePointLayer
            servicePoint.serviceFeatureTable = ServicePointTable
            servicePoint.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_service_point, context.resources.newTheme())
            servicePoint.layerType = Enums.LayerType.ServicePoint

            val switch = OnlineQueryResult()
            switch.featureLayer = SwitchLayer
            switch.serviceFeatureTable = SwitchTable
            switch.drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_ic_swtich, context.resources.newTheme())
            switch.layerType = Enums.LayerType.Switch

            layers.add(station)
            layers.add(subStation)
            layers.add(distributionBox)
            layers.add(dynamicProtectiveDevice)
            layers.add(fuse)
            layers.add(poles)
            layers.add(lvOhCable)
            layers.add(mvOhCable)
            layers.add(lvDbArea)
            layers.add(switchGearArea)
            layers.add(transformers)
            layers.add(ringMainUnit)
            layers.add(voltageRegulator)
            layers.add(servicePoint)
            layers.add(switch)

            // set the map to be displayed in the mapView

            // set the map to be displayed in the mapView
            mapView.map = baseMap
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun calculateDistanceBetweenTwoPoints(edinburghGeographic: Point, darEsSalaamGeographic: Point, equidistantSpatialRef: SpatialReference): Double {
        var distance = 0.0
        try {
            // Project the points from geographic to the projected coordinate system.
            val edinburghProjected = GeometryEngine.project(edinburghGeographic, equidistantSpatialRef) as Point
            val darEsSalaamProjected = GeometryEngine.project(darEsSalaamGeographic, equidistantSpatialRef) as Point

            // Get the planar distance between the points in the spatial reference unit (meters).
            // Result = 7,372,671.29511302 (around 7,372.67 kilometers)
            distance = GeometryEngine.distanceBetween(edinburghProjected, darEsSalaamProjected)


        } catch (e: Exception) {
            e.printStackTrace()
        }
        return distance
    }

    fun queryOnline(point: Point?, spatialReference: SpatialReference?) {
        point?.let { mPoint ->
            spatialReference?.let { sp ->
                for (i in layers.indices) {
                    try {
                        val layer = layers[i]
                        val query = QueryConfig.getQuery(mPoint, sp, true)
                        val future: ListenableFuture<FeatureQueryResult> = layer.serviceFeatureTable.queryFeaturesAsync(query)
                        // add done loading listener to fire when the selection returns
                        future.addDoneListener {
                            try {
                                // call get on the future to get the result
                                val result = future.get()
                                // check there are some results
                                val resultIterator: Iterator<Feature> = result.iterator()
                                if (resultIterator.hasNext()) {
                                    while (resultIterator.hasNext()) {
                                        // get the extent of the first feature in the result to zoom to
                                        val feature = resultIterator.next() as ArcGISFeature
                                        feature.loadAsync()
                                        val mOnlineQueryResult = OnlineQueryResult()
                                        mOnlineQueryResult.feature = feature
                                        mOnlineQueryResult.serviceFeatureTable = layer.serviceFeatureTable
                                        mOnlineQueryResult.featureLayer = layer.featureLayer
                                        mOnlineQueryResult.objectID = feature.attributes[Columns.ObjectID].toString()
                                        mOnlineQueryResult.layerType = layer.layerType
                                        mOnlineQueryResult.drawable = layer.drawable

                                        when {
                                            isPolygon(layer.featureLayer) -> {
                                                mOnlineQueryResult.featureType = Enums.SHAPE.POLYGON
                                            }
                                            isPolyline(layer.featureLayer) -> {
                                                mOnlineQueryResult.featureType = Enums.SHAPE.POLYLINE
                                            }
                                            else -> {
                                                mOnlineQueryResult.featureType = Enums.SHAPE.POINT
                                            }
                                        }

                                        if (layer.featureLayer.name.equals("SERVICE_POINT", ignoreCase = true)) {
                                            queryRelatedOCLMETER(mOnlineQueryResult, point)
                                        }
                                        mOnlineQueryResults.add(mOnlineQueryResult)
                                    }
                                }

                                if (i == layers.size - 1)
                                    queryFinished.value = true
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun queryRelatedOCLMETER(mOnlineQueryResult: OnlineQueryResult, point: Point?) {
        try {
            val servicePoint = mOnlineQueryResult.feature
            servicePoint.loadAsync()
            servicePoint.addDoneLoadingListener {
                if (servicePoint != null && servicePoint.attributes != null && servicePoint.attributes[Columns.SERVICE_POINT.SERVICE_POINT_NO] != null) {
                    val servicePointNo = servicePoint.attributes[Columns.SERVICE_POINT.SERVICE_POINT_NO].toString()
                    val oclMeterTable = ServiceFeatureTable("http://5.9.13.170:6080/arcgis/rest/services/EKC/NEW_CheckData/FeatureServer/15")
                    oclMeterTable.addDoneLoadingListener {
                        val relatedQueryParameters = QueryConfig.getRelatedQuery(servicePointNo, Columns.OCL_METER.OCL_METER_FOREIGN_KEY, point)
                        //                                    FeatureLayer layer = new FeatureLayer(oclMeterTable);
                        val queryResults = oclMeterTable.queryFeaturesAsync(relatedQueryParameters)
                        queryResults.addDoneListener {
                            try {// call get on the future to get the result

                                // call get on the future to get the result
                                val mResult = queryResults.get()
                                // check there are some results
                                // check there are some results
                                val mResultIterator: Iterator<Feature> = mResult.iterator()
                                if (mResultIterator.hasNext()) {
                                    mOnlineQueryResult.isHasRelatedFeatures = true
                                    val onlineQueryResults = java.util.ArrayList<OnlineQueryResult>()
                                    while (mResultIterator.hasNext()) {
                                        val onlineQueryResult = OnlineQueryResult()
                                        // get the extent of the first feature in the result to zoom to
                                        val mFeature = mResultIterator.next() as ArcGISFeature
                                        mFeature.loadAsync()
                                        onlineQueryResult.feature = mFeature
                                        onlineQueryResult.serviceFeatureTable = oclMeterTable
                                        onlineQueryResult.objectID = mFeature.attributes[Columns.ObjectID].toString()
                                        onlineQueryResult.featureType = Enums.SHAPE.POINT
                                        onlineQueryResults.add(onlineQueryResult)
                                        mOnlineQueryResult.relatedFeatures = onlineQueryResults

                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    oclMeterTable.loadAsync()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isPolyline(featureLayer: FeatureLayer?): Boolean {
        return featureLayer == MvOhCableLayer || featureLayer == LvOhCableLayer

    }

    private fun isPolygon(featureLayer: FeatureLayer?): Boolean {
        return featureLayer == LvdbAreaLayer || featureLayer == SwitchgearAreaLayer

    }

    fun addLocalLayers(baseMap: ArcGISMap, selectedVersion: Int, selectedTitle: String) {

    }

    fun queryOffline(point: Point?, spatialReference: SpatialReference?) {

    }

    fun prepareSelectedFeature() {
        if (onlineData) {
            selectedFeature = selectedResult?.feature!!
            selectedResult?.featureLayer?.let { layer ->
                selectedLayer = layer
            }
            selectedTable = selectedResult?.serviceFeatureTable!!
            objectID = selectedResult?.objectID!!
            selectedResult?.feature?.loadAsync()
            selectedResult?.feature?.addDoneLoadingListener {
                try {
                    extractData()
                    loadImages()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            selectedFeature = selectedResult?.featureOffline!!
            selectedResult?.featureLayer?.let { layer ->
                selectedLayer = layer
            }
            selectedOfflineFeatureTable = selectedResult?.geodatabaseFeatureTable!!
            objectID = selectedResult?.objectID!!
            extractData()
            loadImages()
        }
    }

    fun fillFeatureList() {
        featuresList.clear()
        selectedResult?.let { result ->
            if (result.isHasRelatedFeatures) {
                featuresList.add(result)
                if (!result.relatedFeatures.isNullOrEmpty()) {
                    result.relatedFeatures.forEach { relatedFeature ->
                        featuresList.add(relatedFeature)
                    }
                }
            } else if (!result.isHasRelatedFeatures) {
                featuresList.add(result)
            }
        }
    }

    private fun extractData() {
        try {
            fields.clear()
            var fieldList: List<Field?>? = null

            fieldList = if (onlineData) {
                selectedResult?.serviceFeatureTable?.fields
            } else {
                selectedResult?.geodatabaseFeatureTable?.fields
            }

            if (!fieldList.isNullOrEmpty()) {
                for (field in fieldList) {
                    if (field != null && isValidField(field.name)) {
                        val fieldModel = FieldModel()
                        fieldModel.title = field.name
                        fieldModel.alias = field.alias
                        if (field.domain != null && field.domain is CodedValueDomain) {
                            if (isCheckDomain(field.domain)) { //Yes, No, N / A Domain

                                //if domain doesn't have data field
                                if (!isDomainHasDataField(field, fieldList)) { //TODO Domain Must Have default value
                                    val codedValueDomain = field.domain as CodedValueDomain
                                    fieldModel.choiceDomain = codedValueDomain
                                    fieldModel.type = Enums.FieldType.DomainWithNoDataField.type //Domain hasn't data field to check
                                    fieldModel.selectedDomainIndex = codedValueDomain.codedValues.indexOf(selectedFeature.attributes[field.name])
                                } else {
                                    //else if domain has data field
                                    val codedValueDomain = field.domain as CodedValueDomain
                                    fieldModel.choiceDomain = codedValueDomain
                                    fieldModel.type = Enums.FieldType.DomainWithDataField.type //Domain has data field to check
                                    fieldModel.selectedDomainIndex = codedValueDomain.codedValues.indexOf(selectedFeature.attributes[field.name])
                                }
                            } else {
                                //any other Domain
                                if (hasCheckDomain(field.name, fieldList)) {
                                    val codedValueDomain = field.domain as CodedValueDomain
                                    var founded = false
                                    for (codedValue in codedValueDomain.codedValues) {
                                        if (selectedFeature.attributes[field.name] != null && codedValue.code == selectedFeature.attributes[field.name]) {
                                            fieldModel.textValue = codedValue.name
                                            founded = true
                                            break
                                        }
                                    }
                                    if (!founded) {
                                        fieldModel.textValue = OConstants.null_
                                    }
                                    fieldModel.type = Enums.FieldType.DataField.type
                                } else {
                                    val codedValueDomain = field.domain as CodedValueDomain
                                    fieldModel.choiceDomain = codedValueDomain
                                    fieldModel.type = Enums.FieldType.DomainWithNoDataField.type //Domain hasn't data field to check
                                    fieldModel.selectedDomainIndex = selectedFeature.attributes[field.name].toString()
                                    codedValueDomain.codedValues?.let {
                                        val fieldValue = selectedFeature.attributes[field.name].toString()
                                        for (i in 0 until it.size) {
                                            val name = it[i].name
                                            val code = it[i].code
                                            if (code == fieldValue) {
                                                fieldModel.textValue = name
                                                break
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            fieldModel.textValue = selectedFeature.attributes[field.name].toString()
                            fieldModel.type = Enums.FieldType.DataField.type
                        }
                        if (fieldModel.type == Enums.FieldType.DomainWithDataField.type) {
                            associateDataFieldWithDomain(fieldModel)
                        } else {
                            fieldModel.isHasCheckDomain = false
                            fields.add(fieldModel)
                        }
                    }
                }
                if (fields.isNotEmpty()) {
                    fields = sortFields(fields)
                    liveDataFields.value = fields
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun associateDataFieldWithDomain(fieldModel: FieldModel) {
        fields.forEach { field ->
            if (fieldModel.title.startsWith(field.title) && field.title != fieldModel.title && !isCheckDomain(field.domain)) {
                field.checkDomain = fieldModel
                field.isHasCheckDomain = true
            }
        }
    }

    private fun hasCheckDomain(name: String, fieldList: List<Field>): Boolean {
        val mName = name + "_Check"
        for (field in fieldList) {
            if (field.name == mName) {
                return true
            }
        }
        return false
    }

    private fun isDomainHasDataField(originalField: Field, fieldList: List<Field>): Boolean {
        val domainName = originalField.name
        for (field in fieldList) {
            if (domainName.startsWith(field.name)) {
                return if (field.domain != null && field.domain is CodedValueDomain && field.domain.name != OConstants.CHECK_DOMAIN_NAME) {
                    true
                } else field.domain == null
            }
        }
        return false
    }

    private fun isValidField(name: String): Boolean {
        return when (name) {
            "GlobalID" -> false
            "Note" -> false
            "created_user" -> false
            "created_date" -> false
            "last_edited_user" -> false
            "last_edited_date" -> false
            else -> true
        }
    }

    private fun isCheckDomain(domain: Domain?): Boolean {
        domain?.let {
            it.name?.let { name ->
                if (name == OConstants.CHECK_DOMAIN_NAME)
                    return true
            }
        }
        return false
    }

    private fun loadImages() {
        try {
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path + File.separator + OConstants.IMAGE_FOLDER_NAME_COMPRESSED + File.separator
            val folder = File(path)
            if (folder.exists()) {
                val allFiles = folder.listFiles { dir, name -> name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") }
                if (allFiles != null && allFiles.isNotEmpty()) {

                    for (file in allFiles) {
                        if (file.path.contains("_$objectID.png") || file.path.contains("_$objectID.jpg") || file.path.contains("_$objectID.jpeg")) {
                            imagesList.add(file)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getField(data: ArrayList<FieldModel>, name: String, type: Int): FieldModel? {
        for (fieldModel in data) {
            if (fieldModel.title.toLowerCase().startsWith(name.toLowerCase()) && fieldModel.type == type) {
                return fieldModel
            }
        }
        return null
    }

    private fun sortFields(fields: ArrayList<FieldModel>): ArrayList<FieldModel> {
        val result = java.util.ArrayList<FieldModel>()
        getField(fields, Columns.ObjectID, Enums.FieldType.DataField.type)?.let {
            result.add(it)
        }

        getField(fields, Columns.SiteVisit, Enums.FieldType.DomainWithNoDataField.type)?.let {
            result.add(it)
        }

        for (field in fields) {

            if (field.type == 2 &&
                    !isObjectID(field)
                    && isValidField(field.title)) {
                result.add(field)
                getField(fields, field.title.toLowerCase(), Enums.FieldType.DomainWithDataField.type)?.let {
                    result.add(it)
                }
            }
        }
        for (field in fields) {
            if (field.type == Enums.FieldType.DomainWithNoDataField.type && !isSiteVisit(field)) {
                result.add(field)
            }
        }
        return result
    }


    private fun isObjectID(field: FieldModel): Boolean {
        return field.title.toLowerCase() == Columns.ObjectID.toLowerCase()
    }

    private fun isSiteVisit(field: FieldModel): Boolean {
        return field.title.toLowerCase() == Columns.SiteVisit.toLowerCase()
    }

    fun createFile(name: String, layerFolderName: String, extension: String, type: String) {

        try {
            val d = Date()
            val rootFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), OConstants.IMAGE_FOLDER_NAME)
            if (!rootFolder.exists()) {
                rootFolder.mkdir()
            }
            val dateFolderName = File(rootFolder, SimpleDateFormat("dd_MM_yyyy", Locale.ENGLISH).format(d))
            if (!dateFolderName.exists()) {
                dateFolderName.mkdir()
            }

            val layerFolder = File(dateFolderName.path, layerFolderName)
            if (!layerFolder.exists()) {
                layerFolder.mkdir()
            }
            val pointFolder = File(layerFolder.path, name)
            if (!pointFolder.exists()) {
                pointFolder.mkdir()
            }
            mFileTemp = File(pointFolder.path + File.separator + type + "_" + SimpleDateFormat("dd_MM_yyyy_hh_mm_ss", Locale.ENGLISH).format(d) + layerFolderName + "_" + name + "." + extension.trim { it <= ' ' })

            Log.e(TAG, "file createFile " + mFileTemp.path)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun compressImage(filePath: String): File? {

//        String filePath = getRealPathFromURI(imageUri, context);
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = actualWidth / actualHeight.toFloat()
        val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = UpdateFragment.calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        try {
            val canvas = Canvas(scaledBitmap!!)
            canvas.setMatrix(scaleMatrix)
            canvas.drawBitmap(bmp, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        //      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath)
            val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0)
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270f)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap!!, 0, 0,
                    scaledBitmap.width, scaledBitmap.height, matrix,
                    true)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        val file = getImageFile()
        val filename = getFilename()
        val mImageFile = File(file!!.path, filename)
        try {
            out = FileOutputStream(mImageFile)

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap?.compress(Bitmap.CompressFormat.PNG, 80, out)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return mImageFile
    }

    private fun getImageFile(): File? {
        var mediaStorageDir: File? = null
        try {
            mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), OConstants.IMAGE_FOLDER_NAME_COMPRESSED)
            if (!mediaStorageDir.exists()) mediaStorageDir.mkdir()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return mediaStorageDir
    }

    private fun getFilename(): String? {
        var uriSting: String? = null
        try {
            val d = Date()
            uriSting = "Image_" + SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.ENGLISH).format(d) + "_" + selectedResult?.objectID + ".png"
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return uriSting
    }

    fun writeBitmapInFile(bmp: Bitmap) {
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(mFileTemp)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    lateinit var substationTable: ServiceFeatureTable
    lateinit var stationTable: ServiceFeatureTable
    lateinit var LvOhCableTable: ServiceFeatureTable
    lateinit var MvOhCableTable: ServiceFeatureTable
    lateinit var LvdbAreaTable: ServiceFeatureTable
    lateinit var SwitchgearAreaTable: ServiceFeatureTable
    lateinit var FCL_DistributionBoxTable: ServiceFeatureTable
    lateinit var FCL_POLESTable: ServiceFeatureTable
    lateinit var DynamicProtectiveDeviceTable: ServiceFeatureTable
    lateinit var FuseTable: ServiceFeatureTable
    lateinit var TransFormersTable: ServiceFeatureTable
    lateinit var RingMainUnitTable: ServiceFeatureTable
    lateinit var VoltageRegulatorTable: ServiceFeatureTable
    lateinit var ServicePointTable: ServiceFeatureTable
    lateinit var SwitchTable: ServiceFeatureTable

    lateinit var substationLayer: FeatureLayer
    lateinit var stationLayer: FeatureLayer
    lateinit var LvOhCableLayer: FeatureLayer
    lateinit var MvOhCableLayer: FeatureLayer
    lateinit var LvdbAreaLayer: FeatureLayer
    lateinit var SwitchgearAreaLayer: FeatureLayer
    lateinit var FCL_DistributionBoxLayer: FeatureLayer
    lateinit var FCL_POLES_Layer: FeatureLayer
    lateinit var DynamicProtectiveDeviceLayer: FeatureLayer
    lateinit var FuseLayer: FeatureLayer
    lateinit var TransFormersLayer: FeatureLayer
    lateinit var RingMainUnitLayer: FeatureLayer
    lateinit var VoltageRegulatorLayer: FeatureLayer
    lateinit var ServicePointLayer: FeatureLayer
    lateinit var SwitchLayer: FeatureLayer

    lateinit var substationOfflineTable: GeodatabaseFeatureTable
    lateinit var stationOfflineTable: GeodatabaseFeatureTable
    lateinit var LvOhCableOfflineTable: GeodatabaseFeatureTable
    lateinit var MvOhCableOfflineTable: GeodatabaseFeatureTable
    lateinit var LvdbAreaOfflineTable: GeodatabaseFeatureTable
    lateinit var SwitchgearAreaOfflineTable: GeodatabaseFeatureTable
    lateinit var FCL_DistributionBoxOfflineTable: GeodatabaseFeatureTable
    lateinit var FCL_POLESOfflineTable: GeodatabaseFeatureTable
    lateinit var DynamicProtectiveDeviceOfflineTable: GeodatabaseFeatureTable
    lateinit var FuseOfflineTable: GeodatabaseFeatureTable
    lateinit var TransFormersOfflineTable: GeodatabaseFeatureTable
    lateinit var RingMainUnitOfflineTable: GeodatabaseFeatureTable
    lateinit var VoltageRegulatorOfflineTable: GeodatabaseFeatureTable
    lateinit var ServicePointOfflineTable: GeodatabaseFeatureTable
    lateinit var SwitchOfflineTable: GeodatabaseFeatureTable
}