package com.sec.datacheck.checkdata.view.activities.map;


import com.sec.datacheck.checkdata.model.models.OnlineQueryResult;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;

import java.util.ArrayList;

public interface MapPresenterListener {

    void onQueryOnline(ArrayList<OnlineQueryResult> results, FeatureLayer featureLayer, Point point);

    void hideFragmentFromActivity();

    void onDownloadGeoDatabaseSuccess(boolean status, String folderPath, String geoDatabasePath);

    void onQueryOffline(ArrayList<OnlineQueryResult> results, FeatureLayer featureLayer, Point point);

    void onSyncSuccess(boolean status);

    void onShowOfflineViews(boolean status);

    void onDeleteFeature(boolean status);

    void onUpdateFeature(boolean status, Throwable t);

}
