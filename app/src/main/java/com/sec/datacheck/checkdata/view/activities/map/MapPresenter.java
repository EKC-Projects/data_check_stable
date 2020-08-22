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
import com.sec.datacheck.checkdata.model.Enums;
import com.sec.datacheck.checkdata.model.QueryConfig;
import com.sec.datacheck.checkdata.model.models.BookMark;
import com.sec.datacheck.checkdata.model.models.Columns;
import com.sec.datacheck.checkdata.model.models.DataCollectionApplication;
import com.sec.datacheck.checkdata.model.models.OConstants;
import com.sec.datacheck.checkdata.model.models.OnlineQueryResult;
import com.sec.datacheck.checkdata.view.POJO.FieldModel;
import com.sec.datacheck.checkdata.view.utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MapPresenter {

    private static final String ROOT_GEO_DATABASE_PATH = "geodatabase";
    private final String IMAGE_FOLDER_NAME = "SEC_Data_Check";
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
                                mOnlineQueryResult.setFeatureType(Enums.SHAPE.POLYGON);
                            } else if (isPolyline(mFeatureLayer)) {
                                mOnlineQueryResult.setFeatureType(Enums.SHAPE.POLYLINE);
                            } else {
                                mOnlineQueryResult.setFeatureType(Enums.SHAPE.POINT);
                            }
                            Log.i(TAG, "queryCheckDataOnline(): Feature founded with id = " + feature.getAttributes().get(Columns.ObjectID));


                            if (mFeatureLayer.getName().equalsIgnoreCase("SERVICE_POINT")) {
                                queryRelatedOCLMETER(mOnlineQueryResults, mOnlineQueryResult, mFeatureLayer, point);
                            }

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
                    listener.onQueryOnline(mOnlineQueryResults, mFeatureLayer, point);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            listener.onQueryOnline(mOnlineQueryResults, mFeatureLayer, point);
        }
    }

    private void queryRelatedOCLMETER(ArrayList<OnlineQueryResult> mOnlineQueryResults, OnlineQueryResult mOnlineQueryResult, FeatureLayer mFeatureLayer, Point point) {
        try {
            synchronized (""){
                ArcGISFeature servicePoint = mOnlineQueryResult.getFeature();
                servicePoint.loadAsync();
                servicePoint.addDoneLoadingListener(() -> {
                    if (servicePoint != null && servicePoint.getAttributes() != null && servicePoint.getAttributes().get(Columns.SERVICE_POINT.SERVICE_POINT_NO) != null) {
                        String  servicePointNo = servicePoint.getAttributes().get(Columns.SERVICE_POINT.SERVICE_POINT_NO).toString();
                        final ServiceFeatureTable oclMeterTable = new ServiceFeatureTable("http://5.9.13.170:6080/arcgis/rest/services/EKC/NEW_CheckData/FeatureServer/15");
                        oclMeterTable.addDoneLoadingListener(() -> {
                            try {
                                Log.e(TAG, "queryRelatedOCLMETER: table Name = " + oclMeterTable.getTableName() +
                                        "SERVICE POINT NO = " + servicePointNo +
                                        " - fields size = " + oclMeterTable.getFields().size());
                                QueryParameters relatedQueryParameters = QueryConfig.getRelatedQuery(servicePointNo, Columns.OCL_METER.OCL_METER_FOREIGN_KEY,point);
                                //                                    FeatureLayer layer = new FeatureLayer(oclMeterTable);
                                ListenableFuture<FeatureQueryResult> queryResults = oclMeterTable.queryFeaturesAsync(relatedQueryParameters);
                                queryResults.addDoneListener(() -> {
                                    try {
                                        // call get on the future to get the result
                                        FeatureQueryResult mResult = queryResults.get();
                                        // check there are some results
                                        Iterator<Feature> mResultIterator = mResult.iterator();
                                        if (mResultIterator.hasNext()) {
                                            mOnlineQueryResult.setHasRelatedFeatures(true);
                                            ArrayList<OnlineQueryResult> onlineQueryResults = new ArrayList<>();
                                            while (mResultIterator.hasNext()) {
                                                OnlineQueryResult onlineQueryResult = new OnlineQueryResult();
                                                // get the extent of the first feature in the result to zoom to
                                                ArcGISFeature mFeature = (ArcGISFeature) mResultIterator.next();
                                                mFeature.addDoneLoadingListener(() -> {
                                                    onlineQueryResult.setFeature(mFeature);
                                                    onlineQueryResult.setServiceFeatureTable(oclMeterTable);
                                                    onlineQueryResult.setObjectID(mFeature.getAttributes().get(Columns.ObjectID).toString());
                                                    onlineQueryResult.setFeatureType(Enums.SHAPE.POINT);
                                                    onlineQueryResults.add(onlineQueryResult);
                                                    mOnlineQueryResult.setRelatedFeatures(onlineQueryResults);
                                                    Log.e(TAG, "queryRelatedOCLMETER: feature foreignKey = " + mFeature.getAttributes().get(Columns.OCL_METER.OCL_METER_FOREIGN_KEY));
                                                });
                                                mFeature.loadAsync();
                                            }

                                            if (listener != null) {
//                                        listener.onQueryOnline(mOnlineQueryResults, mFeatureLayer, point);
                                            }
                                        } else {
                                            Log.e(TAG, "queryRelatedOCLMETER(): No states found ");
                                            if (listener != null) {
//                                        listener.onQueryOnline(mOnlineQueryResults, mFeatureLayer, point);
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "queryRelatedOCLMETER(): No states found ");
                                        e.printStackTrace();
                                        if (listener != null) {
//                                    listener.onQueryOnline(mOnlineQueryResults, mFeatureLayer, point);
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                Log.e(TAG, "queryRelatedOCLMETER(): No states found ");
                                e.printStackTrace();
                                if (listener != null) {
//                            listener.onQueryOnline(mOnlineQueryResults, mFeatureLayer, point);
                                }
                            }
                        });
                        oclMeterTable.loadAsync();
                    } else {
                        if (listener != null) {
//                    listener.onQueryOnline(mOnlineQueryResults, mFeatureLayer, point);
                        }
                    }
                });
            }

        } catch (Exception e) {
            Log.e(TAG, "queryRelatedOCLMETER(): No states found ");
            e.printStackTrace();
            if (listener != null) {
//                listener.onQueryOnline(mOnlineQueryResults, mFeatureLayer, point);
            }
        }
    }


    private boolean isPolygon(FeatureLayer mFeatureLayer) {
        return mFeatureLayer.equals(mCurrent.LvdbAreaLayer) || mFeatureLayer.equals(mCurrent.SwitchgearAreaLayer);
    }

    private boolean isPolyline(FeatureLayer mFeatureLayer) {
        return mFeatureLayer.equals(mCurrent.MvOhCableLayer) || mFeatureLayer.equals(mCurrent.LvOhCableLayer);
    }

    void queryCheckDataOffline(ArrayList<OnlineQueryResult> mOnlineQueryResults, Point
            point, SpatialReference sp, GeodatabaseFeatureTable mGeodatabaseFeatureTable, FeatureLayer
                                       mFeatureLayer) {
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
                                if (isPolygon(mFeatureLayer)) {
                                    mOnlineQueryResult.setFeatureType(Enums.SHAPE.POLYGON);
                                } else if (isPolyline(mFeatureLayer)) {
                                    mOnlineQueryResult.setFeatureType(Enums.SHAPE.POLYLINE);
                                } else {
                                    mOnlineQueryResult.setFeatureType(Enums.SHAPE.POINT);
                                }

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
                        listener.onQueryOffline(mOnlineQueryResults, mFeatureLayer, point);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onQueryOffline(mOnlineQueryResults, mFeatureLayer, point);
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

    public void updateFeatureOnline(OnlineQueryResult result, String code, String
            deviceNo, String typeCode) {
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

    public void updateFeatureOffline(OnlineQueryResult result, String code, String
            deviceNo, String typeCode) {
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

    void downloadAndSaveDatabase(String downloadGeoDatabase, String
            localDatabaseTitle, Envelope extent) {
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

    void addLocalLayers(final MapView mapView, ArcGISMap map, final int databaseNumber, String
            dbTitle) {

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
        result.add(getField(fields, mCurrent.getString(R.string.site_visit), 3));
        Log.i(TAG, "sortFields: fields size = " + fields.size());
        for (FieldModel field : fields) {

            Log.i(TAG, "sortFields: field title = " + field.getTitle());

            if (field.getType() == 2 &&
                    !isObjectID(field, mCurrent)
                    && isValidField(field.getTitle())) {

                result.add(field);

                if (getField(fields, field.getTitle().toLowerCase(), 1) != null) {

                    result.add(getField(fields, field.getTitle().toLowerCase(), 1));
                    Log.i(TAG, "sortFields: valid field check = " + getField(fields, field.getTitle().toLowerCase(), 1).getTitle());

                }

                Log.i(TAG, "sortFields: valid field = " + field.getTitle());
            }
        }

        for (FieldModel field : fields) {
            if (field.getType() == 3 && !isSiteVisit(field, mCurrent)) {
                result.add(field);
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

    private boolean isObjectID(FieldModel field, MapActivity mCurrent) {
        return field.getTitle().toLowerCase().equals(mCurrent.getString(R.string.objectid).toLowerCase());
    }

    private boolean isSiteVisit(FieldModel field, MapActivity mCurrent) {
        return field.getTitle().toLowerCase().equals(mCurrent.getString(R.string.site_visit).toLowerCase());
    }
    /**-----------------------------------Updates-------------------------------------------------**/

    /**
     * --------------------------------------Online Updates -------------------------------------
     **/

    public void updateOnline(OnlineQueryResult
                                     result, ArrayList<FieldModel> fieldModels, String notes) {
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

    /**
     * --------------------------------update offline mode----------------------------------------
     */

    public void updateOffline(OnlineQueryResult
                                      result, ArrayList<FieldModel> fieldModels, String note) {
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
}
