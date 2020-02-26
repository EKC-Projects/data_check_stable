// Generated code from Butter Knife. Do not modify!
package com.sec.datacheck.checkdata.view.fragments.OHLine;

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

public class OHLineFragment_ViewBinding implements Unbinder {
  private OHLineFragment target;

  @UiThread
  public OHLineFragment_ViewBinding(OHLineFragment target, View source) {
    this.target = target;

    target.size = Utils.findRequiredViewAsType(source, R.id.oh_line_size_sp, "field 'size'", Spinner.class);
    target.martialType = Utils.findRequiredViewAsType(source, R.id.oh_line_frag_martial_type_sp, "field 'martialType'", Spinner.class);
    target.voltage = Utils.findRequiredViewAsType(source, R.id.oh_line_frag_voltage_sp, "field 'voltage'", Spinner.class);
    target.electricityStatus = Utils.findRequiredViewAsType(source, R.id.oh_line_frag_electricity_status_sp, "field 'electricityStatus'", Spinner.class);
    target.noOfLines = Utils.findRequiredViewAsType(source, R.id.oh_line_frag_no_of_lines_sp, "field 'noOfLines'", Spinner.class);
    target.notes = Utils.findRequiredViewAsType(source, R.id.oh_line_frag_notes_et, "field 'notes'", EditText.class);
    target.saveBtn = Utils.findRequiredViewAsType(source, R.id.oh_line_frag_save_btn, "field 'saveBtn'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    OHLineFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.size = null;
    target.martialType = null;
    target.voltage = null;
    target.electricityStatus = null;
    target.noOfLines = null;
    target.notes = null;
    target.saveBtn = null;
  }
}
