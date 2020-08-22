package com.sec.datacheck.checkdata.view.callbacks.mapCallbacks;

import com.esri.arcgisruntime.geometry.Point;

public interface SingleTapListener {

    void onSingleTap(Point point);
    void applyRotation(double rotationAngle);
}
