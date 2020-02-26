// Generated code from Butter Knife. Do not modify!
package com.sec.datacheck.checkdata.view.fragments.Meter;

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

public class MeterFragment_ViewBinding implements Unbinder {
  private MeterFragment target;

  @UiThread
  public MeterFragment_ViewBinding(MeterFragment target, View source) {
    this.target = target;

    target.X_Y_CoordinatesSpinner = Utils.findRequiredViewAsType(source, R.id.meter_frag_X_Y_Coordinates_sp, "field 'X_Y_CoordinatesSpinner'", Spinner.class);
    target.serialNo = Utils.findRequiredViewAsType(source, R.id.meter_frag_Serial_No_sp, "field 'serialNo'", Spinner.class);
    target.subscriptionNo = Utils.findRequiredViewAsType(source, R.id.meter_frag_subscription_no_sp, "field 'subscriptionNo'", Spinner.class);
    target.voltageType = Utils.findRequiredViewAsType(source, R.id.meter_frag_voltage_type__mv_lv_sp, "field 'voltageType'", Spinner.class);
    target.meterTypeSortDigitalMechan = Utils.findRequiredViewAsType(source, R.id.meter_frag_meter_type__sort_digital_mechan_sp, "field 'meterTypeSortDigitalMechan'", Spinner.class);
    target.meterWorkTypeNormal = Utils.findRequiredViewAsType(source, R.id.meter_frag_meter_work_type_normal_c_t_sp, "field 'meterWorkTypeNormal'", Spinner.class);
    target.meterBoxTypeSingleDouble_q = Utils.findRequiredViewAsType(source, R.id.meter_frag_meter_box_type__single_double_q_sp, "field 'meterBoxTypeSingleDouble_q'", Spinner.class);
    target.substationNo = Utils.findRequiredViewAsType(source, R.id.meter_frag_substation_no_sp, "field 'substationNo'", Spinner.class);
    target.substationFeederNo = Utils.findRequiredViewAsType(source, R.id.meter_frag_substation_feeder_no_sp, "field 'substationFeederNo'", Spinner.class);
    target.C_T_ratio = Utils.findRequiredViewAsType(source, R.id.meter_frag_c_t_ratio_sp, "field 'C_T_ratio'", Spinner.class);
    target.Manufacture = Utils.findRequiredViewAsType(source, R.id.meter_frag_manufacture_sp, "field 'Manufacture'", Spinner.class);
    target.backerSize = Utils.findRequiredViewAsType(source, R.id.meter_frag_backer_size_sp, "field 'backerSize'", Spinner.class);
    target.Smart = Utils.findRequiredViewAsType(source, R.id.meter_frag_smart_sp, "field 'Smart'", Spinner.class);
    target.customerTypeSp = Utils.findRequiredViewAsType(source, R.id.meter_frag_customer_type_sp, "field 'customerTypeSp'", Spinner.class);
    target.notes = Utils.findRequiredViewAsType(source, R.id.substation_frag_notes_et, "field 'notes'", EditText.class);
    target.saveBtn = Utils.findRequiredViewAsType(source, R.id.substation_frag_save_btn, "field 'saveBtn'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MeterFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.X_Y_CoordinatesSpinner = null;
    target.serialNo = null;
    target.subscriptionNo = null;
    target.voltageType = null;
    target.meterTypeSortDigitalMechan = null;
    target.meterWorkTypeNormal = null;
    target.meterBoxTypeSingleDouble_q = null;
    target.substationNo = null;
    target.substationFeederNo = null;
    target.C_T_ratio = null;
    target.Manufacture = null;
    target.backerSize = null;
    target.Smart = null;
    target.customerTypeSp = null;
    target.notes = null;
    target.saveBtn = null;
  }
}
