// Generated code from Butter Knife. Do not modify!
package com.sec.datacheck;

import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NavigateActivity_ViewBinding implements Unbinder {
  private NavigateActivity target;

  @UiThread
  public NavigateActivity_ViewBinding(NavigateActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public NavigateActivity_ViewBinding(NavigateActivity target, View source) {
    this.target = target;

    target.mDataCollectionCV = Utils.findRequiredViewAsType(source, R.id.data_collection_card_view, "field 'mDataCollectionCV'", LinearLayout.class);
    target.mDataCheckCV = Utils.findRequiredViewAsType(source, R.id.data_check_card_view, "field 'mDataCheckCV'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    NavigateActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mDataCollectionCV = null;
    target.mDataCheckCV = null;
  }
}
