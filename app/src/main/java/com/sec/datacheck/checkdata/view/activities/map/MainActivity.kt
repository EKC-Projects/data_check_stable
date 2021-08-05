package com.sec.datacheck.checkdata.view.activities.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.PointCollection
import com.esri.arcgisruntime.geometry.Polyline
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.esri.arcgisruntime.mapping.view.LocationDisplay.DataSourceStatusChangedEvent
import com.esri.arcgisruntime.mapping.view.WrapAroundMode
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.sec.datacheck.R
import com.sec.datacheck.checkdata.model.*
import com.sec.datacheck.checkdata.model.models.BookMark
import com.sec.datacheck.checkdata.model.models.DataCollectionApplication
import com.sec.datacheck.checkdata.model.models.OConstants
import com.sec.datacheck.checkdata.model.models.OnlineQueryResult
import com.sec.datacheck.checkdata.view.activities.MapSingleTapListener
import com.sec.datacheck.checkdata.view.adapter.BookMarkAdapter
import com.sec.datacheck.checkdata.view.adapter.MultiResultAdapter
import com.sec.datacheck.checkdata.view.callbacks.mapCallbacks.GpsListener
import com.sec.datacheck.checkdata.view.callbacks.mapCallbacks.SingleTapListener
import com.sec.datacheck.checkdata.view.fragments.newUpdateFragement.NewUpdateFragment
import com.sec.datacheck.checkdata.view.utils.Utilities
import com.sec.datacheck.databinding.ActivityMain2Binding
import com.sec.datacheck.databinding.MapSelectPointBottomSheetBinding
import kotlinx.android.synthetic.main.map_select_point_bottom_sheet.view.*
import java.io.File
import java.util.*
import java.util.concurrent.ExecutionException

class MainActivity : AppCompatActivity(), View.OnClickListener, SingleTapListener, MultiResultAdapter.MultiResultListener {


    private val viewModel by lazy {
        ViewModelProvider(this).get(MapViewModel::class.java)
    }

