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
import com.esri.arcgisruntime.concurrent.Job
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.*
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.tasks.geodatabase.GeodatabaseSyncTask
import com.sec.datacheck.R
import com.sec.datacheck.checkdata.model.Enums
import com.sec.datacheck.checkdata.model.QueryConfig
import com.sec.datacheck.checkdata.model.models.Columns
import com.sec.datacheck.checkdata.model.models.DataCollectionApplication
import com.sec.datacheck.checkdata.model.models.OConstants
import com.sec.datacheck.checkdata.model.models.OnlineQueryResult
import com.sec.datacheck.checkdata.model.showToast
import com.sec.datacheck.checkdata.view.POJO.FieldModel
import com.sec.datacheck.checkdata.view.fragments.updateFragment.UpdateFragment
import com.sec.datacheck.checkdata.view.utils.Utilities
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutionException
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
    val databasePath = MutableLiveData<String>()
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

            OCL_METERTable = ServiceFeatureTable(context.getString(R.string.OCL_METER))

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

            // set the map to be displayed in the mapView
            mapView.map = baseMap

            fillLayerList(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fillLayerList(context: Context) {

        val station = getLayerInfo(stationLayer, stationTable, stationOfflineTable, Enums.LayerType.STATION, R.drawable.rounded_ic_station, context)
        val subStation = getLayerInfo(substationLayer, substationTable, substationOfflineTable, Enums.LayerType.SUBSTATION, R.drawable.rounded_ic_substation, context)
        val distributionBox = getLayerInfo(FCL_DistributionBoxLayer, FCL_DistributionBoxTable, FCL_DistributionBoxOfflineTable, Enums.LayerType.DistributionBox, R.drawable.rounded_ic_dist_box, context)
        val dynamicProtectiveDevice = getLayerInfo(DynamicProtectiveDeviceLayer, DynamicProtectiveDeviceTable, DynamicProtectiveDeviceOfflineTable, Enums.LayerType.DynamicProtectiveDevice, R.drawable.rounded_ic_dynamic_protection_device, context)
        val fuse = getLayerInfo(FuseLayer, FuseTable, FuseOfflineTable, Enums.LayerType.Fuse, R.drawable.rounded_ic_meter, context)
        val poles = getLayerInfo(FCL_POLES_Layer, FCL_POLESTable, FCL_POLESOfflineTable, Enums.LayerType.Pole, R.drawable.rounded_ic_rmu, context)
        val lvOhCable = getLayerInfo(LvOhCableLayer, LvOhCableTable, LvOhCableOfflineTable, Enums.LayerType.LvOhCable, R.drawable.rounded_ic_lv_oh_cable, context)
        val mvOhCable = getLayerInfo(MvOhCableLayer, MvOhCableTable, MvOhCableOfflineTable, Enums.LayerType.MvOhCable, R.drawable.rounded_ic_mv_oh_cable, context)
        val lvDbArea = getLayerInfo(LvdbAreaLayer, LvdbAreaTable, LvdbAreaOfflineTable, Enums.LayerType.LvdbArea, R.drawable.rounded_ic_lvdb_area, context)
        val switchGearArea = getLayerInfo(SwitchgearAreaLayer, SwitchgearAreaTable, SwitchOfflineTable, Enums.LayerType.SwitchgearArea, R.drawable.rounded_ic_swtich_gear_area, context)
        val transformers = getLayerInfo(TransFormersLayer, TransFormersTable, TransFormersOfflineTable, Enums.LayerType.TransFormers, R.drawable.rounded_ic_transformer, context)
        val ringMainUnit = getLayerInfo(RingMainUnitLayer, RingMainUnitTable, RingMainUnitOfflineTable, Enums.LayerType.RingMainUnit, R.drawable.rounded_ic_ring_main_unit, context)
        val voltageRegulator = getLayerInfo(VoltageRegulatorLayer, VoltageRegulatorTable, VoltageRegulatorOfflineTable, Enums.LayerType.VoltageRegulator, R.drawable.rounded_ic_voltage_regulator, context)
        val servicePoint = getLayerInfo(ServicePointLayer, ServicePointTable, ServicePointOfflineTable, Enums.LayerType.ServicePoint, R.drawable.rounded_ic_service_point, context)
        val switch = getLayerInfo(SwitchLayer, SwitchTable, SwitchOfflineTable, Enums.LayerType.Switch, R.drawable.rounded_ic_swtich, context)

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

    }

    private fun getLayerInfo(substationLayer: FeatureLayer?, serviceFeatureTable: ServiceFeatureTable?, geoDatabaseFeatureTable: GeodatabaseFeatureTable?, layerType: Enums.LayerType, resource: Int, context: Context): OnlineQueryResult {
        val layerInfo = OnlineQueryResult()
        layerInfo.featureLayer = substationLayer
        layerInfo.serviceFeatureTable = serviceFeatureTable
        layerInfo.geodatabaseFeatureTable = geoDatabaseFeatureTable
        layerInfo.drawable = ResourcesCompat.getDrawable(context.resources, resource, context.resources.newTheme())
        layerInfo.layerType = layerType
        return layerInfo
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
                                            queryRelatedOclMeter(mOnlineQueryResult, point)
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

    private fun queryRelatedOclMeter(mOnlineQueryResult: OnlineQueryResult, point: Point?) {
        try {
            if (onlineData) {
                val servicePoint = mOnlineQueryResult.feature

                servicePoint.loadAsync()
                servicePoint.addDoneLoadingListener {
                    if (servicePoint != null && servicePoint.attributes != null && servicePoint.attributes[Columns.SERVICE_POINT.SERVICE_POINT_NO] != null) {
                        val servicePointNo = servicePoint.attributes[Columns.SERVICE_POINT.SERVICE_POINT_NO].toString()
                        if (onlineData) {
                            OCL_METERTable?.addDoneLoadingListener {
                                handleMeterTableLoading(servicePointNo, mOnlineQueryResult, point)
                            }
                            OCL_METERTable?.loadAsync()
                        } else {
                            OCL_METER_OfflineTable?.addDoneLoadingListener {
                                handleMeterTableLoading(servicePointNo, mOnlineQueryResult, point)
                            }
                            OCL_METER_OfflineTable?.loadAsync()
                        }
                    }
                }
            } else {
                val servicePoint = mOnlineQueryResult.featureOffline

                if (servicePoint != null && servicePoint.attributes != null && servicePoint.attributes[Columns.SERVICE_POINT.SERVICE_POINT_NO] != null) {
                    val servicePointNo = servicePoint.attributes[Columns.SERVICE_POINT.SERVICE_POINT_NO].toString()
                    if (onlineData) {
                        OCL_METERTable?.addDoneLoadingListener {
                            handleMeterTableLoading(servicePointNo, mOnlineQueryResult, point)
                        }
                        OCL_METERTable?.loadAsync()
                    } else {
                        OCL_METER_OfflineTable?.addDoneLoadingListener {
                            handleMeterTableLoading(servicePointNo, mOnlineQueryResult, point)
                        }
                        OCL_METER_OfflineTable?.loadAsync()
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleMeterTableLoading(servicePointNo: String, mOnlineQueryResult: OnlineQueryResult, point: Point?) {
        try {
            val relatedQueryParameters = QueryConfig.getRelatedQuery(servicePointNo, Columns.OCL_METER.OCL_METER_FOREIGN_KEY, point)
            val queryResults = if (onlineData) OCL_METERTable?.queryFeaturesAsync(relatedQueryParameters) else
                OCL_METER_OfflineTable?.queryFeaturesAsync(relatedQueryParameters)
            queryResults?.addDoneListener {
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

                            if (onlineData) {
                                onlineQueryResult.feature = mFeature
                                onlineQueryResult.serviceFeatureTable = OCL_METERTable
                            } else {
                                onlineQueryResult.featureOffline = mFeature
                                onlineQueryResult.geodatabaseFeatureTable = OCL_METER_OfflineTable
                            }
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

    fun queryOffline(point: Point?, spatialReference: SpatialReference?) {
        point?.let { mPoint ->
            spatialReference?.let { sp ->
                for (i in layers.indices) {
                    try {
                        val layer = layers[i]
                        val query = QueryConfig.getQuery(mPoint, sp, true)
                        layer.geodatabaseFeatureTable?.let { geodatabaseFeatureTable ->
                            val future: ListenableFuture<FeatureQueryResult> = geodatabaseFeatureTable.queryFeaturesAsync(query)
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
                                            mOnlineQueryResult.featureOffline = feature
                                            mOnlineQueryResult.geodatabaseFeatureTable = geodatabaseFeatureTable
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
                                                queryRelatedOclMeter(mOnlineQueryResult, point)
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
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
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


    fun downloadAndSaveDatabase(serverUrl: String, currentViewpoint: Viewpoint, localDatabaseTitle: String, extent: Envelope?) {
        try {
            // create a geodatabase sync task
            val geodatabaseSyncTask = GeodatabaseSyncTask(serverUrl)
            geodatabaseSyncTask.loadAsync()
            geodatabaseSyncTask.addDoneLoadingListener {
                // create generate geodatabase parameters for the current extent
                val defaultParameters = geodatabaseSyncTask.createDefaultGenerateGeodatabaseParametersAsync(extent)
                defaultParameters.addDoneListener {
                    try {
                        // set parameters and don't include attachments
                        val parameters = defaultParameters.get()
                        parameters.isReturnAttachments = false

                        // define the local path where the geodatabase will be stored
                        val rootFolder = File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DCIM), OConstants.IMAGE_FOLDER_NAME)
                        if (!rootFolder.exists()) rootFolder.mkdirs()
                        val file1 = File(rootFolder.path, "geodatabase")
                        if (!file1.exists()) {
                            file1.mkdirs()
                        }
                        val databaseFile = File(file1.path, "$localDatabaseTitle.geodatabase")
                        val localGeodatabasePath = databaseFile.path

                        // create and start the job
                        val generateGeodatabaseJob = geodatabaseSyncTask.generateGeodatabaseAsync(parameters, localGeodatabasePath)
                        generateGeodatabaseJob.start()

                        // get geodatabase when done
                        generateGeodatabaseJob.addJobDoneListener {
                            if (generateGeodatabaseJob.status == Job.Status.SUCCEEDED) {
                                val geodatabase = generateGeodatabaseJob.result
                                geodatabase.loadAsync()
                                geodatabase.addDoneLoadingListener {
                                    if (geodatabase.loadStatus == LoadStatus.LOADED) {
                                        for (geodatabaseFeatureTable in geodatabase
                                                .geodatabaseFeatureTables) {
                                            geodatabaseFeatureTable.loadAsync()
                                        }
                                        val dbNum = DataCollectionApplication.getDatabaseNumber()
                                        DataCollectionApplication.setLocalDatabaseTitle(localDatabaseTitle, dbNum)
                                        DataCollectionApplication.incrementDatabaseNumber()
                                        Log.i(TAG, "Local geodatabase stored at: $localGeodatabasePath")
                                        databasePath.value = databaseFile.path
                                        DataCollectionApplication.addBookMark(currentViewpoint.toJson(), localDatabaseTitle)
                                        Utilities.dismissLoadingDialog()
                                    } else {
                                        Log.e(TAG, "Error loading geodatabase: " + geodatabase.loadError.message)
                                    }
                                }
                            } else if (generateGeodatabaseJob.error != null) {
                                Log.e(TAG, "Error generating geodatabase: " + generateGeodatabaseJob.error.message)
                                generateGeodatabaseJob.error.printStackTrace()
                                Utilities.dismissLoadingDialog()
                            } else {
                                Log.e(TAG, "Unknown Error generating geodatabase")
                            }
                        }
                    } catch (e: InterruptedException) {
                        Log.e(TAG, "Error generating geodatabase parameters : " + e.message)
                        Utilities.dismissLoadingDialog()
                    } catch (e: ExecutionException) {
                        Log.e(TAG, "Error generating geodatabase parameters : " + e.message)
                        Utilities.dismissLoadingDialog()
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun loadGeodatabase(databasePath: String?, mapView: MapView, map: ArcGISMap, context: Context?) {

        try {// create a new geodatabase from local path
            val geodatabase = Geodatabase(databasePath)

            // load the geodatabase
            geodatabase.loadAsync()

            // create feature layer from geodatabase and add to the map
            geodatabase.addDoneLoadingListener {
                if (geodatabase.loadStatus == LoadStatus.LOADED) {
                    map.operationalLayers.clear()
                    geodatabase.geodatabaseFeatureTables.forEach { geodatabaseFeatureTable ->
                        geodatabaseFeatureTable.loadAsync()
                        // create a layer from the geodatabase feature table and add to map
                        associateOfflineTables(geodatabaseFeatureTable, map)
                    }
                    mapView.map = map
                    context?.let {
                        fillLayerList(it)
                    }
                } else {
                    context?.let {
                        (it as MainActivity).showToast("Geodatabase failed to load!")
                    }
                }
                mapView.setViewpointAsync(Viewpoint(geodatabase.generateGeodatabaseGeometry.extent), 0.5f)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun associateOfflineTables(table: GeodatabaseFeatureTable?, baseMap: ArcGISMap) {
        when (table?.serviceLayerId) {
            Enums.TableId.Station.id -> {
                stationOfflineTable = table
                stationLayer = FeatureLayer(stationOfflineTable)
                baseMap.operationalLayers.add(stationLayer)
                stationLayer.loadAsync()
            }
            Enums.TableId.SubStation.id -> {
                substationOfflineTable = table
                substationLayer = FeatureLayer(substationOfflineTable)
                baseMap.operationalLayers.add(substationLayer)
                substationLayer.loadAsync()
            }
            Enums.TableId.DistributionBox.id -> {
                FCL_DistributionBoxOfflineTable = table
                FCL_DistributionBoxLayer = FeatureLayer(FCL_DistributionBoxOfflineTable)
                baseMap.operationalLayers.add(FCL_DistributionBoxLayer)
                FCL_DistributionBoxLayer.loadAsync()
            }
            Enums.TableId.TransFormers.id -> {
                TransFormersOfflineTable = table
                TransFormersLayer = FeatureLayer(TransFormersOfflineTable)
                baseMap.operationalLayers.add(TransFormersLayer)
                TransFormersLayer.loadAsync()
            }
            Enums.TableId.RingMainUnit.id -> {
                RingMainUnitOfflineTable = table
                RingMainUnitLayer = FeatureLayer(RingMainUnitOfflineTable)
                baseMap.operationalLayers.add(RingMainUnitLayer)
                RingMainUnitLayer.loadAsync()
            }
            Enums.TableId.Pole.id -> {
                FCL_POLESOfflineTable = table
                FCL_POLES_Layer = FeatureLayer(FCL_POLESOfflineTable)
                baseMap.operationalLayers.add(FCL_POLES_Layer)
                FCL_POLES_Layer.loadAsync()
            }
            Enums.TableId.VoltageRegulator.id -> {
                VoltageRegulatorOfflineTable = table
                VoltageRegulatorLayer = FeatureLayer(VoltageRegulatorOfflineTable)
                baseMap.operationalLayers.add(VoltageRegulatorLayer)
                VoltageRegulatorLayer.loadAsync()
            }
            Enums.TableId.ServicePoint.id -> {
                ServicePointOfflineTable = table
                ServicePointLayer = FeatureLayer(ServicePointOfflineTable)
                baseMap.operationalLayers.add(ServicePointLayer)
                ServicePointLayer.loadAsync()
            }
            Enums.TableId.Switch.id -> {
                SwitchOfflineTable = table
                SwitchLayer = FeatureLayer(SwitchOfflineTable)
                baseMap.operationalLayers.add(SwitchLayer)
                SwitchLayer.loadAsync()
            }
            Enums.TableId.Fuse.id -> {
                FuseOfflineTable = table
                FuseLayer = FeatureLayer(FuseOfflineTable)
                baseMap.operationalLayers.add(FuseLayer)
                FuseLayer.loadAsync()
            }
            Enums.TableId.DynamicProtectiveDevice.id -> {
                DynamicProtectiveDeviceOfflineTable = table
                DynamicProtectiveDeviceLayer = FeatureLayer(DynamicProtectiveDeviceOfflineTable)
                baseMap.operationalLayers.add(DynamicProtectiveDeviceLayer)
                DynamicProtectiveDeviceLayer.loadAsync()
            }
            Enums.TableId.MvOhCable.id -> {
                MvOhCableOfflineTable = table
                MvOhCableLayer = FeatureLayer(MvOhCableOfflineTable)
                baseMap.operationalLayers.add(MvOhCableLayer)
                MvOhCableLayer.loadAsync()
            }
            Enums.TableId.LvOhCable.id -> {
                LvOhCableOfflineTable = table
                LvOhCableLayer = FeatureLayer(LvOhCableOfflineTable)
                baseMap.operationalLayers.add(LvOhCableLayer)
                LvOhCableLayer.loadAsync()
            }
            Enums.TableId.LvdbArea.id -> {
                LvdbAreaOfflineTable = table
                LvdbAreaLayer = FeatureLayer(LvdbAreaOfflineTable)
                baseMap.operationalLayers.add(LvdbAreaLayer)
                LvdbAreaLayer.loadAsync()
            }
            Enums.TableId.SwitchgearArea.id -> {
                SwitchgearAreaOfflineTable = table
                SwitchgearAreaLayer = FeatureLayer(SwitchgearAreaOfflineTable)
                baseMap.operationalLayers.add(SwitchgearAreaLayer)
                SwitchgearAreaLayer.loadAsync()
            }
            Enums.TableId.OCLMeter.id -> {
                OCL_METER_OfflineTable = table
                OCL_METER_OfflineTable?.loadAsync()
            }
        }
    }

    private fun isValidLayer(tableId: Long): Boolean {
        return when (tableId) {
            Enums.TableId.OCLMeter.id -> {
                false
            }
            else -> {
                true
            }
        }
    }

    var substationTable: ServiceFeatureTable? = null
    var stationTable: ServiceFeatureTable? = null
    var LvOhCableTable: ServiceFeatureTable? = null
    var MvOhCableTable: ServiceFeatureTable? = null
    var LvdbAreaTable: ServiceFeatureTable? = null
    var SwitchgearAreaTable: ServiceFeatureTable? = null
    var FCL_DistributionBoxTable: ServiceFeatureTable? = null
    var FCL_POLESTable: ServiceFeatureTable? = null
    var DynamicProtectiveDeviceTable: ServiceFeatureTable? = null
    var FuseTable: ServiceFeatureTable? = null
    var TransFormersTable: ServiceFeatureTable? = null
    var RingMainUnitTable: ServiceFeatureTable? = null
    var VoltageRegulatorTable: ServiceFeatureTable? = null
    var ServicePointTable: ServiceFeatureTable? = null
    var SwitchTable: ServiceFeatureTable? = null
    var OCL_METERTable: ServiceFeatureTable? = null

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

    var substationOfflineTable: GeodatabaseFeatureTable? = null
    var stationOfflineTable: GeodatabaseFeatureTable? = null
    var LvOhCableOfflineTable: GeodatabaseFeatureTable? = null
    var MvOhCableOfflineTable: GeodatabaseFeatureTable? = null
    var LvdbAreaOfflineTable: GeodatabaseFeatureTable? = null
    var SwitchgearAreaOfflineTable: GeodatabaseFeatureTable? = null
    var FCL_DistributionBoxOfflineTable: GeodatabaseFeatureTable? = null
    var FCL_POLESOfflineTable: GeodatabaseFeatureTable? = null
    var DynamicProtectiveDeviceOfflineTable: GeodatabaseFeatureTable? = null
    var FuseOfflineTable: GeodatabaseFeatureTable? = null
    var TransFormersOfflineTable: GeodatabaseFeatureTable? = null
    var RingMainUnitOfflineTable: GeodatabaseFeatureTable? = null
    var VoltageRegulatorOfflineTable: GeodatabaseFeatureTable? = null
    var ServicePointOfflineTable: GeodatabaseFeatureTable? = null
    var SwitchOfflineTable: GeodatabaseFeatureTable? = null
    var OCL_METER_OfflineTable: GeodatabaseFeatureTable? = null
}