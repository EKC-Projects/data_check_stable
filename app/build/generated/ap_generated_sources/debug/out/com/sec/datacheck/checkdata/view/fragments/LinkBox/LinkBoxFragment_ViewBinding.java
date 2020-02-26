// Generated code from Butter Knife. Do not modify!
package com.sec.datacheck.checkdata.view.fragments.LinkBox;

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

public class LinkBoxFragment_ViewBinding implements Unbinder {
  private LinkBoxFragment target;

  @UiThread
  public LinkBoxFragment_ViewBinding(LinkBoxFragment target, View source) {
    this.target = target;

    target.X_Y_CoordinatesSpinner_1_pointsSpinner = Utils.findRequiredViewAsType(source, R.id.link_box_frag_x_y_coordinates_1_points_sp, "field 'X_Y_CoordinatesSpinner_1_pointsSpinner'", Spinner.class);
    target.typeSpinner = Utils.findRequiredViewAsType(source, R.id.link_box_frag_Type_sp, "field 'typeSpinner'", Spinner.class);
    target.linkBoxSpinner = Utils.findRequiredViewAsType(source, R.id.link_box_frag_Link_Box_sp, "field 'linkBoxSpinner'", Spinner.class);
    target.totalNoOfLinkBoxInBoxInTheCKSpinner = Utils.findRequiredViewAsType(source, R.id.link_box_frag_Total_no__of_Link_Box_in_the_CK_sp, "field 'totalNoOfLinkBoxInBoxInTheCKSpinner'", Spinner.class);
    target.linkBoxDistributionPanelSpinner = Utils.findRequiredViewAsType(source, R.id.link_box_frag_link_box_distribution_Panel_sp, "field 'linkBoxDistributionPanelSpinner'", Spinner.class);
    target.notes = Utils.findRequiredViewAsType(source, R.id.link_box_frag_notes_et, "field 'notes'", EditText.class);
    target.saveBtn = Utils.findRequiredViewAsType(source, R.id.link_box_frag_save_btn, "field 'saveBtn'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    LinkBoxFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.X_Y_CoordinatesSpinner_1_pointsSpinner = null;
    target.typeSpinner = null;
    target.linkBoxSpinner = null;
    target.totalNoOfLinkBoxInBoxInTheCKSpinner = null;
    target.linkBoxDistributionPanelSpinner = null;
    target.notes = null;
    target.saveBtn = null;
  }
}
