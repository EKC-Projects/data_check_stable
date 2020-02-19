// Generated code from Butter Knife. Do not modify!
package com.sec.datacheck.checkdata.view.activities.map;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.sec.datacheck.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MapActivity_ViewBinding implements Unbinder {
  private MapActivity target;

  @UiThread
  public MapActivity_ViewBinding(MapActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MapActivity_ViewBinding(MapActivity target, View source) {
    this.target = target;

    target.mapView = Utils.findRequiredViewAsType(source, R.id.mapView, "field 'mapView'", MapView.class);
    target.rlFragment = Utils.findRequiredViewAsType(source, R.id.rlFragment, "field 'rlFragment'", RelativeLayout.class);
    target.fabGeneral = Utils.findRequiredViewAsType(source, R.id.fab_general, "field 'fabGeneral'", FloatingActionMenu.class);
    target.fabDistributionBox = Utils.findRequiredViewAsType(source, R.id.fab_add_distribution_box, "field 'fabDistributionBox'", FloatingActionButton.class);
    target.fabPoles = Utils.findRequiredViewAsType(source, R.id.fab_add_poles, "field 'fabPoles'", FloatingActionButton.class);
    target.fabRMU = Utils.findRequiredViewAsType(source, R.id.fab_add_rmu, "field 'fabRMU'", FloatingActionButton.class);
    target.fabSubStation = Utils.findRequiredViewAsType(source, R.id.fab_add_sub_station, "field 'fabSubStation'", FloatingActionButton.class);
    target.fabOCLMeter = Utils.findRequiredViewAsType(source, R.id.fab_add_ocl_meter, "field 'fabOCLMeter'", FloatingActionButton.class);
    target.fabServicePoint = Utils.findRequiredViewAsType(source, R.id.fab_add_service_point, "field 'fabServicePoint'", FloatingActionButton.class);
    target.fabLocation = Utils.findRequiredViewAsType(source, R.id.fabLocation, "field 'fabLocation'", com.google.android.material.floatingactionbutton.FloatingActionButton.class);
    target.fabFullScreen = Utils.findRequiredViewAsType(source, R.id.fabFullScreen, "field 'fabFullScreen'", com.google.android.material.floatingactionbutton.FloatingActionButton.class);
    target.mFabMeasureMenu = Utils.findRequiredViewAsType(source, R.id.fab_measure, "field 'mFabMeasureMenu'", FloatingActionMenu.class);
    target.fabMeasureDistance = Utils.findRequiredViewAsType(source, R.id.fab_measure_distance, "field 'fabMeasureDistance'", FloatingActionButton.class);
    target.fabMeasureArea = Utils.findRequiredViewAsType(source, R.id.fab_measure_area, "field 'fabMeasureArea'", FloatingActionButton.class);
    target.mapLegend = Utils.findRequiredViewAsType(source, R.id.linear_layers_info, "field 'mapLegend'", LinearLayout.class);
    target.tvLatLong = Utils.findRequiredViewAsType(source, R.id.tvLatLong, "field 'tvLatLong'", TextView.class);
    target.tvMoreLayerInfo = Utils.findRequiredViewAsType(source, R.id.tv_more_layer_info, "field 'tvMoreLayerInfo'", TextView.class);
    target.mapLegendContainer = Utils.findRequiredViewAsType(source, R.id.linear_layers_details, "field 'mapLegendContainer'", LinearLayout.class);
    target.mCompass = Utils.findRequiredViewAsType(source, R.id.compass, "field 'mCompass'", ImageView.class);
    target.mConstraintLayout = Utils.findRequiredViewAsType(source, R.id.map_layout, "field 'mConstraintLayout'", ConstraintLayout.class);
    target.mBottomSheet = Utils.findRequiredViewAsType(source, R.id.bottom_sheet, "field 'mBottomSheet'", LinearLayout.class);
    target.mUpdateBtn = Utils.findRequiredViewAsType(source, R.id.update_btn, "field 'mUpdateBtn'", Button.class);
    target.mCancelBtn = Utils.findRequiredViewAsType(source, R.id.cancel_btn, "field 'mCancelBtn'", Button.class);
    target.mCreatePointBtn = Utils.findRequiredViewAsType(source, R.id.add_btn, "field 'mCreatePointBtn'", Button.class);
    target.mCancelCreatePointBtn = Utils.findRequiredViewAsType(source, R.id.cancel_button, "field 'mCancelCreatePointBtn'", Button.class);
    target.mEditPointLayout = Utils.findRequiredViewAsType(source, R.id.edit_point_bottom_sheet_container, "field 'mEditPointLayout'", LinearLayout.class);
    target.mAddPointLayout = Utils.findRequiredViewAsType(source, R.id.add_point_bottom_sheet_container, "field 'mAddPointLayout'", LinearLayout.class);
    target.mMeasureLayerInfo = Utils.findRequiredViewAsType(source, R.id.measure_info, "field 'mMeasureLayerInfo'", LinearLayout.class);
    target.mMeasureInMeterLbl = Utils.findRequiredViewAsType(source, R.id.measure_function_in_meter_lbl, "field 'mMeasureInMeterLbl'", TextView.class);
    target.mMeasureValueInMeterLbl = Utils.findRequiredViewAsType(source, R.id.measure_function_value_in_meter_lbl, "field 'mMeasureValueInMeterLbl'", TextView.class);
    target.mMeasureInKMLbl = Utils.findRequiredViewAsType(source, R.id.measure_function_in_km_lbl, "field 'mMeasureInKMLbl'", TextView.class);
    target.mMeasureValueInKMLbl = Utils.findRequiredViewAsType(source, R.id.measure_function_value_in_km_lbl, "field 'mMeasureValueInKMLbl'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MapActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mapView = null;
    target.rlFragment = null;
    target.fabGeneral = null;
    target.fabDistributionBox = null;
    target.fabPoles = null;
    target.fabRMU = null;
    target.fabSubStation = null;
    target.fabOCLMeter = null;
    target.fabServicePoint = null;
    target.fabLocation = null;
    target.fabFullScreen = null;
    target.mFabMeasureMenu = null;
    target.fabMeasureDistance = null;
    target.fabMeasureArea = null;
    target.mapLegend = null;
    target.tvLatLong = null;
    target.tvMoreLayerInfo = null;
    target.mapLegendContainer = null;
    target.mCompass = null;
    target.mConstraintLayout = null;
    target.mBottomSheet = null;
    target.mUpdateBtn = null;
    target.mCancelBtn = null;
    target.mCreatePointBtn = null;
    target.mCancelCreatePointBtn = null;
    target.mEditPointLayout = null;
    target.mAddPointLayout = null;
    target.mMeasureLayerInfo = null;
    target.mMeasureInMeterLbl = null;
    target.mMeasureValueInMeterLbl = null;
    target.mMeasureInKMLbl = null;
    target.mMeasureValueInKMLbl = null;
  }
}
