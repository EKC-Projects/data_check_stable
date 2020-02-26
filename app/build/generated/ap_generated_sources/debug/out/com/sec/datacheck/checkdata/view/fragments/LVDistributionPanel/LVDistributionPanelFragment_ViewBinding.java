// Generated code from Butter Knife. Do not modify!
package com.sec.datacheck.checkdata.view.fragments.LVDistributionPanel;

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

public class LVDistributionPanelFragment_ViewBinding implements Unbinder {
  private LVDistributionPanelFragment target;

  @UiThread
  public LVDistributionPanelFragment_ViewBinding(LVDistributionPanelFragment target, View source) {
    this.target = target;

    target.type_of_the_lv_panel_sp = Utils.findRequiredViewAsType(source, R.id.type_of_the_lv_panel_sp, "field 'type_of_the_lv_panel_sp'", Spinner.class);
    target.total_no_of_feeders_sp = Utils.findRequiredViewAsType(source, R.id.total_no_of_feeders_sp, "field 'total_no_of_feeders_sp'", Spinner.class);
    target.total_no_of_used_feeders_sp = Utils.findRequiredViewAsType(source, R.id.total_no_of_used_feeders_sp, "field 'total_no_of_used_feeders_sp'", Spinner.class);
    target.total_no__of_spare_feeders_sp = Utils.findRequiredViewAsType(source, R.id.total_no__of_spare_feeders_sp, "field 'total_no__of_spare_feeders_sp'", Spinner.class);
    target.main_cables_type_sp = Utils.findRequiredViewAsType(source, R.id.main_cables_type_sp, "field 'main_cables_type_sp'", Spinner.class);
    target.number_of_outgoing_cables_sp = Utils.findRequiredViewAsType(source, R.id.number_of_outgoing_cables_sp, "field 'number_of_outgoing_cables_sp'", Spinner.class);
    target.current_rating_sp = Utils.findRequiredViewAsType(source, R.id.current_rating_sp, "field 'current_rating_sp'", Spinner.class);
    target.voltage_of_equipment_sp = Utils.findRequiredViewAsType(source, R.id.voltage_of_equipment_sp, "field 'voltage_of_equipment_sp'", Spinner.class);
    target.manufacture_of_equipment_sp = Utils.findRequiredViewAsType(source, R.id.manufacture_of_equipment_sp, "field 'manufacture_of_equipment_sp'", Spinner.class);
    target.feeders_Panel_distribution_sp = Utils.findRequiredViewAsType(source, R.id.feeders_Panel_distribution_sp, "field 'feeders_Panel_distribution_sp'", Spinner.class);
    target.notes = Utils.findRequiredViewAsType(source, R.id.lv_distribution_panel_frag_notes_et, "field 'notes'", EditText.class);
    target.saveBtn = Utils.findRequiredViewAsType(source, R.id.lv_distribution_panel_frag_save_btn, "field 'saveBtn'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    LVDistributionPanelFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.type_of_the_lv_panel_sp = null;
    target.total_no_of_feeders_sp = null;
    target.total_no_of_used_feeders_sp = null;
    target.total_no__of_spare_feeders_sp = null;
    target.main_cables_type_sp = null;
    target.number_of_outgoing_cables_sp = null;
    target.current_rating_sp = null;
    target.voltage_of_equipment_sp = null;
    target.manufacture_of_equipment_sp = null;
    target.feeders_Panel_distribution_sp = null;
    target.notes = null;
    target.saveBtn = null;
  }
}
