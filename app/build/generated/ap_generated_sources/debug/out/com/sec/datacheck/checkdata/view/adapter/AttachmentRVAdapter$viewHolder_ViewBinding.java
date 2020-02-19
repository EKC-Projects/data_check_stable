// Generated code from Butter Knife. Do not modify!
package com.sec.datacheck.checkdata.view.adapter;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.sec.datacheck.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AttachmentRVAdapter$viewHolder_ViewBinding implements Unbinder {
  private AttachmentRVAdapter.viewHolder target;

  @UiThread
  public AttachmentRVAdapter$viewHolder_ViewBinding(AttachmentRVAdapter.viewHolder target,
      View source) {
    this.target = target;

    target.imageView = Utils.findRequiredViewAsType(source, R.id.offline_attachments_rv_row_item_image_view, "field 'imageView'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AttachmentRVAdapter.viewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.imageView = null;
  }
}
