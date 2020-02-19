// Generated code from Butter Knife. Do not modify!
package com.sec.datacheck.checkdata.view.fragments.AutoReCloser;

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

public class AutoReCloserFragment_ViewBinding implements Unbinder {
  private AutoReCloserFragment target;

  @UiThread
  public AutoReCloserFragment_ViewBinding(AutoReCloserFragment target, View source) {
    this.target = target;

    target.X_Y_CoordinatesSpinner_1_pointsSpinner = Utils.findRequiredViewAsType(source, R.id.auto_re_closer_frag_x_y_coordinates_1_points_sp, "field 'X_Y_CoordinatesSpinner_1_pointsSpinner'", Spinner.class);
    target.autoReCloserNoSpinner = Utils.findRequiredViewAsType(source, R.id.auto_re_closer_frag_Auto_Recloser_No_sp, "field 'autoReCloserNoSpinner'", Spinner.class);
    target.rationAmpSpinner = Utils.findRequiredViewAsType(source, R.id.auto_re_closer_frag_Ratio_Amp_sp, "field 'rationAmpSpinner'", Spinner.class);
    target.typeInsideSSFeederSpinner = Utils.findRequiredViewAsType(source, R.id.auto_re_closer_frag_Type_Inside__S_S__Feeder_sp, "field 'typeInsideSSFeederSpinner'", Spinner.class);
    target.electricityStatusSpinner = Utils.findRequiredViewAsType(source, R.id.auto_re_closer_frag_Electricity_Status_sp, "field 'electricityStatusSpinner'", Spinner.class);
    target.voltageLevelSpinner = Utils.findRequiredViewAsType(source, R.id.auto_re_closer_frag_Voltage_sp, "field 'voltageLevelSpinner'", Spinner.class);
    target.notes = Utils.findRequiredViewAsType(source, R.id.auto_re_closer_frag_notes_et, "field 'notes'", EditText.class);
    target.saveBtn = Utils.findRequiredViewAsType(source, R.id.auto_re_closer_frag_save_btn, "field 'saveBtn'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AutoReCloserFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.X_Y_CoordinatesSpinner_1_pointsSpinner = null;
    target.autoReCloserNoSpinner = null;
    target.rationAmpSpinner = null;
    target.typeInsideSSFeederSpinner = null;
    target.electricityStatusSpinner = null;
    target.voltageLevelSpinner = null;
    target.notes = null;
    target.saveBtn = null;
  }
}
