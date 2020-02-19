package com.sec.datacheck.checkdata.view.activities.map;

import android.app.ProgressDialog;
import android.os.Environment;
import android.util.Log;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geodatabase.GenerateGeodatabaseJob;
import com.esri.arcgisruntime.tasks.geodatabase.GenerateGeodatabaseParameters;
import com.esri.arcgisruntime.tasks.geodatabase.GeodatabaseSyncTask;
import com.esri.arcgisruntime.tasks.geodatabase.SyncGeodatabaseJob;
import com.esri.arcgisruntime.tasks.geodatabase.SyncGeodatabaseParameters;
import com.esri.arcgisruntime.tasks.geodatabase.SyncLayerOption;
import com.sec.datacheck.R;
import com.sec.datacheck.checkdata.model.QueryConfig;
import com.sec.datacheck.checkdata.model.models.BookMark;
import com.sec.datacheck.checkdata.model.models.Columns;
import com.sec.datacheck.checkdata.model.models.DataCollectionApplication;
import com.sec.datacheck.checkdata.model.models.OConstants;
import com.sec.datacheck.checkdata.model.models.OnlineQueryResult;
import com.sec.datacheck.checkdata.view.POJO.AutoReCloser;
import com.sec.datacheck.checkdata.view.POJO.StationModel;
import com.sec.datacheck.checkdata.view.POJO.SubstationModel;
import com.sec.datacheck.checkdata.view.utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MapPresenter {

    private static final String ROOT_GEO_DATABASE_PATH = "geodatabase";
    private final String IMAGE_FOLDER_NAME = "AJC_Collector";
    private final String TAG = "MapPresenter";
    private MapPresenterListener listener;
    private MapActivity mCurrent;
    private ArrayList<OnlineQueryResult> mOnlineQueryResults;

    MapPresenter(MapPresenterListener listener, MapActivity mCurrent) {
        this.listener = listener;
        this.mCurrent = mCurrent;
    }

    void prepareQueryResult() {
        mOnlineQueryResults = new ArrayList<>();
    }

    public ArrayList<OnlineQueryResult> getOnlineQueryResults() {
        return mOnlineQueryResults;
    }

    public void resetOnlineQueryResults() {
        mOnlineQueryResults = null;
    }

    void queryOnline(ArrayList<OnlineQueryResult> mOnlineQueryResults, Point point, SpatialReference sp, ServiceFeatureTable mServiceFeatureTable, FeatureLayer mFeatureLayer) {
        try {
            Log.i(TAG, "queryOnline(): is Called ");


            QueryParameters query = QueryConfig.getQuery(point, sp, true);

            final ListenableFuture<FeatureQueryResult> future = mServiceFeatureTable.queryFeaturesAsync(query);
            // add done loading listener to fire when the selection returns

            future.addDoneListener(() -> {
                try {
                    // call get on the future to get the result
                    FeatureQueryResult result = future.get();
                    // check there are some results
                    Iterator<Feature> resultIterator = result.iterator();
                    if (resultIterator.hasNext()) {
                        while (resultIterator.hasNext()) {
                            // get the extent of the first feature in the result to zoom to

                            ArcGISFeature feature = (ArcGISFeature) resultIterator.next();
                            feature.loadAsync();
                            OnlineQueryResult mOnlineQueryResult = new OnlineQueryResult();
                            mOnlineQueryResult.setFeature(feature);
                            mOnlineQueryResult.setServiceFeatureTable(mServiceFeatureTable);
                            mOnlineQueryResult.setFeatureLayer(mFeatureLayer);
                            mOnlineQueryResult.setObjectID(String.valueOf(feature.getAttributes().get(Columns.ObjectID)));
                            // select the feature
//                        mFeatureLayer.selectFeature(feature);
                            Log.i(TAG, "queryOnline(): Feature founded with id = " + feature.getAttributes().get(Columns.ObjectID));

                            mOnlineQueryResults.add(mOnlineQueryResult);
                        }
                        if (listener != null) {
                            listener.onQueryOnline(mOnlineQueryResults, mFeatureLayer, point);
                        }
                    } else {
                        Log.e(TAG, "queryOnline(): No states found ");
                        listener.onQueryOnline(mOnlineQueryResults, mFeatureLayer, point);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    void queryCheckDataOnline(ArrayList<OnlineQueryResult> mOnlineQueryResults, Point point, SpatialReference sp, ServiceFeatureTable mServiceFeatureTable, FeatureLayer mFeatureLayer) {
        try {
            Log.i(TAG, "queryCheckDataOnline(): is Called ");


            QueryParameters query = QueryConfig.getQuery(point, sp, true);

            final ListenableFuture<FeatureQueryResult> future = mServiceFeatureTable.queryFeaturesAsync(query);
            // add done loading listener to fire when the selection returns

            future.addDoneListener(() -> {
                try {
                    // call get on the future to get the result
                    FeatureQueryResult result = future.get();
                    // check there are some results
                    Iterator<Feature> resultIterator = result.iterator();
                    if (resultIterator.hasNext()) {
                        while (resultIterator.hasNext()) {
                            // get the extent of the first feature in the result to zoom to

                            ArcGISFeature feature = (ArcGISFeature) resultIterator.next();
                            feature.loadAsync();
                            OnlineQueryResult mOnlineQueryResult = new OnlineQueryResult();
                            mOnlineQueryResult.setFeature(feature);
                            mOnlineQueryResult.setServiceFeatureTable(mServiceFeatureTable);
                            mOnlineQueryResult.setFeatureLayer(mFeatureLayer);
                            mOnlineQueryResult.setObjectID(String.valueOf(feature.getAttributes().get(Columns.ObjectID)));
                            // select the feature
//                        mFeatureLayer.selectFeature(feature);
                            Log.i(TAG, "queryCheckDataOnline(): Feature founded with id = " + feature.getAttributes().get(Columns.ObjectID));

                            mOnlineQueryResults.add(mOnlineQueryResult);
                        }
                        if (listener != null) {
                            listener.onQueryOnline(mOnlineQueryResults, mFeatureLayer, point);
                        }
                    } else {
                        Log.e(TAG, "queryCheckDataOnline(): No states found ");
                        listener.onQueryOnline(mOnlineQueryResults, mFeatureLayer, point);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    void queryOffline(ArrayList<OnlineQueryResult> mOnlineQueryResults, Point point, SpatialReference sp, GeodatabaseFeatureTable mGeodatabaseFeatureTable, FeatureLayer mFeatureLayer) {
        try {
            Log.i(TAG, "queryOffline(): is Called ");


            QueryParameters query = QueryConfig.getQuery(point, sp, true);

            final ListenableFuture<FeatureQueryResult> future = mGeodatabaseFeatureTable.queryFeaturesAsync(query);
            future.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        // call get on the future to get the result
                        FeatureQueryResult result = future.get();
                        // check there are some results
                        Iterator<Feature> resultIterator = result.iterator();
                        if (resultIterator.hasNext()) {
                            while (resultIterator.hasNext()) {
                                // get the extent of the first feature in the result to zoom to

                                Feature feature = resultIterator.next();
                                OnlineQueryResult mOnlineQueryResult = new OnlineQueryResult();
                                mOnlineQueryResult.setFeatureOffline(feature);
                                mOnlineQueryResult.setGeodatabaseFeatureTable(mGeodatabaseFeatureTable);
                                mOnlineQueryResult.setFeatureLayer(mFeatureLayer);
                                mOnlineQueryResult.setObjectID(String.valueOf(feature.getAttributes().get(Columns.ObjectID)));
                                // select the feature
//                        mFeatureLayer.selectFeature(feature);
                                Log.i(TAG, "queryOffline(): Feature founded with id = " + feature.getAttributes().get(Columns.ObjectID));

                                mOnlineQueryResults.add(mOnlineQueryResult);
                            }
                            if (listener != null) {
                                listener.onQueryOffline(mOnlineQueryResults, mFeatureLayer, point);
                            }
                        } else {
                            Log.e(TAG, "queryOffline(): No states found ");
                            listener.onQueryOffline(mOnlineQueryResults, mFeatureLayer, point);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFeatureOnline(OnlineQueryResult result, Map<String, String> data) {
        try {

            final FeatureLayer featureLayer = result.getServiceFeatureTable().getFeatureLayer();
            featureLayer.selectFeature(result.getFeature());

            final ListenableFuture<FeatureQueryResult> selected = featureLayer.getSelectedFeaturesAsync();
            FeatureQueryResult features = null;
            try {
                features = selected.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // check there is at least one selected feature
            if (!features.iterator().hasNext()) {
                Log.e(TAG, "No selected features");
            }

            // get the first selected feature and load it
            final ArcGISFeature feature = (ArcGISFeature) features.iterator().next();
            feature.loadAsync();

            feature.addDoneLoadingListener(() -> {
                // now feature is loaded we can update it; change attribute and geometry (here the point geometry is moved North)
                try {

                    for (String key : data.keySet()) {
                        feature.getAttributes().put(key, data.get(key));
                    }

                    result.getServiceFeatureTable().updateFeatureAsync(feature).get();

                    if (result.getServiceFeatureTable() instanceof ServiceFeatureTable) {
                        ServiceFeatureTable serviceFeatureTable = (ServiceFeatureTable) result.getServiceFeatureTable();

                        // can call getUpdatedFeaturesCountAsync to verify number of updates to be applied before calling applyEditsAsync

                        final List<FeatureEditResult> featureEditResults = serviceFeatureTable.applyEditsAsync().get();
//                        listener.hideFragmentFromActivity();
                        listener.onUpdateFeature(true, null);
                    } else {
                        listener.onUpdateFeature(false, null);
                    }
                } catch (Exception e) {
                    listener.onUpdateFeature(false, e);
                }
            });
        } catch (Exception e) {
            listener.onUpdateFeature(false, e);
        }
    }

    public void updateFeatureOnline(OnlineQueryResult result, String code, String deviceNo, String typeCode) {
        try {
            final FeatureLayer featureLayer = result.getServiceFeatureTable().getFeatureLayer();
            featureLayer.selectFeature(result.getFeature());

            final ListenableFuture<FeatureQueryResult> selected = featureLayer.getSelectedFeaturesAsync();
            FeatureQueryResult features = null;
            try {
                features = selected.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // check there is at least one selected feature
            if (!features.iterator().hasNext()) {
                Log.e(TAG, "No selected features");
            }

            // get the first selected feature and load it
            final ArcGISFeature feature = (ArcGISFeature) features.iterator().next();
            feature.loadAsync();

            feature.addDoneLoadingListener(() -> {
                // now feature is loaded we can update it; change attribute and geometry (here the point geometry is moved North)
                try {
                    feature.getAttributes().put(Columns.Code, Integer.parseInt(code));
                    feature.getAttributes().put(Columns.Device_No, deviceNo);
                    feature.getAttributes().put(Columns.Type, typeCode);

                    result.getServiceFeatureTable().updateFeatureAsync(feature).get();

                    if (result.getServiceFeatureTable() instanceof ServiceFeatureTable) {
                        ServiceFeatureTable serviceFeatureTable = (ServiceFeatureTable) result.getServiceFeatureTable();

                        // can call getUpdatedFeaturesCountAsync to verify number of updates to be applied before calling applyEditsAsync

                        final List<FeatureEditResult> featureEditResults = serviceFeatureTable.applyEditsAsync().get();
                        listener.hideFragmentFromActivity();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFeatureOffline(OnlineQueryResult result, String code, String deviceNo, String typeCode) {
        try {
            Log.i(TAG, "updateFeatureOffline(): is called");

            final FeatureLayer featureLayer = result.getGeodatabaseFeatureTable().getFeatureLayer();
            featureLayer.selectFeature(result.getFeatureOffline());

            final ListenableFuture<FeatureQueryResult> selected = featureLayer.getSelectedFeaturesAsync();
            FeatureQueryResult features = null;
            try {
                features = selected.get();
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.dismissLoadingDialog();
            }

            // check there is at least one selected feature
            if (!features.iterator().hasNext()) {
                Log.e(TAG, "No selected features");
            }

            // get the first selected feature and load it
            final Feature feature = features.iterator().next();

            // now feature is loaded we can update it; change attribute and geometry (here the point geometry is moved North)
            try {
                feature.getAttributes().put(Columns.Code, Integer.parseInt(code));
                feature.getAttributes().put(Columns.Device_No, deviceNo);
                feature.getAttributes().put(Columns.Type, typeCode);

                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable calling update Feature Async");
                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable name = " + result.getGeodatabaseFeatureTable().getTableName());
                Log.i(TAG, "updateFeatureOffline(): Feature type = " + feature.getAttributes().get(Columns.Type));
                Log.i(TAG, "updateFeatureOffline(): Feature code = " + feature.getAttributes().get(Columns.Code));

                result.getGeodatabaseFeatureTable().updateFeatureAsync(feature).addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "updateFeatureOffline(): Feature Updated");
                        listener.hideFragmentFromActivity();

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Utilities.dismissLoadingDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utilities.dismissLoadingDialog();
        }
    }

    public void deleteFeatureOnline(OnlineQueryResult result) {
        Log.i(TAG, "deleteFeature(): is called");

        // get selected features from the layer for this ArcGISFeatureTable
        // query feature layer to find element by id
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause(String.format("OBJECTID = %s", result.getObjectID()));

        final FeatureLayer featureLayer = result.getFeatureLayer();
        final ListenableFuture<FeatureQueryResult> selected = featureLayer.getFeatureTable().queryFeaturesAsync(queryParameters);
        FeatureQueryResult features;
        Feature foundFeature = null;
        try {

            try {
                // check result has a feature
                if (selected.get().iterator().hasNext()) {
                    // attempt to get first feature from result as it should be the only feature
                    foundFeature = selected.get().iterator().next();
                    // delete found features
                    Log.i(TAG, "deleteFeature(): feature has been founded");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            features = selected.get();

            if (foundFeature != null) {
                // delete the selected features
                Log.i(TAG, "deleteFeature(): founded feature not null");
                result.getServiceFeatureTable().deleteFeatureAsync(foundFeature).get();

                //if dealing with ServiceFeatureTable, apply edits after making updates; if editing locally, then edits can
                // be synchronized at some point using the SyncGeodatabaseTask.
                if (result.getServiceFeatureTable() instanceof ServiceFeatureTable) {
                    Log.i(TAG, "deleteFeature(): feature table is ServiceFeatureTable");

                    ServiceFeatureTable serviceFeatureTable = result.getServiceFeatureTable();

                    // can call getDeletedFeaturesCountAsync() to verify number of deletes to be applied before calling applyEditsAsync

                    serviceFeatureTable.applyEditsAsync().addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "deleteFeature(): feature deleted");
                            listener.onDeleteFeature(true);
                        }
                    });

                    // if required, can check the edits applied in this operation by using returned FeatureEditResult
                }
            } else {
                Log.i(TAG, "deleteFeature(): founded feature is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onDeleteFeature(false);
        }
    }

    public void deleteFeatureOffline(OnlineQueryResult result) {
        Log.i(TAG, "deleteFeature(): is called");

        // get selected features from the layer for this ArcGISFeatureTable
        // query feature layer to find element by id
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause(String.format("OBJECTID = %s", result.getObjectID()));

        final FeatureLayer featureLayer = result.getFeatureLayer();
        final ListenableFuture<FeatureQueryResult> selected = featureLayer.getFeatureTable().queryFeaturesAsync(queryParameters);
        FeatureQueryResult features;
        Feature foundFeature = null;
        try {

            try {
                // check result has a feature
                if (selected.get().iterator().hasNext()) {
                    // attempt to get first feature from result as it should be the only feature
                    foundFeature = selected.get().iterator().next();
                    // delete found features
                    Log.i(TAG, "deleteFeature(): feature has been founded");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            features = selected.get();

            if (foundFeature != null) {
                // delete the selected features
                Log.i(TAG, "deleteFeature(): founded feature not null");
                result.getGeodatabaseFeatureTable().deleteFeatureAsync(foundFeature).addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            Log.i(TAG, "deleteFeature(): feature deleted");
                            listener.onDeleteFeature(true);
                        }
                    }
                });

            } else {
                Log.i(TAG, "deleteFeature(): founded feature is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onDeleteFeature(false);
        }
    }

    void downloadAndSaveDatabase(String downloadGeoDatabase, String localDatabaseTitle, Envelope extent) {
        try {
            // create a geodatabase sync task
            final GeodatabaseSyncTask geodatabaseSyncTask = new GeodatabaseSyncTask(mCurrent.getString(R.string.gcs_feature_server_test));
            geodatabaseSyncTask.loadAsync();
            geodatabaseSyncTask.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {

                    // create generate geodatabase parameters for the current extent
                    final ListenableFuture<GenerateGeodatabaseParameters> defaultParameters = geodatabaseSyncTask
                            .createDefaultGenerateGeodatabaseParametersAsync(extent);
                    defaultParameters.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // set parameters and don't include attachments
                                GenerateGeodatabaseParameters parameters = defaultParameters.get();
                                parameters.setReturnAttachments(false);

                                // define the local path where the geodatabase will be stored

                                File rootFolder = new File(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_DCIM), IMAGE_FOLDER_NAME);
                                if (!rootFolder.exists())
                                    rootFolder.mkdirs();
                                File file1 = new File(rootFolder.getPath(), "geodatabase");
                                if (!file1.exists()) {
                                    file1.mkdirs();
                                }

                                File databaseFile = new File(file1.getPath(), localDatabaseTitle + ".geodatabase");
                                final String localGeodatabasePath = databaseFile.getPath();

                                // create and start the job
                                final GenerateGeodatabaseJob generateGeodatabaseJob = geodatabaseSyncTask
                                        .generateGeodatabaseAsync(parameters, localGeodatabasePath);
                                generateGeodatabaseJob.start();

                                // update progress
                                generateGeodatabaseJob.addProgressChangedListener(new Runnable() {
                                    @Override
                                    public void run() {
//                                                        progressBar.setProgress(generateGeodatabaseJob.getProgress());
//                                                        mProgressTextView.setText(getString(R.string.progress_fetching));
                                    }
                                });

                                // get geodatabase when done
                                generateGeodatabaseJob.addJobDoneListener(new Runnable() {
                                    @Override
                                    public void run() {
//                                                        mProgressLayout.setVisibility(View.INVISIBLE);
                                        if (generateGeodatabaseJob.getStatus() == Job.Status.SUCCEEDED) {
                                            final Geodatabase geodatabase = generateGeodatabaseJob.getResult();
                                            geodatabase.loadAsync();
                                            geodatabase.addDoneLoadingListener(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
                                                        for (GeodatabaseFeatureTable geodatabaseFeatureTable : geodatabase
                                                                .getGeodatabaseFeatureTables()) {
                                                            geodatabaseFeatureTable.loadAsync();
                                                        }
                                                        int dbNum = DataCollectionApplication.getDatabaseNumber();
                                                        DataCollectionApplication.setLocalDatabaseTitle(localDatabaseTitle, dbNum);
                                                        DataCollectionApplication.incrementDatabaseNumber();

                                                        Log.i(TAG, "Local geodatabase stored at: " + localGeodatabasePath);
                                                        listener.onDownloadGeoDatabaseSuccess(true, file1.getPath(), databaseFile.getPath());

                                                        DataCollectionApplication.addBookMark(mCurrent.mapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY).toJson(), localDatabaseTitle);

                                                        Utilities.dismissLoadingDialog();

                                                    } else {
                                                        Log.e(TAG, "Error loading geodatabase: " + geodatabase.getLoadError().getMessage());
                                                    }
                                                }
                                            });

                                        } else if (generateGeodatabaseJob.getError() != null) {
                                            Log.e(TAG, "Error generating geodatabase: " + generateGeodatabaseJob.getError().getMessage());
                                            generateGeodatabaseJob.getError().printStackTrace();
                                            Utilities.dismissLoadingDialog();
                                        } else {
                                            Log.e(TAG, "Unknown Error generating geodatabase");
                                        }
                                    }
                                });
                            } catch (InterruptedException | ExecutionException e) {
                                Log.e(TAG, "Error generating geodatabase parameters : " + e.getMessage());
                                Utilities.dismissLoadingDialog();
                            }
                        }
                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadMap(String folderPath, String geoDatabasePath, MapView mapView, ArcGISMap map) {
        try {
            // create path to local geodatabase
            String path = geoDatabasePath;

            // create a new geodatabase from local path
            final Geodatabase geodatabase = new Geodatabase(path);

            // load the geodatabase
            geodatabase.loadAsync();

            // create feature layer from geodatabase and add to the map
            geodatabase.addDoneLoadingListener(() -> {
                if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
                    Log.i(TAG, "loadMap():loading tables...");
                    loadTables(geodatabase, mapView, map);
                } else {

                    Utilities.showToast(mCurrent, "Geodatabase failed to load!");

                    Log.e(TAG, "Geodatabase failed to load!");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTables(Geodatabase geodatabase, MapView mapView, ArcGISMap map) {
        try {
            Log.i(TAG, "loadTables(): is called");

            if (geodatabase != null && geodatabase.getGeodatabaseFeatureTables() != null) {
                Log.i(TAG, "loadTables(): tables count = " + geodatabase.getGeodatabaseFeatureTables().size());
            }

            for (GeodatabaseFeatureTable geodatabaseFeatureTables : geodatabase.getGeodatabaseFeatureTables()) {
                Log.i(TAG, "loadTables(): table name = " + geodatabaseFeatureTables.getTableName());
                // access the geodatabase's feature table name

                geodatabaseFeatureTables.loadAsync();
                // create a layer from the geodatabase feature table and add to map
                final FeatureLayer featureLayer = new FeatureLayer(geodatabaseFeatureTables);
                map.getOperationalLayers().add(featureLayer);
                mapView.setMap(map);

                featureLayer.addDoneLoadingListener(() -> {
                    if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
                        // set viewpoint to the feature layer's extent
//                        mapView.setViewpointAsync(new Viewpoint(featureLayer.getFullExtent()));
                    } else {
                        Utilities.showToast(mCurrent, "Feature Layer failed to load!");
                        Log.e(TAG, "Feature Layer failed to load!");
                    }
                });

                if (OConstants.LAYER_DISTRIBUTION_BOX.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.FCL_DistributionBoxTableOffline = geodatabaseFeatureTables;
                    mCurrent.FCL_DistributionBoxLayer = featureLayer;
                } else if (OConstants.LAYER_POLES.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.FCL_POLESTableOffline = geodatabaseFeatureTables;
                    mCurrent.FCL_POLES_Layer = featureLayer;
                } else if (OConstants.LAYER_RMU.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.FCL_RMUTableOffline = geodatabaseFeatureTables;
                    mCurrent.FCL_RMU_Layer = featureLayer;
                } else if (OConstants.LAYER_SUB_STATION.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.FCL_SubstationTableOffline = geodatabaseFeatureTables;
                    mCurrent.FCL_Substation_Layer = featureLayer;
                } else if (OConstants.LAYER_OCL_METER.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.OCL_METERTableOffline = geodatabaseFeatureTables;
                    mCurrent.OCL_METER_Layer = featureLayer;
                } else if (OConstants.LAYER_SERVICE_POINT.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.ServicePointTableOffline = geodatabaseFeatureTables;
                    mCurrent.ServicePoint_Layer = featureLayer;
                }
                mCurrent.onlineData = false;
            }

            listener.onShowOfflineViews(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addLocalLayers(final MapView mapView, ArcGISMap map, final int databaseNumber, String dbTitle) {

        mCurrent.onlineData = false;

        Log.i(TAG, "Removing all the features layers from map");

        map.getOperationalLayers().clear();

        Log.i(TAG, "addLocalLayers(): Add features layers from Local Geo Database");

        Geodatabase geodatabase;

        try {

            String databasePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + File.separator + IMAGE_FOLDER_NAME + File.separator + ROOT_GEO_DATABASE_PATH + File.separator + dbTitle + ".geodatabase";
            Log.i(TAG, "addLocalLayers(): database path = " + databasePath);

            geodatabase = new Geodatabase(databasePath);
            geodatabase.loadAsync();
            geodatabase.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    mCurrent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.i(TAG, "addLocalLayers(): tables count = " + geodatabase.getGeodatabaseFeatureTables().size());
                                if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
                                    Log.i(TAG, "addLocalLayers(): tables count = " + geodatabase.getGeodatabaseFeatureTables().size());
                                    for (GeodatabaseFeatureTable gdbFeatureTable : geodatabase.getGeodatabaseFeatureTables()) {

                                        Log.i(TAG, "addLocalLayers(): gdb Feature Table has geometry");

                                        if (OConstants.LAYER_DISTRIBUTION_BOX.contains(gdbFeatureTable.getTableName())) {
                                            mCurrent.FCL_DistributionBoxLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.FCL_DistributionBoxTableOffline = ((GeodatabaseFeatureTable) mCurrent.FCL_DistributionBoxLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.FCL_DistributionBoxLayer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.FCL_DistributionBoxTableOffline.getTableName());

                                        } else if (OConstants.LAYER_POLES.contains(gdbFeatureTable.getTableName())) {

                                            mCurrent.FCL_POLES_Layer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.FCL_POLESTableOffline = ((GeodatabaseFeatureTable) mCurrent.FCL_POLES_Layer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.FCL_POLES_Layer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.FCL_POLES_ServiceTable.getTableName());

                                        } else if (OConstants.LAYER_RMU.contains(gdbFeatureTable.getTableName())) {
                                            mCurrent.FCL_RMU_Layer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.FCL_RMUTableOffline = ((GeodatabaseFeatureTable) mCurrent.FCL_RMU_Layer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.FCL_RMU_Layer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.FCL_RMUTableOffline.getTableName());

                                        } else if (OConstants.LAYER_SUB_STATION.contains(gdbFeatureTable.getTableName())) {
                                            mCurrent.FCL_Substation_Layer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.FCL_SubstationTableOffline = ((GeodatabaseFeatureTable) mCurrent.FCL_Substation_Layer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.FCL_Substation_Layer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.FCL_SubstationTableOffline.getTableName());

                                        } else if (OConstants.LAYER_OCL_METER.contains(gdbFeatureTable.getTableName())) {
                                            mCurrent.OCL_METER_Layer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.OCL_METERTableOffline = ((GeodatabaseFeatureTable) mCurrent.OCL_METER_Layer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.OCL_METER_Layer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.OCL_METERTableOffline.getTableName());

                                        } else if (OConstants.LAYER_SERVICE_POINT.contains(gdbFeatureTable.getTableName())) {
                                            mCurrent.ServicePoint_Layer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.ServicePointTableOffline = ((GeodatabaseFeatureTable) mCurrent.ServicePoint_Layer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.ServicePoint_Layer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.ServicePointTableOffline.getTableName());

                                        }

                                        mapView.setMap(map);


                                    }

//                                    Envelope mapExtent = null;
//                                    if (mCurrent.FCL_DistributionBoxLayer != null && mCurrent.FCL_DistributionBoxLayer.getFullExtent() != null) {
//                                        mapExtent = mCurrent.FCL_DistributionBoxLayer.getFullExtent().getExtent();
//                                    } else if (mCurrent.FCL_POLESTableOffline != null && mCurrent.FCL_POLESTableOffline.getExtent() != null) {
//                                        mapExtent = mCurrent.FCL_POLESTableOffline.getExtent();
//                                    } else if (mCurrent.FCL_RMUTableOffline != null && mCurrent.FCL_RMUTableOffline.getExtent() != null) {
//                                        mapExtent = mCurrent.FCL_RMUTableOffline.getExtent();
//                                    } else if (mCurrent.FCL_SubstationTableOffline != null && mCurrent.FCL_SubstationTableOffline.getExtent() != null) {
//                                        mapExtent = mCurrent.FCL_SubstationTableOffline.getExtent();
//                                    } else if (mCurrent.OCL_METERTableOffline != null && mCurrent.OCL_METERTableOffline.getExtent() != null) {
//                                        mapExtent = mCurrent.OCL_METERTableOffline.getExtent();
//                                    } else if (mCurrent.ServicePointTableOffline != null && mCurrent.ServicePointTableOffline.getExtent() != null) {
//                                        mapExtent = mCurrent.ServicePointTableOffline.getExtent();
//                                    }
//
//                                    if (mapExtent != null)
//                                        mapView.setViewpoint(new Viewpoint(mapExtent));

                                    zoomToArea(dbTitle);
                                    mCurrent.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {

                                                mCurrent.currentOfflineVersion = databaseNumber;
                                                mCurrent.item_load_previous_offline.setVisible(true);
                                                mCurrent.menuItemGoOfflineMode.setVisible(false);
                                                mCurrent.menuItemGoOnlineMode.setVisible(true);
                                                mCurrent.menuItemOffline.setVisible(false);
                                                mCurrent.menuItemSync.setVisible(true);
                                                mCurrent.menuItemOnline.setVisible(true);
                                                if (isLocalGeoDatabase()) {
                                                    mCurrent.menuItemLoad.setVisible(false);
                                                } else {
                                                    mCurrent.menuItemLoad.setVisible(true);
                                                }

                                                Utilities.dismissLoadingDialog();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                } else {
                                    Utilities.showToast(mCurrent, "Geodatabase failed to load!");
                                    Log.e(TAG, "Geodatabase failed to load!");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    });
                }
            });


        } catch (Exception e) {
            Log.i(TAG, "Error in adding feature layers from Local Geo Database");
            e.printStackTrace();
        }
    }

    private void zoomToArea(String title) {
        try {
            ArrayList<BookMark> bookMarks = DataCollectionApplication.getAllBookMarks();
            for (BookMark bookMark : bookMarks) {
                if (bookMark.getTitle().matches(title)) {
                    Viewpoint viewpoint = Viewpoint.fromJson(bookMark.getJson());
                    mCurrent.mapView.setViewpoint(viewpoint);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isLocalGeoDatabase() {
        ArrayList<String> databaseTitles = DataCollectionApplication.getOfflineDatabasesTitle();
        for (String title : databaseTitles) {
            if (title != null)
                return false;
        }
        DataCollectionApplication.resetDatabaseNumber();
        return true;
    }

    void syncData(String dbTitle) {
        try {
            GeodatabaseSyncTask mGeodatabaseSyncTask = new GeodatabaseSyncTask(mCurrent.getString(R.string.gcs_feature_server_test));
            Geodatabase mGeodatabase;

            String databasePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + File.separator + IMAGE_FOLDER_NAME + File.separator + ROOT_GEO_DATABASE_PATH + File.separator + dbTitle + ".geodatabase";
            Log.i(TAG, "addLocalLayers(): database path = " + databasePath);

            mGeodatabase = new Geodatabase(databasePath);
            mGeodatabase.loadAsync();

            mGeodatabase.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        // create parameters for the sync task
                        SyncGeodatabaseParameters syncGeodatabaseParameters = new SyncGeodatabaseParameters();
                        syncGeodatabaseParameters.setSyncDirection(SyncGeodatabaseParameters.SyncDirection.BIDIRECTIONAL);
                        syncGeodatabaseParameters.setRollbackOnFailure(false);
                        // get the layer ID for each feature table in the geodatabase, then add to the sync job
                        for (GeodatabaseFeatureTable geodatabaseFeatureTable : mGeodatabase.getGeodatabaseFeatureTables()) {
                            long serviceLayerId = geodatabaseFeatureTable.getServiceLayerId();
                            SyncLayerOption syncLayerOption = new SyncLayerOption(serviceLayerId);
                            syncGeodatabaseParameters.getLayerOptions().add(syncLayerOption);
                        }

                        final SyncGeodatabaseJob syncGeodatabaseJob = mGeodatabaseSyncTask.syncGeodatabase(syncGeodatabaseParameters, mGeodatabase);

                        syncGeodatabaseJob.start();

                        createProgressDialog(syncGeodatabaseJob);

                        syncGeodatabaseJob.addJobDoneListener(() -> {
                            if (syncGeodatabaseJob.getStatus() == Job.Status.SUCCEEDED) {
                                listener.onSyncSuccess(true);
//                                mGeodatabaseButton.setVisibility(View.INVISIBLE);
                            } else {
                                Log.e(TAG, "Database did not sync correctly!");
                                listener.onSyncSuccess(true);

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createProgressDialog(Job job) {
        try {
            ProgressDialog syncProgressDialog = new ProgressDialog(mCurrent);
            syncProgressDialog.setTitle("Sync Geodatabase Job");
            syncProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            syncProgressDialog.setCanceledOnTouchOutside(false);
            syncProgressDialog.show();

            job.addProgressChangedListener(() -> syncProgressDialog.setProgress(job.getProgress()));

            job.addJobDoneListener(syncProgressDialog::dismiss);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**-----------------------------------Updates-------------------------------------------------**/

    public void updateSubStationOnline(OnlineQueryResult result, SubstationModel substationModel) {
        try {
            Log.i(TAG, "updateSubStationOnline(): is Called ");
            final FeatureLayer featureLayer = result.getServiceFeatureTable().getFeatureLayer();
            featureLayer.selectFeature(result.getFeature());

            final ListenableFuture<FeatureQueryResult> selected = featureLayer.getSelectedFeaturesAsync();
            FeatureQueryResult features = null;
            try {
                features = selected.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // check there is at least one selected feature
            if (!features.iterator().hasNext()) {
                Log.e(TAG, "No selected features");
            }

            // get the first selected feature and load it
            final ArcGISFeature feature = (ArcGISFeature) features.iterator().next();
            feature.loadAsync();

            feature.addDoneLoadingListener(() -> {
                // now feature is loaded we can update it; change attribute and geometry (here the point geometry is moved North)
                try {
                    feature.getAttributes().put(Columns.SUBSTATION.X_Y_Coordinates_2_points, substationModel.getX_Y_Coordinates_2_points());
                    feature.getAttributes().put(Columns.SUBSTATION.Substation, substationModel.getSubstation());
                    feature.getAttributes().put(Columns.SUBSTATION.Substation_type, substationModel.getSubstation_type());
                    feature.getAttributes().put(Columns.SUBSTATION.Unit_Substation_serial, substationModel.getUnit_Substation_serial());
                    feature.getAttributes().put(Columns.SUBSTATION.No_of_transformers, substationModel.getNo_of_transformers());
                    feature.getAttributes().put(Columns.SUBSTATION.No_of_switchgears, substationModel.getNo_of_switchgears());
                    feature.getAttributes().put(Columns.SUBSTATION.No_of_LVDB, substationModel.getNo_of_LVDB());
                    feature.getAttributes().put(Columns.SUBSTATION.Substation_room_type, substationModel.getSubstation_room_type());
                    feature.getAttributes().put(Columns.SUBSTATION.Left_S_S, substationModel.getLeft_S_S());
                    feature.getAttributes().put(Columns.SUBSTATION.Right_S_S, substationModel.getRight_S_S());
                    feature.getAttributes().put(Columns.SUBSTATION.Voltage_of_equipment__primary_s, substationModel.getVoltage_of_equipment__primary_s());
                    feature.getAttributes().put(Columns.SUBSTATION.Total_KVA, substationModel.getTotal_KVA());
                    feature.getAttributes().put(Columns.SUBSTATION.Manufacture_of_equipment, substationModel.getManufacture_of_equipment());
                    feature.getAttributes().put(Columns.SUBSTATION.Notes, substationModel.getNotes());

                    result.getServiceFeatureTable().updateFeatureAsync(feature).get();

                    if (result.getServiceFeatureTable() instanceof ServiceFeatureTable) {
                        ServiceFeatureTable serviceFeatureTable = (ServiceFeatureTable) result.getServiceFeatureTable();

                        // can call getUpdatedFeaturesCountAsync to verify number of updates to be applied before calling applyEditsAsync

                        final List<FeatureEditResult> featureEditResults = serviceFeatureTable.applyEditsAsync().get();
                        listener.onUpdateFeature(true, null);
                        listener.hideFragmentFromActivity();

                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                    listener.onUpdateFeature(false, e);
                    listener.hideFragmentFromActivity();
                }
            });
        } catch (Exception e) {
//            e.printStackTrace();
            listener.onUpdateFeature(false, e);
            listener.hideFragmentFromActivity();
        }
    }

    public void updateStationOnline(OnlineQueryResult result, StationModel stationModel) {
        try {
            Log.i(TAG, "updateStationOnline(): is Called ");
            final FeatureLayer featureLayer = result.getServiceFeatureTable().getFeatureLayer();
            featureLayer.selectFeature(result.getFeature());

            final ListenableFuture<FeatureQueryResult> selected = featureLayer.getSelectedFeaturesAsync();
            FeatureQueryResult features = null;
            try {
                features = selected.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // check there is at least one selected feature
            if (!features.iterator().hasNext()) {
                Log.e(TAG, "updateStationOnline(): No selected features");
            }

            // get the first selected feature and load it
            final ArcGISFeature feature = (ArcGISFeature) features.iterator().next();
            feature.loadAsync();

            feature.addDoneLoadingListener(() -> {
                // now feature is loaded we can update it; change attribute and geometry (here the point geometry is moved North)
                try {
                    Map<String, Object> attr = feature.getAttributes();
                    for (String key : attr.keySet()) {
                        try {

                            Log.i(TAG, "updateStationOnline(): " + key + " = " + attr.get(key));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    feature.getAttributes().put(Columns.STATION.X_Y_Coordinates_4_points, stationModel.getX_Y_Coordinates_4_points());
                    feature.getAttributes().put(Columns.STATION.Grid_Station, stationModel.getGrid_Station());
                    feature.getAttributes().put(Columns.STATION.Grid_Station_Name, stationModel.getGrid_Station_Name());
                    feature.getAttributes().put(Columns.STATION.Voltage_Level__132_33__132_13_8, stationModel.getVoltage_Level__132_33__132_13_8());
                    feature.getAttributes().put(Columns.STATION.Notes, stationModel.getNotes());

                    result.getServiceFeatureTable().updateFeatureAsync(feature).get();

                    if (result.getServiceFeatureTable() instanceof ServiceFeatureTable) {
                        ServiceFeatureTable serviceFeatureTable = (ServiceFeatureTable) result.getServiceFeatureTable();

                        // can call getUpdatedFeaturesCountAsync to verify number of updates to be applied before calling applyEditsAsync

                        final List<FeatureEditResult> featureEditResults = serviceFeatureTable.applyEditsAsync().get();
                        listener.onUpdateFeature(true, null);
                        listener.hideFragmentFromActivity();

                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                    listener.onUpdateFeature(false, e);
                    listener.hideFragmentFromActivity();
                }
            });
        } catch (Exception e) {
//            e.printStackTrace();
            listener.onUpdateFeature(false, e);
            listener.hideFragmentFromActivity();
        }
    }

    public void updateAutoReCloserOnline(OnlineQueryResult result, AutoReCloser autoReCloserModel) {
        try {
            Log.i(TAG, "updateStationOnline(): is Called ");
            final FeatureLayer featureLayer = result.getServiceFeatureTable().getFeatureLayer();
            featureLayer.selectFeature(result.getFeature());

            final ListenableFuture<FeatureQueryResult> selected = featureLayer.getSelectedFeaturesAsync();
            FeatureQueryResult features = null;
            try {
                features = selected.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // check there is at least one selected feature
            if (!features.iterator().hasNext()) {
                Log.e(TAG, "updateStationOnline(): No selected features");
            }

            // get the first selected feature and load it
            final ArcGISFeature feature = (ArcGISFeature) features.iterator().next();
            feature.loadAsync();

            feature.addDoneLoadingListener(() -> {
                // now feature is loaded we can update it; change attribute and geometry (here the point geometry is moved North)
                try {
                    Map<String, Object> attr = feature.getAttributes();
                    for (String key : attr.keySet()) {
                        try {

                            Log.i(TAG, "updateStationOnline(): " + key + " = " + attr.get(key));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    feature.getAttributes().put(Columns.AutoReCloser.X_Y_Coordinates_1_points, autoReCloserModel.getX_Y_Coordinates_1_points());
                    feature.getAttributes().put(Columns.AutoReCloser.Auto_Recloser_No, autoReCloserModel.getAuto_Recloser_No());
                    feature.getAttributes().put(Columns.AutoReCloser.Ratio_Amp, autoReCloserModel.getRatio_Amp());
                    feature.getAttributes().put(Columns.AutoReCloser.Type_Inside__S_S__Feeder, autoReCloserModel.getType_Inside__S_S__Feeder());
                    feature.getAttributes().put(Columns.AutoReCloser.Electricity_Status, autoReCloserModel.getElectricity_Status());
                    feature.getAttributes().put(Columns.AutoReCloser.Voltage, autoReCloserModel.getVoltage());
                    feature.getAttributes().put(Columns.AutoReCloser.Notes, autoReCloserModel.getNotes());

                    result.getServiceFeatureTable().updateFeatureAsync(feature).get();

                    if (result.getServiceFeatureTable() instanceof ServiceFeatureTable) {
                        ServiceFeatureTable serviceFeatureTable = (ServiceFeatureTable) result.getServiceFeatureTable();

                        // can call getUpdatedFeaturesCountAsync to verify number of updates to be applied before calling applyEditsAsync

                        final List<FeatureEditResult> featureEditResults = serviceFeatureTable.applyEditsAsync().get();
                        listener.onUpdateFeature(true, null);
                        listener.hideFragmentFromActivity();

                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                    listener.onUpdateFeature(false, e);
                    listener.hideFragmentFromActivity();
                }
            });
        } catch (Exception e) {
//            e.printStackTrace();
            listener.onUpdateFeature(false, e);
            listener.hideFragmentFromActivity();
        }
    }

}
