package com.sec.datacheck.checkdata.model;

import com.esri.arcgisruntime.arcgisservices.RelationshipInfo;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.RelatedQueryParameters;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;

public class QueryConfig {

    public static QueryParameters getQuery(Point point, SpatialReference sp, boolean returnGeometry) {

        QueryParameters query = new QueryParameters();

        Envelope geometry = new Envelope(point, 5.0, 5.0);
        // make search case insensitive
        query.setWhereClause("1=1");
        // call select features

//        query.setSpatialRelationship(QueryParameters.SpatialRelationship.INTERSECTS);
        query.setReturnGeometry(returnGeometry);
        query.setOutSpatialReference(sp);
        query.setGeometry(geometry);

        return query;
    }

    public static QueryParameters getRelatedQuery(String primaryKey, String foreignKeyColumnName,Point point) {

        QueryParameters query = new QueryParameters();
        // make search case insensitive
        query.setWhereClause(foreignKeyColumnName.concat("=").concat("'"+primaryKey+"'"));
        query.setReturnGeometry(false);
//        query.setOutSpatialReference(sp);
//        query.setGeometry(point);
        return query;
    }
}
