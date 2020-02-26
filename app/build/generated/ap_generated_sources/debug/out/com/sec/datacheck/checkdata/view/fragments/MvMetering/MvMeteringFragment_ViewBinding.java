// Generated code from Butter Knife. Do not modify!
package com.sec.datacheck.checkdata.view.fragments.MvMetering;

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

public class MvMeteringFragment_ViewBinding implements Unbinder {
  private MvMeteringFragment target;

  @UiThread
  public MvMeteringFragment_ViewBinding(MvMeteringFragment target, View source) {
    this.target = target;

    target.TypeOfTheEquipment = Utils.findRequiredViewAsType(source, R.id.mv_metering_frag_Type_of_the_equipment_1_points_sp, "field 'TypeOfTheEquipment'", Spinner.class);
    target.equipment = Utils.findRequiredViewAsType(source, R.id.mv_metering_frag_equipment_sp, "field 'equipment'", Spinner.class);
    target.manufactureOfEquipment = Utils.findRequiredViewAsType(source, R.id.mv_metering_frag_manufacture_of_equipment_sp, "field 'manufactureOfEquipment'", Spinner.class);
    target.notes = Utils.findRequiredViewAsType(source, R.id.mv_metering_frag_notes_et, "field 'notes'", EditText.class);
    target.saveBtn = Utils.findRequiredViewAsType(source, R.id.mv_metering_frag_save_btn, "field 'saveBtn'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MvMeteringFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.TypeOfTheEquipment = null;
    target.equipment = null;
    target.manufactureOfEquipment = null;
    target.notes = null;
    target.saveBtn = null;
  }
}
