// Generated code from Butter Knife. Do not modify!
package com.sec.datacheck.checkdata.view.fragments.Pole;

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

public class PoleFragment_ViewBinding implements Unbinder {
  private PoleFragment target;

  @UiThread
  public PoleFragment_ViewBinding(PoleFragment target, View source) {
    this.target = target;

    target.x_y_coordinates_1_points = Utils.findRequiredViewAsType(source, R.id.pole_frag_x_y_coordinates_1_points_sp, "field 'x_y_coordinates_1_points'", Spinner.class);
    target.pole_no = Utils.findRequiredViewAsType(source, R.id.pole_frag_pole_no_sp, "field 'pole_no'", Spinner.class);
    target.location_type__section_middle_e = Utils.findRequiredViewAsType(source, R.id.pole_frag_location_type__section_middle_e_sp, "field 'location_type__section_middle_e'", Spinner.class);
    target.martial_type__wooden_steel = Utils.findRequiredViewAsType(source, R.id.pole_frag_martial_type__wooden_steel_sp, "field 'martial_type__wooden_steel'", Spinner.class);
    target.soil_type__rock_normal_city = Utils.findRequiredViewAsType(source, R.id.pole_frag_soil_type__rock_normal_city_sp, "field 'soil_type__rock_normal_city'", Spinner.class);
    target.pole_height = Utils.findRequiredViewAsType(source, R.id.pole_frag_pole_height_sp, "field 'pole_height'", Spinner.class);
    target.notes = Utils.findRequiredViewAsType(source, R.id.pole_frag_notes_et, "field 'notes'", EditText.class);
    target.saveBtn = Utils.findRequiredViewAsType(source, R.id.pole_frag_save_btn, "field 'saveBtn'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    PoleFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.x_y_coordinates_1_points = null;
    target.pole_no = null;
    target.location_type__section_middle_e = null;
    target.martial_type__wooden_steel = null;
    target.soil_type__rock_normal_city = null;
    target.pole_height = null;
    target.notes = null;
    target.saveBtn = null;
  }
}
