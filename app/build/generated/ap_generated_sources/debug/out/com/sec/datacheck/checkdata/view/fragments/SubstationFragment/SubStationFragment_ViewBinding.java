// Generated code from Butter Knife. Do not modify!
package com.sec.datacheck.checkdata.view.fragments.SubstationFragment;

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

public class SubStationFragment_ViewBinding implements Unbinder {
  private SubStationFragment target;

  @UiThread
  public SubStationFragment_ViewBinding(SubStationFragment target, View source) {
    this.target = target;

    target.X_Y_CoordinatesSpinner = Utils.findRequiredViewAsType(source, R.id.substation_frag_X_Y_Coordinates_sp, "field 'X_Y_CoordinatesSpinner'", Spinner.class);
    target.substationSpinner = Utils.findRequiredViewAsType(source, R.id.substation_frag_substation_sp, "field 'substationSpinner'", Spinner.class);
    target.substationTypeSpinner = Utils.findRequiredViewAsType(source, R.id.substation_frag_substation_type_sp, "field 'substationTypeSpinner'", Spinner.class);
    target.unitSubstationSerialSpinner = Utils.findRequiredViewAsType(source, R.id.substation_frag_substation_serial_sp, "field 'unitSubstationSerialSpinner'", Spinner.class);
    target.noOfTransformersSpinner = Utils.findRequiredViewAsType(source, R.id.substation_frag_No_of_transformers_sp, "field 'noOfTransformersSpinner'", Spinner.class);
    target.noOfSwitchGearsSpinner = Utils.findRequiredViewAsType(source, R.id.substation_frag_No_of_switchgears_sp, "field 'noOfSwitchGearsSpinner'", Spinner.class);
    target.noOfLVDBSpinner = Utils.findRequiredViewAsType(source, R.id.substation_frag_No_of_LVDB_sp, "field 'noOfLVDBSpinner'", Spinner.class);
    target.substationRoomTypeSpinner = Utils.findRequiredViewAsType(source, R.id.substation_frag_Substation_room_type_sp, "field 'substationRoomTypeSpinner'", Spinner.class);
    target.leftSSSpinner = Utils.findRequiredViewAsType(source, R.id.substation_frag_Left_S_S_sp, "field 'leftSSSpinner'", Spinner.class);
    target.rightSSSpinner = Utils.findRequiredViewAsType(source, R.id.substation_frag_Right_S_S_sp, "field 'rightSSSpinner'", Spinner.class);
    target.voltageOfEquipmentPrimarySSpinner = Utils.findRequiredViewAsType(source, R.id.substation_frag_Voltage_of_equipment__primary_s_sp, "field 'voltageOfEquipmentPrimarySSpinner'", Spinner.class);
    target.totalKVASpinner = Utils.findRequiredViewAsType(source, R.id.substation_frag_Total_KVA_sp, "field 'totalKVASpinner'", Spinner.class);
    target.manufactureOfEquipmentSpinner = Utils.findRequiredViewAsType(source, R.id.substation_frag_Manufacture_of_equipment_sp, "field 'manufactureOfEquipmentSpinner'", Spinner.class);
    target.notes = Utils.findRequiredViewAsType(source, R.id.substation_frag_notes_et, "field 'notes'", EditText.class);
    target.saveBtn = Utils.findRequiredViewAsType(source, R.id.substation_frag_save_btn, "field 'saveBtn'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SubStationFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.X_Y_CoordinatesSpinner = null;
    target.substationSpinner = null;
    target.substationTypeSpinner = null;
    target.unitSubstationSerialSpinner = null;
    target.noOfTransformersSpinner = null;
    target.noOfSwitchGearsSpinner = null;
    target.noOfLVDBSpinner = null;
    target.substationRoomTypeSpinner = null;
    target.leftSSSpinner = null;
    target.rightSSSpinner = null;
    target.voltageOfEquipmentPrimarySSpinner = null;
    target.totalKVASpinner = null;
    target.manufactureOfEquipmentSpinner = null;
    target.notes = null;
    target.saveBtn = null;
  }
}