    private lateinit var binding: ActivityMain2Binding
    private lateinit var bottomSheetBinding: MapSelectPointBottomSheetBinding
    private lateinit var mMatrix: Matrix
    private lateinit var mCompassBitmap: Bitmap
    private var sheetBehavior: BottomSheetBehavior<*>? = null
    private var graphicsOverlay: GraphicsOverlay? = null
    private var drawGraphicLayer: GraphicsOverlay? = null
    private var selectedResult: OnlineQueryResult? = null
    private var selectedMapType: Enums.MapType = Enums.MapType.DEFAULT_MAP
    private lateinit var baseMap: ArcGISMap
    private var pointCollection: PointCollection? = null
    private var pictureMarkerSymbol: PictureMarkerSymbol? = null
    private var mCurrentLocation: Point? = null
    private lateinit var mapSingleTapListener: MapSingleTapListener
    private var drawMeasure: Boolean = false
    private var queryStatus: Boolean = false
    private var menuItemOnline: MenuItem? = null
    private var menuItemSync: MenuItem? = null
    private var menuItemOffline: MenuItem? = null
    private var item_load_previous_offline: MenuItem? = null
    private var menuItemGoOfflineMode: MenuItem? = null
    private var menuItemGoOnlineMode: MenuItem? = null
    private var menuItemOverflow: MenuItem? = null
    private var loadMaps: MenuItem? = null
    private var mDefaultMapItem: MenuItem? = null
    private var mOpenStreetMapItem: MenuItem? = null
    private var mGoogleItem: MenuItem? = null
    private var isFullScreenMode: Boolean = false
    private var isFragmentShown: Boolean = false
    private var syncAndGoOnline = false
    private var lastPointStep: Point? = null
    private var dialogView: View? = null
    private var currentViewPoint: Viewpoint? = null
    private lateinit var mMultiResultRecAdapter: MultiResultAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main2)
        bottomSheetBinding = DataBindingUtil.findBinding(binding.root.bottom_sheet)!!
        init()
    }

    private fun init() {
        try {
            requestPermissions()
            handleCompass()
            initSheetBehavior()
            initMap(selectedMapType)
            viewModel.prepareOnlineLayers(baseMap, binding.mapView, baseContext)
            // displaying user location on map
            showDeviceLocation()
            initSingleTap()
            initObservers()
            handleFab()
            handleClickActions()
            handleOnlineStatus()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleOnlineStatus() {
        if (Utilities.isNetworkAvailable(this)) {
            viewModel.onlineData = true
        } else {
            viewModel.onlineData = false
            showOfflineMapsList()
        }
    }

    private fun showOfflineMapsList() {
        //Declaring List to hold offline database titles
        val databaseTitles = DataCollectionApplication.getOfflineDatabasesTitle()

        //Declaring List to hold NonNull offline database titles
        val databaseTitlesWithoutNull = ArrayList<String>()
        //Filtering NonNull database titles
        for (title in databaseTitles) {
            if (title != null) {
                databaseTitlesWithoutNull.add(title)
            }
        }
        //Handle database's titles not null
        if (databaseTitlesWithoutNull.size > 0) {
            //declaring array to hold database titles
            var titles: Array<String?> = arrayOfNulls<String>(databaseTitlesWithoutNull.size)
            //Converting ArrayList to array
            titles = databaseTitlesWithoutNull.toArray(titles)
            //calling method to display dialog with available offline database titles
            displayTitlesOfflineMapDialog(titles, DialogInterface.OnClickListener { dialog, which ->
                try {

                    val selectedTitle: String = titles[which]!!
                    var selectedVersion = 0
                    for (i in databaseTitles.indices) {
                        if (selectedTitle == databaseTitles[i]) {
                            selectedVersion = i + 1
                            break
                        }
                    }
                    viewModel.onlineData = false
                    viewModel.currentOfflineVersion = selectedVersion // TODO un comment
                    viewModel.currentOfflineVersionTitle = selectedTitle
                    val databasePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path + File.separator + OConstants.IMAGE_FOLDER_NAME + File.separator + OConstants.ROOT_GEO_DATABASE_PATH + File.separator + selectedTitle + ".geodatabase"

                    viewModel.loadGeodatabase(databasePath, binding.mapView, baseMap, this)
                    handleOfflineMenu()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        } else {
            //displaying No Offline Map Dialog
            showNoOfflineMapDialog()
        }
    }

    private fun handleClickActions() {
        //map legend
        binding.tvMoreLayerInfo.setOnClickListener(this)
        //Compass rotation
        binding.compass.setOnClickListener(this)
    }

    private fun initObservers() {
        try {
            viewModel.queryFinished.observe(this, Observer {
                if (it) {
                    Utilities.dismissLoadingDialog()
                    if (!viewModel.mOnlineQueryResults.isNullOrEmpty()) {
                        //display bottom sheet list
                        handleMultiOnlineQueryResult()

                    } else {
                        // display please zoom more
                        showToast(getString(R.string.zoom_more))
                    }
                    viewModel.queryFinished.value = false
                    queryStatus = false
                }
            })

            viewModel.databasePath.observe(this, Observer { databasePath ->
                if (!databasePath.isNullOrEmpty()) {
                    viewModel.onlineData = false
                    viewModel.loadGeodatabase(databasePath, binding.mapView, baseMap, this)
                    handleOfflineMenu()
                }
            })

            viewModel.syncStatus.observe(this, Observer {
                when (it) {
                    Enums.SyncStatus.Synced -> {
                        showToast(getString(R.string.sync_completed))
                        if (syncAndGoOnline) {
                            goOnline()
                        }
                    }
                    Enums.SyncStatus.FAILED -> {
                        showToast(getString(R.string.sync_failed_retrying))
                    }
                }
            })

            viewModel.isUpdated.observe(this, Observer {
                if (it) {
                    Utilities.dismissLoadingDialog()
                    viewModel.clearData()
                    selectedResult = null
                    hideFragment()
                    showActivityViews()
                    showToast(getString(R.string.updated_successfuly))
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDeviceLocation() {

        try {
            if (!checkGPSProvider()) {
                setUpLocationRequest(object : GpsListener {
                    override fun onResponse(status: Boolean) {
                        if (status) {
                            val locationDisplay: LocationDisplay = binding.mapView.locationDisplay
                            locationDisplay.autoPanMode = LocationDisplay.AutoPanMode.NAVIGATION

                            locationDisplay.addLocationChangedListener { locationChangedEvent -> //reading location changing
                                mCurrentLocation = locationChangedEvent.location.position
                                if (mCurrentLocation != null) {
                                    var accuracy = locationChangedEvent.location.horizontalAccuracy
                                    val mAccuracy = (accuracy * 100).toInt()
                                    accuracy = mAccuracy.toDouble() / 100
                                    val latLang = "Lat: " + Utilities.round(mCurrentLocation!!.x, 4) + " Lan: " + Utilities.round(mCurrentLocation!!.y, 4) + " Accuracy = " + accuracy
                                    binding.tvLatLong.text = latLang
                                }
                                mMatrix.reset()
                                mMatrix.postRotate(-binding.mapView.rotation, mCompassBitmap.height / 2.toFloat(), mCompassBitmap.width / 2.toFloat())
                                binding.compass.imageMatrix = mMatrix
                            }

                            locationDisplay.addDataSourceStatusChangedListener { dataSourceStatusChangedEvent: DataSourceStatusChangedEvent ->
                                if (dataSourceStatusChangedEvent.source.locationDataSource.error != null) {
                                    dataSourceStatusChangedEvent.source.locationDataSource.error.printStackTrace()
                                }
                            }
                            locationDisplay.startAsync()
                        }
                    }

                })
            }else{
                val locationDisplay: LocationDisplay = binding.mapView.locationDisplay
                locationDisplay.autoPanMode = LocationDisplay.AutoPanMode.NAVIGATION

                locationDisplay.addLocationChangedListener { locationChangedEvent -> //reading location changing
                    mCurrentLocation = locationChangedEvent.location.position
                    if (mCurrentLocation != null) {
                        var accuracy = locationChangedEvent.location.horizontalAccuracy
                        val mAccuracy = (accuracy * 100).toInt()
                        accuracy = mAccuracy.toDouble() / 100
                        val latLang = "Lat: " + Utilities.round(mCurrentLocation!!.x, 4) + " Lan: " + Utilities.round(mCurrentLocation!!.y, 4) + " Accuracy = " + accuracy
                        binding.tvLatLong.text = latLang
                    }
                    mMatrix.reset()
                    mMatrix.postRotate(-binding.mapView.rotation, mCompassBitmap.height / 2.toFloat(), mCompassBitmap.width / 2.toFloat())
                    binding.compass.imageMatrix = mMatrix
                }

                locationDisplay.addDataSourceStatusChangedListener { dataSourceStatusChangedEvent: DataSourceStatusChangedEvent ->
                    if (dataSourceStatusChangedEvent.source.locationDataSource.error != null) {
                        dataSourceStatusChangedEvent.source.locationDataSource.error.printStackTrace()
                    }
                }
                locationDisplay.startAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initMap(mapType: Enums.MapType) {
        //Declaring baseMap

        try {
            val basemap = Basemap()
            when (mapType) {
                Enums.MapType.OPEN_STREET_MAP -> {
                    val tiledLayer = ArcGISTiledLayer(getString(R.string.open_street_map_url))
                    basemap.baseLayers.add(tiledLayer)
                    baseMap = ArcGISMap(basemap)
                }
                Enums.MapType.GOOGLE_MAP -> {
                    val tiledLayer = ArcGISTiledLayer(getString(R.string.google_map_url))
                    basemap.baseLayers.add(tiledLayer)
                    baseMap = ArcGISMap(basemap)
                }
                Enums.MapType.DEFAULT_MAP -> {
                    baseMap = ArcGISMap(Basemap.createOpenStreetMap())
                    baseMap.maxScale = 1.0
                }
            }

            binding.mapView.map = baseMap
            pointCollection = PointCollection(binding.mapView.spatialReference)

            // wraparound is enabled if layers within map support it
            binding.mapView.wrapAroundMode = WrapAroundMode.ENABLE_WHEN_SUPPORTED
            graphicsOverlay = GraphicsOverlay()
            pictureMarkerSymbol = PictureMarkerSymbol(ResourcesCompat.getDrawable(resources, R.drawable.ic_marker_64, resources.newTheme()) as BitmapDrawable)
            pictureMarkerSymbol?.offsetY = 10f
            binding.mapView.graphicsOverlays.add(graphicsOverlay)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleFab() {
        binding.fabMeasure.setClosedOnTouchOutside(true)
        binding.fabMeasureArea.setOnClickListener(this)
        binding.fabMeasureDistance.setOnClickListener(this)
        binding.fabLocation.setOnClickListener(this)
        binding.fabFullScreen.setOnClickListener(this)
    }

    private fun handleCompass() {
        binding.compass.scaleType = ImageView.ScaleType.MATRIX
        mMatrix = Matrix()
        mCompassBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_compass)

    }

    private fun initSheetBehavior() {
        try {
            sheetBehavior = BottomSheetBehavior.from<LinearLayout>(bottomSheetBinding.bottomSheet)
            sheetBehavior?.setPeekHeight(150, true)
            sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            /**
             * bottom sheet state change listener
             * we are changing button text when sheet changed state
             */
            sheetBehavior?.isHideable = true
            sheetBehavior?.isDraggable = false
            sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            sheetBehavior?.addBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        graphicsOverlay?.graphics?.clear()
                        selectedResult = null
                    }
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSingleTap() {
        try {
            mapSingleTapListener = MapSingleTapListener(this, binding.mapView, this)
            binding.mapView.onTouchListener = mapSingleTapListener
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initMapRotation() {
        try {
            mMatrix.reset()
            val viewpointSetFuture: ListenableFuture<Boolean> = binding.mapView.setViewpointRotationAsync(0.0)
            viewpointSetFuture.addDoneListener {
                try {
                    val completed = viewpointSetFuture.get()
                    if (completed) {
                        mMatrix.postRotate(-binding.mapView.rotation, mCompassBitmap.height / 2.toFloat(), mCompassBitmap.width / 2.toFloat())
                        binding.compass.imageMatrix = mMatrix
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: ExecutionException) {
                    // Deal with exception during animation...
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        menu?.let {
            menuItemOffline = it.findItem(R.id.item_go_offline)
            item_load_previous_offline = it.findItem(R.id.item_load_previous_offline)
            menuItemOnline = it.findItem(R.id.item_go_online)
            menuItemSync = it.findItem(R.id.item_sync)
            menuItemGoOfflineMode = it.findItem(R.id.item_go_offline_mode)
            menuItemGoOnlineMode = it.findItem(R.id.item_go_online_mode)
            menuItemOverflow = it.findItem(R.id.overflow)
            loadMaps = it.findItem(R.id.item_load_maps)
            mDefaultMapItem = it.findItem(R.id.default_map)
            mOpenStreetMapItem = it.findItem(R.id.open_street_map)
            mGoogleItem = it.findItem(R.id.google_map)

        }

        mDefaultMapItem?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_done_24, resources.newTheme())
        mOpenStreetMapItem?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_fiber_manual_record_24, resources.newTheme())
        mGoogleItem?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_fiber_manual_record_24, resources.newTheme())

        if (!viewModel.onlineData) {
            handleOfflineMenu()
        } else {
            handleOnlineMenu()
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun handleOnlineMenu() {
        try {
            item_load_previous_offline!!.isVisible = true
            menuItemGoOfflineMode!!.isVisible = true
            menuItemGoOnlineMode!!.isVisible = false
            if (isFragmentShown) {
                hideActivityViews()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleOfflineMenu() {
        try {
            item_load_previous_offline!!.isVisible = true
            menuItemOffline!!.isVisible = false
            menuItemSync!!.isVisible = true
            menuItemOnline!!.isVisible = true
            menuItemGoOfflineMode!!.isVisible = false
            menuItemGoOnlineMode!!.isVisible = true
            if (isFragmentShown) {
                hideActivityViews()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideActivityViews() {
        try {
            hide(binding.compass)
            hide(binding.fabFullScreen)
            hide(binding.fabMeasure)
            hide(binding.fabLocation)
            hide(binding.linearLayersInfo)
            hide(binding.tvLatLong)

            item_load_previous_offline!!.isVisible = false
            menuItemOffline!!.isVisible = false
            menuItemGoOfflineMode!!.isVisible = false
            menuItemGoOnlineMode!!.isVisible = false
            menuItemOverflow!!.isVisible = false
            loadMaps!!.isVisible = false
            menuItemSync!!.isVisible = false
            menuItemOnline!!.isVisible = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showActivityViews() {
        try {
            show(binding.compass)
            show(binding.fabFullScreen)
            show(binding.fabMeasure)
            show(binding.fabLocation)
            show(binding.linearLayersInfo)
            show(binding.tvLatLong)

            if (viewModel.onlineData) {
                item_load_previous_offline!!.isVisible = true
                menuItemGoOfflineMode!!.isVisible = true
                menuItemGoOnlineMode!!.isVisible = false
                menuItemSync!!.isVisible = false
                menuItemOnline!!.isVisible = false
                menuItemOffline!!.isVisible = true
                loadMaps!!.isVisible = true
            } else {
                menuItemOffline!!.isVisible = false
                menuItemGoOfflineMode!!.isVisible = false
                menuItemGoOnlineMode!!.isVisible = true
                menuItemSync!!.isVisible = true
                menuItemOnline!!.isVisible = true
                item_load_previous_offline!!.isVisible = true
                loadMaps!!.isVisible = true
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_go_offline -> {
                // define permission to request
                val reqPermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (ContextCompat.checkSelfPermission(this, reqPermission[0]) != PackageManager.PERMISSION_GRANTED) {
                    // request permission
                    requestPermissions()
                } else {
                    goOffline()
                }
                return true
            }
            R.id.item_load_previous_offline -> {
                showOfflineMapsList()
                return true
            }
            R.id.item_go_online -> {
                syncAndGoOnline()
                return true
            }
            R.id.item_sync -> {
                syncData()
                return true
            }
            R.id.item_Add_Bookmark -> showAddNewBookmarkDialog()
            R.id.item_Show_Bookmarks -> showBookmarksDialog()
            R.id.item_go_offline_mode -> {
                try {
                    goOffline()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                return true
            }
            R.id.item_go_online_mode -> {
                try {
                    if (Utilities.isNetworkAvailable(this)) {
                        goOnline()
                    } else {
                        Utilities.showInfoDialog(this, getString(R.string.network_connection_failed), getString(R.string.please_your_network_connect))
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                return true
            }
            R.id.open_street_map -> {
                loadOpenStreetMap()
                return true
            }
            R.id.google_map -> {
                loadGoogleMap()
                return true
            }
            R.id.default_map -> {
                loadDefaultMap()
                return true
            }
            android.R.id.home -> {
                if (isFragmentShown) {
                    onBackPressed()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        try {
            if (isFragmentShown) {
                Utilities.showConfirmDialog(this, "", "هل انت متأكد من الرجوع ؟") { dialog, which ->
                    if (which == -1) {
                        selectedResult = null
                        viewModel.clearData()
                        hideFragment()
                        showActivityViews()
                    }
                    dialog.dismiss()
                }
            } else {
                Utilities.showConfirmDialog(this, "", "هل تريد الخروج ؟") { dialog, which ->
                    if (which == -1) {
                        dialog.dismiss()
                        finish()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadGoogleMap() {
        try {
            if (Utilities.isNetworkAvailable(this)) {
                mDefaultMapItem?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_fiber_manual_record_24, resources.newTheme())
                mOpenStreetMapItem?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_fiber_manual_record_24, resources.newTheme())
                mGoogleItem?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_done_24, resources.newTheme())

                selectedMapType = Enums.MapType.GOOGLE_MAP
                currentViewPoint = binding.mapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY)
                initMap(selectedMapType)
                if (viewModel.onlineData) {
                    viewModel.prepareOnlineLayers(baseMap, binding.mapView, baseContext)
                } else {
//                    presenter.addLocalLayers(mapView, baseMap, currentOfflineVersion, currentOfflineVersionTitle)
                }
                zoomToViewPoint(currentViewPoint!!)
            } else {
                showAlertDialog(getString(R.string.no_internet), getString(R.string.ok))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun loadOpenStreetMap() {
        try {
            if (Utilities.isNetworkAvailable(this)) {
                mDefaultMapItem?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_fiber_manual_record_24, resources.newTheme())
                mOpenStreetMapItem?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_done_24, resources.newTheme())
                mGoogleItem?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_fiber_manual_record_24, resources.newTheme())
                selectedMapType = Enums.MapType.OPEN_STREET_MAP
                currentViewPoint = binding.mapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY)
                initMap(selectedMapType)
                if (viewModel.onlineData) {
                    viewModel.prepareOnlineLayers(baseMap, binding.mapView, baseContext)
                } else {
//                    presenter.addLocalLayers(bindingmapView, baseMap, currentOfflineVersion, currentOfflineVersionTitle)
                }
                zoomToViewPoint(currentViewPoint!!)
            } else {
                showAlertDialog(getString(R.string.no_internet), getString(R.string.ok))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun loadDefaultMap() {
        try {
            mDefaultMapItem?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_done_24, resources.newTheme())
            mOpenStreetMapItem?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_fiber_manual_record_24, resources.newTheme())
            mGoogleItem?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_fiber_manual_record_24, resources.newTheme())
            selectedMapType = Enums.MapType.DEFAULT_MAP
            currentViewPoint = binding.mapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY)
            initMap(selectedMapType)
            if (viewModel.onlineData) {
                viewModel.prepareOnlineLayers(baseMap, binding.mapView, baseContext)
            } else {
//                presenter.addLocalLayers(binding.mapView, baseMap, currentOfflineVersion, currentOfflineVersionTitle)
            }
            zoomToViewPoint(currentViewPoint!!)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun goOnline() {
        try {
            if (Utilities.isNetworkAvailable(this)) {
                currentViewPoint = binding.mapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY)
                initMap(selectedMapType)
                viewModel.prepareOnlineLayers(baseMap, binding.mapView, baseContext)
                zoomToViewPoint(currentViewPoint!!)
                viewModel.onlineData = true
                showActivityViews()
            } else {
                showInfoDialog(getString(R.string.network_connection_failed), getString(R.string.please_your_network_connect))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun zoomToViewPoint(currentViewPoint: Viewpoint) {
        try {
            binding.mapView.setViewpointAsync(currentViewPoint)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showBookmarksDialog() {
        val bookMarks = DataCollectionApplication.getAllBookMarks()

        if (bookMarks.size > 0) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.dialog_show_bookmarks_title))
            dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_show_bookmarks, null, false)
            val listView: ListView = dialogView?.findViewById(R.id.lvBookmarks) as ListView
            builder.setView(dialogView)
            val alertDialog = builder.create()
            val bookMarkAdapter = BookMarkAdapter(this, bookMarks, alertDialog)
            listView.adapter = bookMarkAdapter
            alertDialog.show()
            listView.onItemClickListener = OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                try {
                    val bookMark = bookMarkAdapter.getItem(position) as BookMark
                    val viewpoint = Viewpoint.fromJson(bookMark.json)
                    binding.mapView.setViewpoint(viewpoint)
                    alertDialog.dismiss()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            Utilities.showToast(this, getString(R.string.no_bookmarks))
        }
    }

    private fun showAddNewBookmarkDialog() {

        val mBookMarkDlg = Utilities.showAlertDialogWithCustomView(this, R.layout.book_mark_layout, getString(R.string.dialog_bookmark_cancel))

        val bookmarkET = mBookMarkDlg.view.findViewById<EditText>(R.id.book_mark_name_edit_text)
        val bookmarkBtn = mBookMarkDlg.view.findViewById<Button>(R.id.book_mark_save_btn)
        bookmarkBtn.setOnClickListener {
            if (bookmarkET.text == null || bookmarkET.text.toString().isEmpty()) {
                bookmarkET.error = getString(R.string.required)
            } else {
                val title = bookmarkET.text.toString()
                saveBookMark(title, binding.mapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY))
                mBookMarkDlg.dismiss()
            }
        }
    }

    private fun saveBookMark(title: String, currentViewpoint: Viewpoint?) {
        DataCollectionApplication.addBookMark(currentViewpoint!!.toJson(), title)
    }

    private fun syncAndGoOnline() {
        syncData()
        syncAndGoOnline = true
    }

    private fun syncData() {
        try {
            if (Utilities.isNetworkAvailable(this)) {
                viewModel.syncData(viewModel.currentOfflineVersionTitle, getString(R.string.gcs_feature_server), this)
            } else {
                showInfoDialog(getString(R.string.network_connection_failed), getString(R.string.please_your_network_connect))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun goOffline() {
        try {
            val materialDialog = Utilities.showAlertDialogWithCustomView(this, R.layout.dialog_local_db_name, getString(R.string.cancel))
            val databaseName = materialDialog.findViewById(R.id.local_db_name_edit_text) as EditText
            val dialogCloseButton = materialDialog.findViewById(R.id.local_db_download_btn) as Button
            dialogCloseButton.setOnClickListener { v: View? ->
                val localDatabaseTitle = databaseName.text.toString()
                if (localDatabaseTitle == "") {
                    databaseName.error = getString(R.string.name_validation)
                } else {
                    try {
                        menuItemGoOfflineMode!!.isVisible = false
                        menuItemGoOnlineMode!!.isVisible = true
                        materialDialog.dismiss()
                        //Async task
                        graphicsOverlay!!.graphics.clear()
                        Utilities.showLoadingDialog(this)
                        viewModel.currentOfflineVersionTitle = localDatabaseTitle
                        currentViewPoint = binding.mapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY)
                        viewModel.downloadAndSaveDatabase(getString(R.string.gcs_feature_server), currentViewPoint!!, localDatabaseTitle, binding.mapView.visibleArea.extent)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == OConstants.MY_LOCATION_REQUEST_CODE) {
            try {
                if (!permissions.isNullOrEmpty()) {
                    if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED || permissions[0] == Manifest.permission.ACCESS_COARSE_LOCATION &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        showDeviceLocation()
                    } else {
                        Utilities.showToast(this, getString(R.string.please_open_gps_location))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.pause()
    }

    override fun onClick(v: View?) {
        v?.let { view ->
            when (view) {
                binding.fabLocation -> {
                    if (mCurrentLocation != null) {
                        zoomToCurrentLocation()
                    }else{
                        showDeviceLocation()
                    }
                }
                binding.tvMoreLayerInfo -> {
                    if (binding.linearLayersDetails.visibility == View.VISIBLE) {
                        binding.linearLayersDetails.visibility = View.GONE
                    } else {
                        binding.linearLayersDetails.visibility = View.VISIBLE
                    }
                }
                binding.fabFullScreen -> {
                    if (isFullScreenMode) {
                        exitFullScreenMode()
                    } else {
                        fullScreenMode()
                    }
                }
                binding.fabMeasureDistance -> {
                    handleFabMeasureAction()
                    binding.fabMeasure.close(true)
                    drawMeasure = true
                    viewModel.shapeType = Enums.SHAPE.POLYLINE
                }
                binding.compass -> {
                    initMapRotation()
                }
            }
        }
    }

    private fun zoomToCurrentLocation() {
        try {
            binding.mapView.setViewpoint(Viewpoint(mCurrentLocation, 16.0))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun applyRotation(rotationAngle: Double) {
        try {
            mMatrix.reset()
            mMatrix.postRotate(-rotationAngle.toFloat(), mCompassBitmap.height / 2.toFloat(), mCompassBitmap.width / 2.toFloat())
            binding.compass.imageMatrix = mMatrix
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSingleTap(point: Point?) {
        try {
            viewModel.selectedPointOnMap = point
            if (drawMeasure) {
                if (viewModel.shapeType == Enums.SHAPE.POLYLINE) {
                    if (pointCollection!!.spatialReference == null) {
                        pointCollection = PointCollection(binding.mapView.spatialReference)
                    }
                    pointCollection!!.add(point)
                    drawLine()
                }
            } else if (!queryStatus) {
                if (sheetBehavior?.state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    Utilities.showToast(this, getString(R.string.please_cancel_and_select_again))
                } else {
                    queryStatus = true
                    viewModel.prepareQueryResult()
                    Utilities.showLoadingDialog(this)
                    if (viewModel.onlineData) {
                        viewModel.queryOnline(point, binding.mapView.spatialReference)
                    } else {
                        viewModel.queryOffline(point, binding.mapView.spatialReference)
                    }
                }
            } else {
                Utilities.showToast(this, getString(R.string.please_wait))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * ---------------------------------------Draw------------------------------------------------
     */
    private fun handleFabMeasureAction() {
        try {
            startDrawMode()
            startSupportActionMode(object : ActionMode.Callback {
                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                    val inflater = menuInflater
                    inflater.inflate(R.menu.menu_action_add_shape, menu)
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                    try {
                        when (item.itemId) {
                            R.id.item_Done -> {
                                done()
                                mode.finish()
                                return true
                            }
                            R.id.item_undo -> {
                                undo()
                                return true
                            }
                            R.id.item_redo -> {
                                redo()
                                return true
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode) {
                    done()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun redo() {
        try {
            if (pointCollection != null && lastPointStep != null) {
                pointCollection!!.add(lastPointStep)
                drawLine()
                lastPointStep = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun undo() {
        try {
            if (pointCollection!!.size > 0) {

                val lastIndex = pointCollection!!.size - 1
                lastPointStep = pointCollection!![lastIndex]
                pointCollection!!.removeAt(lastIndex)
                if (pointCollection!!.size >= 0) {

                    if (viewModel.shapeType == Enums.SHAPE.POLYLINE) {
                        drawLine()
                    }
                } else graphicsOverlay!!.graphics.clear()
            } else {
                showToast("No Steps")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun done() {
        try {
            endDrawMode()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun drawLine() {
        try {
            pointCollection?.let { points ->
                drawGraphicLayer?.let { graphicsOverlay ->
                    graphicsOverlay.graphics?.clear()

                    // create a new point collection for polyline
                    val polyline = Polyline(points)

                    //define a line symbol
                    val lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.argb(255, 255, 40, 0), 4.0f)
                    val line = Graphic(polyline, lineSymbol)
                    drawGraphicLayer!!.graphics.add(line)
                    for (point in points) {
                        drawGraphicLayer!!.graphics.add(Graphic(point, pictureMarkerSymbol))
                    }
                    if (points.size >= 2) {
                        binding.measureFunctionValueInMeterLbl.text = ""
                        binding.measureFunctionValueInKmLbl.text = ""
                        binding.measureInfo.visibility = View.VISIBLE
                        var distanceInMeter = 0.0
                        for (i in points.indices) {
                            if (i < points.size - 1) {
                                distanceInMeter += viewModel.calculateDistanceBetweenTwoPoints(points[i], points[i + 1], binding.mapView.spatialReference)
                                binding.measureFunctionValueInMeterLbl.text = "${Utilities.round(distanceInMeter, 2)}"
                                binding.measureFunctionValueInKmLbl.text = "${Utilities.round((distanceInMeter / 1000), 2)}"
                            }
                        }
                    } else {
                        binding.measureFunctionValueInMeterLbl.text = ""
                        binding.measureFunctionValueInKmLbl.text = ""
                        binding.measureInfo.visibility = View.GONE
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startDrawMode() {
        try {
            drawMeasure = true
            drawGraphicLayer = GraphicsOverlay()
            binding.mapView.graphicsOverlays.add(drawGraphicLayer)
            binding.measureFunctionValueInKmLbl.text = ""
            binding.measureFunctionValueInMeterLbl.text = ""
            hide(binding.fabMeasure)
            hide(binding.fabLocation)
            hide(binding.fabFullScreen)
            hide(binding.compass)
            hide(binding.linearLayersInfo)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun endDrawMode() {
        try {
            drawGraphicLayer?.graphics?.clear()
            pointCollection?.clear()
            drawMeasure = false
            binding.measureFunctionValueInKmLbl.text = ""
            binding.measureFunctionValueInMeterLbl.text = ""
            hide(binding.measureInfo)
            show(binding.fabMeasure)
            show(binding.fabLocation)
            show(binding.fabFullScreen)
            show(binding.compass)
            show(binding.linearLayersInfo)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun handleMultiOnlineQueryResult() {
        try {
            show(bottomSheetBinding.mapSelectBottomSheetEditMultiResultContainer)
            sheetBehavior!!.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            bottomSheetBinding.mapSelectBottomSheetMultiResultCloseIv.setOnClickListener { v: View? ->
                sheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                if (selectedResult != null) {
                    selectedResult!!.featureLayer.clearSelection()
                    viewModel.selectedPointOnMap = null
                    viewModel.selectedResult?.featureLayer?.clearSelection()
                }
                hide(bottomSheetBinding.mapSelectBottomSheetEditMultiResultContainer)
            }
            showMultiResultRecyclerView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showMultiResultRecyclerView() {
        try {
            mMultiResultRecAdapter = MultiResultAdapter(viewModel.mOnlineQueryResults, this, this)
            bottomSheetBinding.mapSelectBottomSheetMultiResultRecyclerview.isNestedScrollingEnabled = false
            bottomSheetBinding.mapSelectBottomSheetMultiResultRecyclerview.layoutManager = LinearLayoutManager(this)
            bottomSheetBinding.mapSelectBottomSheetMultiResultRecyclerview.adapter = mMultiResultRecAdapter
            ViewCompat.setNestedScrollingEnabled(bottomSheetBinding.mapSelectBottomSheetMultiResultRecyclerview, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onItemSelected(selectedItem: OnlineQueryResult?, position: Int) {
        selectedItem?.let { item ->
            selectedResult?.featureLayer?.clearSelection()
            selectedResult = item
            mMultiResultRecAdapter.updateSelectedItem(item)
            if (viewModel.onlineData) {
                item.featureLayer.selectFeature(item.feature)
            } else {
                item.featureLayer.selectFeature(item.featureOffline)
            }
        }
    }

    override fun onEditItemSelected(onlineQueryResult: OnlineQueryResult?) {
        onlineQueryResult?.let { item ->
            selectedResult?.featureLayer?.clearSelection()
            selectedResult = item
            viewModel.selectedResult = item
            //hide bottomSheet
            hide(bottomSheetBinding.mapSelectBottomSheetEditMultiResultContainer)
            item.featureLayer.clearSelection()
            sheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN

            viewModel.prepareSelectedFeature()
            viewModel.fillFeatureList()
            //showEditFragment
            showUpdateFragment(NewUpdateFragment.newInstance())

        }
    }

    private fun showUpdateFragment(fragment: Fragment) {
        try {
            val actionBar = supportActionBar
            actionBar?.let { bar ->
                bar.setDisplayHomeAsUpEnabled(true)
                bar.setDisplayShowHomeEnabled(true)
                selectedResult?.let {
                    it.featureLayer.name?.let { name ->
                        bar.title = getString(R.string.update) + " $name"
                    }
                }
            }

            exitFullScreenMode()
            selectedResult!!.featureLayer.clearSelection()
            hide(binding.mapView)
            show(binding.fragment)
            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
                    .replace(R.id.fragment, fragment)
                    .addToBackStack(null)
                    .commit()
            isFragmentShown = true
//            hideActivityViews()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun hideFragment() {
        try {
            supportFragmentManager.findFragmentById(R.id.fragment)?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
            binding.fragment.visibility = View.INVISIBLE
            show(binding.mapView)
            isFragmentShown = false
            val actionBar: ActionBar? = supportActionBar
            actionBar?.let {
                it.setDisplayHomeAsUpEnabled(false)
                it.setDisplayShowHomeEnabled(false)
                it.title = getString(R.string.home)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun exitFullScreenMode() {
        try {
            isFullScreenMode = false
            show(binding.fabLocation)
            show(binding.tvLatLong)
            binding.fabFullScreen.setImageResource(R.drawable.ic_fullscreen_white_24dp)
            supportActionBar?.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun fullScreenMode() {
        try {
            isFullScreenMode = true
            binding.fabFullScreen.setImageResource(R.drawable.ic_fullscreen_exit_white_24dp)
            hide(binding.fabLocation)
            hide(binding.tvLatLong)
            supportActionBar?.hide()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}