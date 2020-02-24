// Generated code from Butter Knife. Do not modify!
package com.sec.datacheck.checkdata.view.fragments.FuseCutOutFragment;

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

public class FuseCutOutFragment_ViewBinding implements Unbinder {
  private FuseCutOutFragment target;

  @UiThread
  public FuseCutOutFragment_ViewBinding(FuseCutOutFragment target, View source) {
    this.target = target;

    target.X_Y_CoordinatesSpinner_1_pointsSpinner = Utils.findRequiredViewAsType(source, R.id.fuse_cut_out_x_y_frag_coordinates_1_points_sp, "field 'X_Y_CoordinatesSpinner_1_pointsSpinner'", Spinner.class);
    target.FuseCutOutNoSpinner = Utils.findRequiredViewAsType(source, R.id.fuse_cut_out_frag_Fuse_Cut_Out_No_sp, "field 'FuseCutOutNoSpinner'", Spinner.class);
    target.rationAmpSpinner = Utils.findRequiredViewAsType(source, R.id.fuse_cut_out_frag_Ratio_Amp_sp, "field 'rationAmpSpinner'", Spinner.class);
    target.typeSpinner = Utils.findRequiredViewAsType(source, R.id.fuse_cut_out_frag_Type_sp, "field 'typeSpinner'", Spinner.class);
    target.electricityStatusOpenCloseSpinner = Utils.findRequiredViewAsType(source, R.id.fuse_cut_out_frag_Electricity_Status__Open_Close_sp, "field 'electricityStatusOpenCloseSpinner'", Spinner.class);
    target.voltageLevelSpinner = Utils.findRequiredViewAsType(source, R.id.fuse_cut_out_frag_Voltage_sp, "field 'voltageLevelSpinner'", Spinner.class);
    target.notes = Utils.findRequiredViewAsType(source, R.id.fuse_cut_out_frag_notes_et, "field 'notes'", EditText.class);
    target.saveBtn = Utils.findRequiredViewAsType(source, R.id.fuse_cut_out_frag_save_btn, "field 'saveBtn'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    FuseCutOutFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.X_Y_CoordinatesSpinner_1_pointsSpinner = null;
    target.FuseCutOutNoSpinner = null;
    target.rationAmpSpinner = null;
    target.typeSpinner = null;
    target.electricityStatusOpenCloseSpinner = null;
    target.voltageLevelSpinner = null;
    target.notes = null;
    target.saveBtn = null;
  }
}
