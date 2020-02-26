// Generated code from Butter Knife. Do not modify!
package com.sec.datacheck.checkdata.view.fragments;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sec.datacheck.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class EditFeatureFragment_ViewBinding implements Unbinder {
  private EditFeatureFragment target;

  @UiThread
  public EditFeatureFragment_ViewBinding(EditFeatureFragment target, View source) {
    this.target = target;

    target.mOfflineAttachmentsRV = Utils.findRequiredViewAsType(source, R.id.offline_attachments_recycler_view, "field 'mOfflineAttachmentsRV'", RecyclerView.class);
    target.mObjectIDTV = Utils.findRequiredViewAsType(source, R.id.object_id, "field 'mObjectIDTV'", TextView.class);
    target.typesSpinner = Utils.findRequiredViewAsType(source, R.id.type_spinner, "field 'typesSpinner'", Spinner.class);
    target.mCodeET = Utils.findRequiredViewAsType(source, R.id.mCodeEt, "field 'mCodeET'", EditText.class);
    target.mDeviceNoET = Utils.findRequiredViewAsType(source, R.id.device_num, "field 'mDeviceNoET'", EditText.class);
    target.mTakePictureFAB = Utils.findRequiredViewAsType(source, R.id.take_picture_fab, "field 'mTakePictureFAB'", FloatingActionButton.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    EditFeatureFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mOfflineAttachmentsRV = null;
    target.mObjectIDTV = null;
    target.typesSpinner = null;
    target.mCodeET = null;
    target.mDeviceNoET = null;
    target.mTakePictureFAB = null;
  }
}
