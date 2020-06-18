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
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.LayerList;
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
import com.sec.datacheck.checkdata.view.POJO.AutoReCloserModel;
import com.sec.datacheck.checkdata.view.POJO.FieldModel;
import com.sec.datacheck.checkdata.view.POJO.FuseCutOutModel;
import com.sec.datacheck.checkdata.view.POJO.LVDistributionPanelModel;
import com.sec.datacheck.checkdata.view.POJO.LinkBoxModel;
import com.sec.datacheck.checkdata.view.POJO.MeterModel;
import com.sec.datacheck.checkdata.view.POJO.MvMeteringModel;
import com.sec.datacheck.checkdata.view.POJO.OHLinesModel;
import com.sec.datacheck.checkdata.view.POJO.PoleModel;
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
    private final String IMAGE_FOLDER_NAME = "SEC_Check_Data";
    private final String TAG = "MapPresenter";
    public static final String POINT = "Point";
    public static final String POLYLINE = "PolyLine";
    public static final String POLYGON = "Polygon";
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

                            if (isPolygon(mFeatureLayer)) {
                                mOnlineQueryResult.setFeatureType(POLYGON);
                            } else if (isPolyline(mFeatureLayer)) {
                                mOnlineQueryResult.setFeatureType(POLYLINE);
                            } else {
                                mOnlineQueryResult.setFeatureType(POINT);
                            }
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isPolygon(FeatureLayer mFeatureLayer) {
        return mFeatureLayer.equals(mCurrent.LvdbAreaLayer) || mFeatureLayer.equals(mCurrent.SwitchgearAreaLayer);
    }

    private boolean isPolyline(FeatureLayer mFeatureLayer) {
        return mFeatureLayer.equals(mCurrent.MvOhCableLayer) || mFeatureLayer.equals(mCurrent.LvOhCableLayer);
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

    void queryCheckDataOffline(ArrayList<OnlineQueryResult> mOnlineQueryResults, Point point, SpatialReference sp, GeodatabaseFeatureTable mGeodatabaseFeatureTable, FeatureLayer mFeatureLayer) {
        try {
            Log.i(TAG, "queryCheckDataOffline(): is Called ");


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
                                Log.i(TAG, "queryCheckDataOffline(): Feature founded with id = " + feature.getAttributes().get(Columns.ObjectID));

                                mOnlineQueryResults.add(mOnlineQueryResult);
                            }
                            if (listener != null) {
                                listener.onQueryOffline(mOnlineQueryResults, mFeatureLayer, point);
                            }
                        } else {
                            Log.e(TAG, "queryCheckDataOffline(): No states found ");
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
                        ServiceFeatureTable serviceFeatureTable = result.getServiceFeatureTable();

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
                        ServiceFeatureTable serviceFeatureTable = result.getServiceFeatureTable();

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
            final GeodatabaseSyncTask geodatabaseSyncTask = new GeodatabaseSyncTask(mCurrent.getString(R.string.gcs_feature_server));
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
                featureLayer.loadAsync();
                featureLayer.addDoneLoadingListener(() -> {
                    if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
                        Log.i(TAG, "loadTables: Feature Layer failed to load!");
//                        mapView.setViewpointAsync(new Viewpoint(featureLayer.getFullExtent()));
                    } else {
//                        Utilities.showToast(mCurrent, "Feature Layer failed to load!");
                        Log.e(TAG, "Feature Layer failed to load!");
                    }
                });

//                if (OConstants.LAYER_DISTRIBUTION_BOX.contains(geodatabaseFeatureTables.getTableName())) {
//                    mCurrent.FCL_DistributionBoxTableOffline = geodatabaseFeatureTables;
//                    mCurrent.FCL_DistributionBoxLayer = featureLayer;
//                } else if (OConstants.LAYER_POLES.contains(geodatabaseFeatureTables.getTableName())) {
//                    mCurrent.FCL_POLESTableOffline = geodatabaseFeatureTables;
//                    mCurrent.FCL_POLES_Layer = featureLayer;
//                } else if (OConstants.LAYER_RMU.contains(geodatabaseFeatureTables.getTableName())) {
//                    mCurrent.FCL_RMUTableOffline = geodatabaseFeatureTables;
//                    mCurrent.FCL_RMU_Layer = featureLayer;
//                } else if (OConstants.LAYER_SUB_STATION.contains(geodatabaseFeatureTables.getTableName())) {
//                    mCurrent.FCL_SubstationTableOffline = geodatabaseFeatureTables;
//                    mCurrent.FCL_Substation_Layer = featureLayer;
//                } else if (OConstants.LAYER_OCL_METER.contains(geodatabaseFeatureTables.getTableName())) {
//                    mCurrent.OCL_METERTableOffline = geodatabaseFeatureTables;
//                    mCurrent.OCL_METER_Layer = featureLayer;
//                } else if (OConstants.LAYER_SERVICE_POINT.contains(geodatabaseFeatureTables.getTableName())) {
//                    mCurrent.ServicePointTableOffline = geodatabaseFeatureTables;
//                    mCurrent.ServicePoint_Layer = featureLayer;
//                }

                if (OConstants.LAYER_RING_MAIN_UNIT.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.RingMainUnitOfflineTable = geodatabaseFeatureTables;
                    mCurrent.RingMainUnitLayer = featureLayer;

                } else if (OConstants.LAYER_DYNAMIC_PROTECTIVE_DEVICE.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.DynamicProtectiveDeviceOfflineTable = geodatabaseFeatureTables;
                    mCurrent.DynamicProtectiveDeviceLayer = featureLayer;
                } else if (OConstants.LAYER_FUSE.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.FuseOfflineTable = geodatabaseFeatureTables;
                    mCurrent.FuseLayer = featureLayer;
                } else if (OConstants.LAYER_Station.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.stationOfflineTable = geodatabaseFeatureTables;
                    mCurrent.stationLayer = featureLayer;
                } else if (OConstants.LAYER_Substation.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.substationOfflineTable = geodatabaseFeatureTables;
                    mCurrent.substationLayer = featureLayer;
                } else if (OConstants.LAYER_DISTRIBUTION_BOX.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.FCL_DistributionBoxOfflineTable = geodatabaseFeatureTables;
                    mCurrent.FCL_DistributionBoxLayer = featureLayer;
                } else if (OConstants.LAYER_SERVICE_POINT.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.ServicePointOfflineTable = geodatabaseFeatureTables;
                    mCurrent.ServicePointLayer = featureLayer;
                } else if (OConstants.LAYER_POLE.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.FCL_POLESOfflineTable = geodatabaseFeatureTables;
                    mCurrent.FCL_POLES_Layer = featureLayer;
                } else if (OConstants.LAYER_LV_OH_CABLE.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.LvOhCableOfflineTable = geodatabaseFeatureTables;
                    mCurrent.LvOhCableLayer = featureLayer;
                } else if (OConstants.LAYER_MV_OH_CABLE.matches(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.MvOhCableOfflineTable = geodatabaseFeatureTables;
                    mCurrent.MvOhCableLayer = featureLayer;
                } else if (OConstants.LAYER_SWITCH.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.SwitchOfflineTable = geodatabaseFeatureTables;
                    mCurrent.SwitchLayer = featureLayer;
                } else if (OConstants.LAYER_LVDB_AREA.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.LvdbAreaOfflineTable = geodatabaseFeatureTables;
                    mCurrent.LvdbAreaLayer = featureLayer;
                } else if (OConstants.LAYER_SWITCHGEAR_AREA.contains(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.switchgearAreaOfflineTable = geodatabaseFeatureTables;
                    mCurrent.SwitchgearAreaLayer = featureLayer;
                } else if (OConstants.LAYER_Voltage_regulator.matches(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.VoltageRegulatorOfflineTable = geodatabaseFeatureTables;
                    mCurrent.VoltageRegulatorLayer = featureLayer;
                } else if (OConstants.LAYER_Transformer.matches(geodatabaseFeatureTables.getTableName())) {
                    mCurrent.TransFormersOfflineTable = geodatabaseFeatureTables;
                    mCurrent.TransFormersLayer = featureLayer;
                }
                mCurrent.onlineData = false;
            }

            sortLayers(map);
            listener.onShowOfflineViews(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sortLayers(ArcGISMap map) {
        try {
            LayerList layerList = map.getOperationalLayers();
            List<Layer> layers = new ArrayList<>();
            layers.add(mCurrent.SwitchgearAreaLayer);
            layers.add(mCurrent.LvdbAreaLayer);
            layers.add(mCurrent.MvOhCableLayer);
            layers.add(mCurrent.LvOhCableLayer);

            for (Layer layer : layerList) {
                if (!layer.getName().toLowerCase().equals(mCurrent.SwitchgearAreaLayer.getName().toLowerCase()) &&
                        !layer.getName().toLowerCase().equals(mCurrent.LvdbAreaLayer.getName().toLowerCase()) &&
                        !layer.getName().toLowerCase().equals(mCurrent.MvOhCableLayer.getName().toLowerCase()) &&
                        !layer.getName().toLowerCase().equals(mCurrent.LvOhCableLayer.getName().toLowerCase())) {

                    Log.i(TAG, "sortLayers: layer to add = " + layer.getName());
                    layers.add(layer);
                }
            }
            Log.i(TAG, "sortLayers: layers size " + layers.size());

            map.getOperationalLayers().clear();
            for (Layer layer : layers) {
                Log.i(TAG, "sortLayers: layer name = " + layer.getName());
                map.getOperationalLayers().add(layer);
            }

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

                                        Log.i(TAG, "addLocalLayers(): gdbFeatureTable is " + gdbFeatureTable.getTableName());

                                        if (OConstants.LAYER_Substation.matches(gdbFeatureTable.getTableName())) {
                                            mCurrent.substationLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.substationOfflineTable = ((GeodatabaseFeatureTable) mCurrent.substationLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.substationLayer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.substationOfflineTable.getTableName());

                                        } else if (OConstants.LAYER_Station.matches(gdbFeatureTable.getTableName())) {
                                            mCurrent.stationLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.stationOfflineTable = ((GeodatabaseFeatureTable) mCurrent.stationLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.stationLayer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.stationOfflineTable.getTableName());

                                        } else if (OConstants.LAYER_DISTRIBUTION_BOX.matches(gdbFeatureTable.getTableName())) {

                                            mCurrent.FCL_DistributionBoxLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.FCL_DistributionBoxOfflineTable = ((GeodatabaseFeatureTable) mCurrent.FCL_DistributionBoxLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.FCL_DistributionBoxLayer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.FCL_DistributionBoxOfflineTable.getTableName());

                                        } else if (OConstants.LAYER_DYNAMIC_PROTECTIVE_DEVICE.matches(gdbFeatureTable.getTableName())) {

                                            mCurrent.DynamicProtectiveDeviceLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.DynamicProtectiveDeviceOfflineTable = ((GeodatabaseFeatureTable) mCurrent.DynamicProtectiveDeviceLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.DynamicProtectiveDeviceLayer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.DynamicProtectiveDeviceOfflineTable.getTableName());

                                        } else if (OConstants.LAYER_FUSE.matches(gdbFeatureTable.getTableName())) {

                                            mCurrent.FuseLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.FuseOfflineTable = ((GeodatabaseFeatureTable) mCurrent.FuseLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.FuseLayer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.FuseOfflineTable.getTableName());

                                        } else if (OConstants.LAYER_LV_OH_CABLE.matches(gdbFeatureTable.getTableName())) {

                                            mCurrent.LvOhCableLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.LvOhCableOfflineTable = ((GeodatabaseFeatureTable) mCurrent.LvOhCableLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.LvOhCableLayer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.LvOhCableOfflineTable.getTableName());
                                        } else if (OConstants.LAYER_MV_OH_CABLE.matches(gdbFeatureTable.getTableName())) {
                                            mCurrent.MvOhCableLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.MvOhCableOfflineTable = ((GeodatabaseFeatureTable) mCurrent.MvOhCableLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.MvOhCableLayer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.MvOhCableOfflineTable.getTableName());
                                        } else if (OConstants.LAYER_SWITCH.matches(gdbFeatureTable.getTableName())) {

                                            mCurrent.SwitchLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.SwitchOfflineTable = ((GeodatabaseFeatureTable) mCurrent.SwitchLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.SwitchLayer);
                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.SwitchOfflineTable.getTableName());
                                        } else if (OConstants.LAYER_Transformer.matches(gdbFeatureTable.getTableName())) {
                                            mCurrent.TransFormersLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.TransFormersOfflineTable = ((GeodatabaseFeatureTable) mCurrent.TransFormersLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.TransFormersLayer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.TransFormersOfflineTable.getTableName());
                                        } else if (OConstants.LAYER_Voltage_regulator.matches(gdbFeatureTable.getTableName())) {

                                            mCurrent.VoltageRegulatorLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.VoltageRegulatorOfflineTable = ((GeodatabaseFeatureTable) mCurrent.VoltageRegulatorLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.VoltageRegulatorLayer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.VoltageRegulatorOfflineTable.getTableName());
                                        } else if (OConstants.LAYER_RING_MAIN_UNIT.matches(gdbFeatureTable.getTableName())) {
                                            mCurrent.RingMainUnitLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.RingMainUnitOfflineTable = ((GeodatabaseFeatureTable) mCurrent.RingMainUnitLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.RingMainUnitLayer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.RingMainUnitOfflineTable.getTableName());
                                        } else if (OConstants.LAYER_POLE.matches(gdbFeatureTable.getTableName())) {
                                            mCurrent.FCL_POLES_Layer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.FCL_POLESOfflineTable = ((GeodatabaseFeatureTable) mCurrent.FCL_POLES_Layer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.FCL_POLES_Layer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.FCL_POLESOfflineTable.getTableName());
                                        } else if (OConstants.LAYER_SERVICE_POINT.matches(gdbFeatureTable.getTableName())) {
                                            mCurrent.ServicePointLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.ServicePointOfflineTable = ((GeodatabaseFeatureTable) mCurrent.ServicePointLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.ServicePointLayer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.ServicePointOfflineTable.getTableName());
                                        } else if (OConstants.LAYER_LVDB_AREA.matches(gdbFeatureTable.getTableName())) {
                                            mCurrent.LvdbAreaLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.LvdbAreaOfflineTable = ((GeodatabaseFeatureTable) mCurrent.LvdbAreaLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.LvdbAreaLayer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.LvdbAreaOfflineTable.getTableName());
                                        } else if (OConstants.LAYER_SWITCHGEAR_AREA.matches(gdbFeatureTable.getTableName())) {
                                            mCurrent.SwitchgearAreaLayer = new FeatureLayer(gdbFeatureTable);
                                            mCurrent.switchgearAreaOfflineTable = ((GeodatabaseFeatureTable) mCurrent.SwitchgearAreaLayer.getFeatureTable());
                                            map.getOperationalLayers().add(mCurrent.SwitchgearAreaLayer);

                                            Log.i(TAG, "addLocalLayers(): LayerName is " + mCurrent.switchgearAreaOfflineTable.getTableName());
                                        }
                                        mapView.setMap(map);
                                    }
                                    sortLayers(map);

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

    private void setLayerAndTable(FeatureLayer layer, GeodatabaseFeatureTable table, GeodatabaseFeatureTable gdbFeatureTable, ArcGISMap map) {
        layer = new FeatureLayer(gdbFeatureTable);
        table = ((GeodatabaseFeatureTable) layer.getFeatureTable());
        map.getOperationalLayers().add(layer);
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
            GeodatabaseSyncTask mGeodatabaseSyncTask = new GeodatabaseSyncTask(mCurrent.getString(R.string.gcs_feature_server));
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
//                                listener.onSyncSuccess(true);
                                syncData(dbTitle);
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

    private FieldModel getField(ArrayList<FieldModel> data, String name, int type) {
        for (FieldModel fieldModel : data) {
            if (fieldModel.getTitle().toLowerCase().startsWith(name.toLowerCase()) && fieldModel.getType() == type) {
                return fieldModel;
            }
        }
        return null;
    }

    public ArrayList<FieldModel> sortFields(ArrayList<FieldModel> fields) {

        ArrayList<FieldModel> result = new ArrayList<>();

        result.add(getField(fields, mCurrent.getString(R.string.objectid), 2));
        Log.i(TAG, "sortFields: fields size = " + fields.size());
        for (FieldModel field : fields) {

            Log.i(TAG, "sortFields: field title = " + field.getTitle());

            if (field.getType() == 2 && !field.getTitle().toLowerCase().equals(mCurrent.getString(R.string.objectid).toLowerCase())
                    && isValidField(field.getTitle())) {

                result.add(field);

                if (getField(fields, field.getTitle().toLowerCase(), 1) != null) {

                    result.add(getField(fields, field.getTitle().toLowerCase(), 1));
                    Log.i(TAG, "sortFields: valid field check = " + getField(fields, field.getTitle().toLowerCase(), 1).getTitle());

                }

                Log.i(TAG, "sortFields:       valid field = " + field.getTitle());
            }
        }
        Log.i(TAG, "sortFields: result size = " + result.size());
        return result;
    }

    private boolean isValidField(String name) {
        try {
            switch (name) {
                case "GlobalID":
                    return false;
                case "Note":
                    return false;
                case "created_user":
                    return false;
                case "created_date":
                    return false;
                case "last_edited_user":
                    return false;
                case "last_edited_date":
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**-----------------------------------Updates-------------------------------------------------**/

    /**
     * --------------------------------------Online Updates -------------------------------------
     **/

    public void updateOnline(OnlineQueryResult result, ArrayList<FieldModel> fieldModels, String notes) {
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
                    for (FieldModel field : fieldModels) {
                        feature.getAttributes().put(field.getTitle(), field.getSelectedDomainIndex());
                    }
                    feature.getAttributes().put(Columns.Notes, notes);

                    result.getServiceFeatureTable().updateFeatureAsync(feature).get();

                    if (result.getServiceFeatureTable() instanceof ServiceFeatureTable) {
                        ServiceFeatureTable serviceFeatureTable = result.getServiceFeatureTable();

                        // can call getUpdatedFeaturesCountAsync to verify number of updates to be applied before calling applyEditsAsync

                        final List<FeatureEditResult> featureEditResults = serviceFeatureTable.applyEditsAsync().get();
                        featureLayer.clearSelection();
                        listener.onUpdateFeature(true, null);
                        listener.hideFragmentFromActivity();

                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                    featureLayer.clearSelection();
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
                        ServiceFeatureTable serviceFeatureTable = result.getServiceFeatureTable();

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
                        ServiceFeatureTable serviceFeatureTable = result.getServiceFeatureTable();

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

    public void updateAutoReCloserOnline(OnlineQueryResult result, AutoReCloserModel autoReCloserModel) {
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
                        ServiceFeatureTable serviceFeatureTable = result.getServiceFeatureTable();

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

    public void updateFuseCutOutOnline(OnlineQueryResult result, FuseCutOutModel fuseCutOutModel) {
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
                    feature.getAttributes().put(Columns.FuseCutOut.X_Y_Coordinates_1_points, fuseCutOutModel.getX_Y_Coordinates_1_points());
                    feature.getAttributes().put(Columns.FuseCutOut.Fuse_Cut_Out_No, fuseCutOutModel.getFuse_Cut_Out_No());
                    feature.getAttributes().put(Columns.FuseCutOut.Ratio_Amp, fuseCutOutModel.getRatio_Amp());
                    feature.getAttributes().put(Columns.FuseCutOut.Type, fuseCutOutModel.getType());
                    feature.getAttributes().put(Columns.FuseCutOut.Electricity_Status__Open_Close, fuseCutOutModel.getElectricity_Status__Open_Close());
                    feature.getAttributes().put(Columns.FuseCutOut.Voltage, fuseCutOutModel.getVoltage());
                    feature.getAttributes().put(Columns.FuseCutOut.Notes, fuseCutOutModel.getNotes());

                    result.getServiceFeatureTable().updateFeatureAsync(feature).get();

                    if (result.getServiceFeatureTable() instanceof ServiceFeatureTable) {
                        ServiceFeatureTable serviceFeatureTable = result.getServiceFeatureTable();

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

    public void updateLinkBoxOnline(OnlineQueryResult result, LinkBoxModel linkBoxModel) {
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
                    feature.getAttributes().put(Columns.LinkBox.X_Y_Coordinates_1_points, linkBoxModel.getX_Y_Coordinates_1_points());
                    feature.getAttributes().put(Columns.LinkBox.Type, linkBoxModel.getType());
                    feature.getAttributes().put(Columns.LinkBox.Link_Box, linkBoxModel.getLink_Box());
                    feature.getAttributes().put(Columns.LinkBox.Total_no__of_Link_Box_in_the_CK, linkBoxModel.getTotal_no__of_Link_Box_in_the_CK());
                    feature.getAttributes().put(Columns.LinkBox.link_box_distribution_Panel, linkBoxModel.getLink_box_distribution_Panel());
                    feature.getAttributes().put(Columns.LinkBox.Notes, linkBoxModel.getNotes());

                    result.getServiceFeatureTable().updateFeatureAsync(feature).get();

                    if (result.getServiceFeatureTable() instanceof ServiceFeatureTable) {
                        ServiceFeatureTable serviceFeatureTable = result.getServiceFeatureTable();

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

    public void updateMvMeteringOnline(OnlineQueryResult result, MvMeteringModel mvMeteringModel) {
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
                    feature.getAttributes().put(Columns.MV_Metering.Equipment, mvMeteringModel.getEquipment());
                    feature.getAttributes().put(Columns.MV_Metering.Type_of_the_equipment, mvMeteringModel.getType_of_the_equipment());
                    feature.getAttributes().put(Columns.MV_Metering.Manufacture_of_equipment, mvMeteringModel.getManufacture_of_equipment());
                    feature.getAttributes().put(Columns.MV_Metering.Notes, mvMeteringModel.getNotes());

                    result.getServiceFeatureTable().updateFeatureAsync(feature).get();

                    if (result.getServiceFeatureTable() instanceof ServiceFeatureTable) {
                        ServiceFeatureTable serviceFeatureTable = result.getServiceFeatureTable();

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

    public void updateOHLineOnline(OnlineQueryResult result, OHLinesModel ohLinesModel) {
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
                    feature.getAttributes().put(Columns.OH_Lines.Electricity_Status, ohLinesModel.getElectricityStatus());
                    feature.getAttributes().put(Columns.OH_Lines.No_of_lines, ohLinesModel.getNoOfLines());
                    feature.getAttributes().put(Columns.OH_Lines.Size, ohLinesModel.getSize());
                    feature.getAttributes().put(Columns.OH_Lines.Voltage, ohLinesModel.getVoltage());
                    feature.getAttributes().put(Columns.OH_Lines.Martial_Type, ohLinesModel.getMartialType());
                    feature.getAttributes().put(Columns.OH_Lines.Notes, ohLinesModel.getNotes());

                    result.getServiceFeatureTable().updateFeatureAsync(feature).get();

                    if (result.getServiceFeatureTable() instanceof ServiceFeatureTable) {
                        ServiceFeatureTable serviceFeatureTable = result.getServiceFeatureTable();

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

    public void updatePoleOnline(OnlineQueryResult result, PoleModel poleModel) {
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
                    feature.getAttributes().put(Columns.Pole.Location_Type__Section_Middle_E, poleModel.getLocationTypeSectionMiddleE());
                    feature.getAttributes().put(Columns.Pole.Martial_Type__Wooden_Steel, poleModel.getMartialTypeWoodenSteel());
                    feature.getAttributes().put(Columns.Pole.Pole_height, poleModel.getPoleHeight());
                    feature.getAttributes().put(Columns.Pole.Pole_No, poleModel.getPoleNo());
                    feature.getAttributes().put(Columns.Pole.Soil_Type__Rock_Normal_City, poleModel.getSoilTypeRockNormalCity());
                    feature.getAttributes().put(Columns.Pole.X_Y_Coordinates_1_points, poleModel.getxYCoordinates1Points());
                    feature.getAttributes().put(Columns.Pole.Notes, poleModel.getNotes());

                    result.getServiceFeatureTable().updateFeatureAsync(feature).get();

                    if (result.getServiceFeatureTable() instanceof ServiceFeatureTable) {
                        ServiceFeatureTable serviceFeatureTable = result.getServiceFeatureTable();

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

    public void updateMeterOnline(OnlineQueryResult result, MeterModel meterModel) {
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
                    feature.getAttributes().put(Columns.Meter.Backer__Size, meterModel.getBacker_Size());
                    feature.getAttributes().put(Columns.Meter.C_T_Ratio, meterModel.getC_T_Ratio());
                    feature.getAttributes().put(Columns.Meter.Customer_Type, meterModel.getCustomer_Type());
                    feature.getAttributes().put(Columns.Meter.Manufacture, meterModel.getManufacture());
                    feature.getAttributes().put(Columns.Meter.Meter_Box_Type__single_double_q, meterModel.getMeter_Box_TypeSingleDoubleQ());
                    feature.getAttributes().put(Columns.Meter.X_Y_Coordinates_1_points, meterModel.getX_Y_Coordinates_1_points());
                    feature.getAttributes().put(Columns.Meter.Meter_Work_Type__Normal___C_T__, meterModel.getMeter_Work_Type__NormalCT());
                    feature.getAttributes().put(Columns.Meter.Meter_Type__Sort_Digital_Mechan, meterModel.getMeter_Type__Sort_Digital_Mechan());
                    feature.getAttributes().put(Columns.Meter.Notes, meterModel.getNotes());
                    feature.getAttributes().put(Columns.Meter.Serial_No, meterModel.getSerial_No());
                    feature.getAttributes().put(Columns.Meter.Smart, meterModel.getSmart());
                    feature.getAttributes().put(Columns.Meter.Voltage_Type__MV_LV, meterModel.getVoltage_Type__MV_LV());
                    feature.getAttributes().put(Columns.Meter.Subscription_No, meterModel.getSubscription_No());
                    feature.getAttributes().put(Columns.Meter.X_Y_Coordinates_1_points, meterModel.getX_Y_Coordinates_1_points());
                    feature.getAttributes().put(Columns.Meter.Substation_Feeder_No, meterModel.getSubstation_Feeder_No());

                    result.getServiceFeatureTable().updateFeatureAsync(feature).get();

                    if (result.getServiceFeatureTable() instanceof ServiceFeatureTable) {
                        ServiceFeatureTable serviceFeatureTable = result.getServiceFeatureTable();

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

    public void updateLVDistributionPanelOnline(OnlineQueryResult result, LVDistributionPanelModel lvDistributionPanelModel) {
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
                    feature.getAttributes().put(Columns.LVDistributionPanel.Type_of_the_LV_panel, lvDistributionPanelModel.getType_of_the_LV_panel());
                    feature.getAttributes().put(Columns.LVDistributionPanel.Total_no_of_feeders, lvDistributionPanelModel.getTotal_no_of_feeders());
                    feature.getAttributes().put(Columns.LVDistributionPanel.Total_no_of_used_feeders, lvDistributionPanelModel.getTotal_no_of_used_feeders());
                    feature.getAttributes().put(Columns.LVDistributionPanel.Total_no__of_Spare_feeders, lvDistributionPanelModel.getTotal_no__of_Spare_feeders());
                    feature.getAttributes().put(Columns.LVDistributionPanel.Main_cables_type, lvDistributionPanelModel.getMain_cables_type());
                    feature.getAttributes().put(Columns.LVDistributionPanel.Number_of_outgoing_cables, lvDistributionPanelModel.getNumber_of_outgoing_cables());
                    feature.getAttributes().put(Columns.LVDistributionPanel.Current_Rating, lvDistributionPanelModel.getCurrent_Rating());
                    feature.getAttributes().put(Columns.LVDistributionPanel.Voltage_of_equipment, lvDistributionPanelModel.getVoltage_of_equipment());
                    feature.getAttributes().put(Columns.LVDistributionPanel.Notes, lvDistributionPanelModel.getNotes());
                    feature.getAttributes().put(Columns.LVDistributionPanel.Manufacture_of_equipment, lvDistributionPanelModel.getManufacture_of_equipment());
                    feature.getAttributes().put(Columns.LVDistributionPanel.Feeders_Panel_distribution, lvDistributionPanelModel.getFeeders_Panel_distribution());


                    result.getServiceFeatureTable().updateFeatureAsync(feature).get();

                    if (result.getServiceFeatureTable() instanceof ServiceFeatureTable) {
                        ServiceFeatureTable serviceFeatureTable = result.getServiceFeatureTable();

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

    /**
     * --------------------------------update offline mode----------------------------------------
     */


    public void updateOffline(OnlineQueryResult result, ArrayList<FieldModel> fieldModels, String note) {
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
                for (FieldModel field : fieldModels) {
                    feature.getAttributes().put(field.getTitle(), field.getSelectedDomainIndex());
                }

                feature.getAttributes().put(Columns.Notes, note);

                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable calling update Feature Async");
                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable name = " + result.getGeodatabaseFeatureTable().getTableName());
                result.getGeodatabaseFeatureTable().updateFeatureAsync(feature).addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "updateFeatureOffline(): Feature Updated");
                        featureLayer.clearSelection();
                        listener.onUpdateFeature(true, null);
                        listener.hideFragmentFromActivity();

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                featureLayer.clearSelection();
                listener.onUpdateFeature(false, e);
                listener.hideFragmentFromActivity();
            }
        } catch (Exception e) {
            listener.onUpdateFeature(false, e);
            e.printStackTrace();
            listener.hideFragmentFromActivity();
        }
    }

    public void updateSubStationOffline(OnlineQueryResult result, SubstationModel substationModel) {
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


                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable calling update Feature Async");
                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable name = " + result.getGeodatabaseFeatureTable().getTableName());
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

    public void updateStationOffline(OnlineQueryResult result, StationModel stationModel) {
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
                feature.getAttributes().put(Columns.STATION.X_Y_Coordinates_4_points, stationModel.getX_Y_Coordinates_4_points());
                feature.getAttributes().put(Columns.STATION.Grid_Station, stationModel.getGrid_Station());
                feature.getAttributes().put(Columns.STATION.Grid_Station_Name, stationModel.getGrid_Station_Name());
                feature.getAttributes().put(Columns.STATION.Voltage_Level__132_33__132_13_8, stationModel.getVoltage_Level__132_33__132_13_8());
                feature.getAttributes().put(Columns.STATION.Notes, stationModel.getNotes());


                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable calling update Feature Async");
                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable name = " + result.getGeodatabaseFeatureTable().getTableName());
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

    public void updateAutoReCloserOffline(OnlineQueryResult result, AutoReCloserModel autoReCloserModel) {
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
                feature.getAttributes().put(Columns.AutoReCloser.X_Y_Coordinates_1_points, autoReCloserModel.getX_Y_Coordinates_1_points());
                feature.getAttributes().put(Columns.AutoReCloser.Auto_Recloser_No, autoReCloserModel.getAuto_Recloser_No());
                feature.getAttributes().put(Columns.AutoReCloser.Ratio_Amp, autoReCloserModel.getRatio_Amp());
                feature.getAttributes().put(Columns.AutoReCloser.Type_Inside__S_S__Feeder, autoReCloserModel.getType_Inside__S_S__Feeder());
                feature.getAttributes().put(Columns.AutoReCloser.Electricity_Status, autoReCloserModel.getElectricity_Status());
                feature.getAttributes().put(Columns.AutoReCloser.Voltage, autoReCloserModel.getVoltage());
                feature.getAttributes().put(Columns.AutoReCloser.Notes, autoReCloserModel.getNotes());


                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable calling update Feature Async");
                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable name = " + result.getGeodatabaseFeatureTable().getTableName());
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

    public void updateFuseCutOutOffline(OnlineQueryResult result, FuseCutOutModel fuseCutOutModel) {
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
                feature.getAttributes().put(Columns.FuseCutOut.X_Y_Coordinates_1_points, fuseCutOutModel.getX_Y_Coordinates_1_points());
                feature.getAttributes().put(Columns.FuseCutOut.Fuse_Cut_Out_No, fuseCutOutModel.getFuse_Cut_Out_No());
                feature.getAttributes().put(Columns.FuseCutOut.Ratio_Amp, fuseCutOutModel.getRatio_Amp());
                feature.getAttributes().put(Columns.FuseCutOut.Type, fuseCutOutModel.getType());
                feature.getAttributes().put(Columns.FuseCutOut.Electricity_Status__Open_Close, fuseCutOutModel.getElectricity_Status__Open_Close());
                feature.getAttributes().put(Columns.FuseCutOut.Voltage, fuseCutOutModel.getVoltage());
                feature.getAttributes().put(Columns.FuseCutOut.Notes, fuseCutOutModel.getNotes());


                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable calling update Feature Async");
                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable name = " + result.getGeodatabaseFeatureTable().getTableName());
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

    public void updateLinkBoxOffline(OnlineQueryResult result, LinkBoxModel linkBoxModel) {
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
                feature.getAttributes().put(Columns.LinkBox.X_Y_Coordinates_1_points, linkBoxModel.getX_Y_Coordinates_1_points());
                feature.getAttributes().put(Columns.LinkBox.Type, linkBoxModel.getType());
                feature.getAttributes().put(Columns.LinkBox.Link_Box, linkBoxModel.getLink_Box());
                feature.getAttributes().put(Columns.LinkBox.Total_no__of_Link_Box_in_the_CK, linkBoxModel.getTotal_no__of_Link_Box_in_the_CK());
                feature.getAttributes().put(Columns.LinkBox.link_box_distribution_Panel, linkBoxModel.getLink_box_distribution_Panel());
                feature.getAttributes().put(Columns.LinkBox.Notes, linkBoxModel.getNotes());


                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable calling update Feature Async");
                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable name = " + result.getGeodatabaseFeatureTable().getTableName());
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

    public void updateMvMeteringOffline(OnlineQueryResult result, MvMeteringModel mvMeteringModel) {
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
                feature.getAttributes().put(Columns.MV_Metering.Equipment, mvMeteringModel.getEquipment());
                feature.getAttributes().put(Columns.MV_Metering.Type_of_the_equipment, mvMeteringModel.getType_of_the_equipment());
                feature.getAttributes().put(Columns.MV_Metering.Manufacture_of_equipment, mvMeteringModel.getManufacture_of_equipment());
                feature.getAttributes().put(Columns.MV_Metering.Notes, mvMeteringModel.getNotes());


                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable calling update Feature Async");
                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable name = " + result.getGeodatabaseFeatureTable().getTableName());
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

    public void updateOHLineOffline(OnlineQueryResult result, OHLinesModel ohLinesModel) {
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
                feature.getAttributes().put(Columns.OH_Lines.Electricity_Status, ohLinesModel.getElectricityStatus());
                feature.getAttributes().put(Columns.OH_Lines.No_of_lines, ohLinesModel.getNoOfLines());
                feature.getAttributes().put(Columns.OH_Lines.Size, ohLinesModel.getSize());
                feature.getAttributes().put(Columns.OH_Lines.Voltage, ohLinesModel.getVoltage());
                feature.getAttributes().put(Columns.OH_Lines.Martial_Type, ohLinesModel.getMartialType());
                feature.getAttributes().put(Columns.OH_Lines.Notes, ohLinesModel.getNotes());


                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable calling update Feature Async");
                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable name = " + result.getGeodatabaseFeatureTable().getTableName());
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

    public void updatePoleOffline(OnlineQueryResult result, PoleModel poleModel) {
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
                feature.getAttributes().put(Columns.Pole.Location_Type__Section_Middle_E, poleModel.getLocationTypeSectionMiddleE());
                feature.getAttributes().put(Columns.Pole.Martial_Type__Wooden_Steel, poleModel.getMartialTypeWoodenSteel());
                feature.getAttributes().put(Columns.Pole.Pole_height, poleModel.getPoleHeight());
                feature.getAttributes().put(Columns.Pole.Pole_No, poleModel.getPoleNo());
                feature.getAttributes().put(Columns.Pole.Soil_Type__Rock_Normal_City, poleModel.getSoilTypeRockNormalCity());
                feature.getAttributes().put(Columns.Pole.X_Y_Coordinates_1_points, poleModel.getxYCoordinates1Points());
                feature.getAttributes().put(Columns.Pole.Notes, poleModel.getNotes());


                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable calling update Feature Async");
                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable name = " + result.getGeodatabaseFeatureTable().getTableName());
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

    public void updateMeterOffline(OnlineQueryResult result, MeterModel meterModel) {
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
                feature.getAttributes().put(Columns.Meter.Backer__Size, meterModel.getBacker_Size());
                feature.getAttributes().put(Columns.Meter.C_T_Ratio, meterModel.getC_T_Ratio());
                feature.getAttributes().put(Columns.Meter.Customer_Type, meterModel.getCustomer_Type());
                feature.getAttributes().put(Columns.Meter.Manufacture, meterModel.getManufacture());
                feature.getAttributes().put(Columns.Meter.Meter_Box_Type__single_double_q, meterModel.getMeter_Box_TypeSingleDoubleQ());
                feature.getAttributes().put(Columns.Meter.X_Y_Coordinates_1_points, meterModel.getX_Y_Coordinates_1_points());
                feature.getAttributes().put(Columns.Meter.Meter_Work_Type__Normal___C_T__, meterModel.getMeter_Work_Type__NormalCT());
                feature.getAttributes().put(Columns.Meter.Meter_Type__Sort_Digital_Mechan, meterModel.getMeter_Type__Sort_Digital_Mechan());
                feature.getAttributes().put(Columns.Meter.Notes, meterModel.getNotes());
                feature.getAttributes().put(Columns.Meter.Serial_No, meterModel.getSerial_No());
                feature.getAttributes().put(Columns.Meter.Smart, meterModel.getSmart());
                feature.getAttributes().put(Columns.Meter.Voltage_Type__MV_LV, meterModel.getVoltage_Type__MV_LV());
                feature.getAttributes().put(Columns.Meter.Subscription_No, meterModel.getSubscription_No());
                feature.getAttributes().put(Columns.Meter.X_Y_Coordinates_1_points, meterModel.getX_Y_Coordinates_1_points());
                feature.getAttributes().put(Columns.Meter.Substation_Feeder_No, meterModel.getSubstation_Feeder_No());


                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable calling update Feature Async");
                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable name = " + result.getGeodatabaseFeatureTable().getTableName());
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

    public void updateLVDistributionPanelOffline(OnlineQueryResult result, LVDistributionPanelModel lvDistributionPanelModel) {
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
                feature.getAttributes().put(Columns.LVDistributionPanel.Type_of_the_LV_panel, lvDistributionPanelModel.getType_of_the_LV_panel());
                feature.getAttributes().put(Columns.LVDistributionPanel.Total_no_of_feeders, lvDistributionPanelModel.getTotal_no_of_feeders());
                feature.getAttributes().put(Columns.LVDistributionPanel.Total_no_of_used_feeders, lvDistributionPanelModel.getTotal_no_of_used_feeders());
                feature.getAttributes().put(Columns.LVDistributionPanel.Total_no__of_Spare_feeders, lvDistributionPanelModel.getTotal_no__of_Spare_feeders());
                feature.getAttributes().put(Columns.LVDistributionPanel.Main_cables_type, lvDistributionPanelModel.getMain_cables_type());
                feature.getAttributes().put(Columns.LVDistributionPanel.Number_of_outgoing_cables, lvDistributionPanelModel.getNumber_of_outgoing_cables());
                feature.getAttributes().put(Columns.LVDistributionPanel.Current_Rating, lvDistributionPanelModel.getCurrent_Rating());
                feature.getAttributes().put(Columns.LVDistributionPanel.Voltage_of_equipment, lvDistributionPanelModel.getVoltage_of_equipment());
                feature.getAttributes().put(Columns.LVDistributionPanel.Notes, lvDistributionPanelModel.getNotes());
                feature.getAttributes().put(Columns.LVDistributionPanel.Manufacture_of_equipment, lvDistributionPanelModel.getManufacture_of_equipment());
                feature.getAttributes().put(Columns.LVDistributionPanel.Feeders_Panel_distribution, lvDistributionPanelModel.getFeeders_Panel_distribution());


                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable calling update Feature Async");
                Log.i(TAG, "updateFeatureOffline(): getGeodatabaseFeatureTable name = " + result.getGeodatabaseFeatureTable().getTableName());
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


}
