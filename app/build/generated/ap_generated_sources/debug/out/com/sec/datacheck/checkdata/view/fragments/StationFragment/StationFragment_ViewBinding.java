// Generated code from Butter Knife. Do not modify!
package com.sec.datacheck.checkdata.view.fragments.StationFragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.sec.datacheck.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class StationFragment_ViewBinding implements Unbinder {
  private StationFragment target;

  @UiThread
  public StationFragment_ViewBinding(StationFragment target, View source) {
    this.target = target;

    target.X_Y_CoordinatesSpinner = Utils.findRequiredViewAsType(source, R.id.station_frag_x_y_coordinates_4_points_sp, "field 'X_Y_CoordinatesSpinner'", Spinner.class);
    target.gridStationSpinner = Utils.findRequiredViewAsType(source, R.id.station_frag_Grid_Station_sp, "field 'gridStationSpinner'", Spinner.class);
    target.gridStationNameSpinner = Utils.findRequiredViewAsType(source, R.id.station_frag_grid_station_name_type_sp, "field 'gridStationNameSpinner'", Spinner.class);
    target.voltageLevelSpinner = Utils.findRequiredViewAsType(source, R.id.station_frag_Voltage_Level__132_33__132_13_8_sp, "field 'voltageLevelSpinner'", Spinner.class);
    target.notes = Utils.findRequiredViewAsType(source, R.id.station_frag_notes_et, "field 'notes'", EditText.class);
    target.saveBtn = Utils.findRequiredViewAsType(source, R.id.station_frag_save_btn, "field 'saveBtn'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    StationFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.X_Y_CoordinatesSpinner = null;
    target.gridStationSpinner = null;
    target.gridStationNameSpinner = null;
    target.voltageLevelSpinner = null;
    target.notes = null;
    target.saveBtn = null;
  }
}
