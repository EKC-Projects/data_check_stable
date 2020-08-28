package com.sec.datacheck.checkdata.model.models;

import android.graphics.drawable.Drawable;

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.sec.datacheck.checkdata.model.Enums;

import java.util.ArrayList;

public class OnlineQueryResult {

    private FeatureLayer featureLayer;

    private ServiceFeatureTable serviceFeatureTable;

    private ArcGISFeature feature;

    private GeodatabaseFeatureTable geodatabaseFeatureTable;

    private Feature featureOffline;

    private String objectID;

    private Enums.SHAPE featureType;

    private boolean hasRelatedFeatures;

    private Drawable drawable;

    private ArrayList<OnlineQueryResult> relatedFeatures;

    private Enums.LayerType layerType;

    public OnlineQueryResult() {
    }

    public ServiceFeatureTable getServiceFeatureTable() {
        return serviceFeatureTable;
    }

    public void setServiceFeatureTable(ServiceFeatureTable serviceFeatureTable) {
        this.serviceFeatureTable = serviceFeatureTable;
    }

    public FeatureLayer getFeatureLayer() {
        return featureLayer;
    }

    public void setFeatureLayer(FeatureLayer featureLayer) {
        this.featureLayer = featureLayer;
    }

    public ArcGISFeature getFeature() {
        return feature;
    }

    public void setFeature(ArcGISFeature feature) {
        this.feature = feature;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public GeodatabaseFeatureTable getGeodatabaseFeatureTable() {
        return geodatabaseFeatureTable;
    }

    public void setGeodatabaseFeatureTable(GeodatabaseFeatureTable geodatabaseFeatureTable) {
        this.geodatabaseFeatureTable = geodatabaseFeatureTable;
    }

    public Feature getFeatureOffline() {
        return featureOffline;
    }

    public void setFeatureOffline(Feature featureOffline) {
        this.featureOffline = featureOffline;
    }

    public Enums.SHAPE getFeatureType() {
        return featureType;
    }

    public void setFeatureType(Enums.SHAPE featureType) {
        this.featureType = featureType;
    }

    public boolean isHasRelatedFeatures() {
        return hasRelatedFeatures;
    }

    public void setHasRelatedFeatures(boolean hasRelatedFeatures) {
        this.hasRelatedFeatures = hasRelatedFeatures;
    }

    public ArrayList<OnlineQueryResult> getRelatedFeatures() {
        return relatedFeatures;
    }

    public void setRelatedFeatures(ArrayList<OnlineQueryResult> relatedFeatures) {
        this.relatedFeatures = relatedFeatures;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public Enums.LayerType getLayerType() {
        return layerType;
    }

    public void setLayerType(Enums.LayerType layerType) {
        this.layerType = layerType;
    }
}
