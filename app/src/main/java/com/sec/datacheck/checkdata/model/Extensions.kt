package com.sec.datacheck.checkdata.model

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.sec.datacheck.R
import com.sec.datacheck.checkdata.model.models.OConstants


fun AppCompatActivity.requestPermissions() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE),
                OConstants.MY_LOCATION_REQUEST_CODE)
    }
}

fun AppCompatActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun AppCompatActivity.showNoOfflineMapDialog() {
    showAlertDialog(getString(R.string.no_offline_version), "Ok")
}

fun AppCompatActivity.displayTitlesOfflineMapDialog(titles: Array<String?>, listener: DialogInterface.OnClickListener) {
    AlertDialog.Builder(this)
            .setItems(titles, listener).setCancelable(true)
            .setPositiveButton(getString(R.string.cancel), DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }).show()
}

fun AppCompatActivity.showAlertDialog(message: String, positiveBtnName: String) {
    val materialDialog: MaterialDialog = MaterialDialog.Builder(this)
            .autoDismiss(false)
            .cancelable(false) //.title(title)
            .content(message)
            .positiveText(positiveBtnName)
            .positiveColorRes(R.color.red)
            .onPositive { dialog: MaterialDialog, which: DialogAction? -> dialog.dismiss() }
            .build()
    materialDialog.titleView.textSize = 12f
    if (!materialDialog.isShowing) materialDialog.show()
}

fun AppCompatActivity.show(view: View) {
    view.visibility = View.VISIBLE
}

fun AppCompatActivity.hide(view: View) {
    view.visibility = View.GONE
}

fun AppCompatActivity.showInfoDialog(title:String,message:String){
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.yes), null)
            .show()
}