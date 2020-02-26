package com.sec.datacheck.checkdata.view.fragments.Meter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.sec.datacheck.R;
import com.sec.datacheck.checkdata.model.models.Columns;
import com.sec.datacheck.checkdata.model.models.OnlineQueryResult;
import com.sec.datacheck.checkdata.view.POJO.MeterModel;
import com.sec.datacheck.checkdata.view.activities.map.MapActivity;
import com.sec.datacheck.checkdata.view.activities.map.MapPresenter;
import com.sec.datacheck.checkdata.view.utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeterFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE_GALLERY = 1;
    private static final int REQUEST_CODE_TAKE_PICTURE = 2;
    private static final int WRITE_EXTERNAL_STORAGE = 3;
    private static final int READ_EXTERNAL_STORAGE = 4;
    private static final int REQUEST_CODE_VIDEO = 5;
    private static final int REQUEST_CODE_AUDIO = 6;
    private static final String JPG = "jpg";
    private static final String MP4 = "mp4";
    private static final String TAG = "MeterFragment";
    private static MapActivity mCurrent;
    private static MapPresenter mPresenter;
    private static OnlineQueryResult mSelectedResult;
    private static boolean mOnlineData;
    private static String TEMP_PHOTO_FILE_NAME;
    private final String IMAGE_FOLDER_NAME = "AJC_Collector";
    @BindView(R.id.meter_frag_X_Y_Coordinates_sp)
    Spinner X_Y_CoordinatesSpinner;
    @BindView(R.id.meter_frag_Serial_No_sp)
    Spinner serialNo;
    @BindView(R.id.meter_frag_subscription_no_sp)
    Spinner subscriptionNo;
    @BindView(R.id.meter_frag_voltage_type__mv_lv_sp)
    Spinner voltageType;
    @BindView(R.id.meter_frag_meter_type__sort_digital_mechan_sp)
    Spinner meterTypeSortDigitalMechan;
    @BindView(R.id.meter_frag_meter_work_type_normal_c_t_sp)
    Spinner meterWorkTypeNormal;
    @BindView(R.id.meter_frag_meter_box_type__single_double_q_sp)
    Spinner meterBoxTypeSingleDouble_q;
    @BindView(R.id.meter_frag_substation_no_sp)
    Spinner substationNo;
    @BindView(R.id.meter_frag_substation_feeder_no_sp)
    Spinner substationFeederNo;
    @BindView(R.id.meter_frag_c_t_ratio_sp)
    Spinner C_T_ratio;
    @BindView(R.id.meter_frag_manufacture_sp)
    Spinner Manufacture;
    @BindView(R.id.meter_frag_backer_size_sp)
    Spinner backerSize;
    @BindView(R.id.meter_frag_smart_sp)
    Spinner Smart;
    @BindView(R.id.meter_frag_customer_type_sp)
    Spinner customerTypeSp;
    @BindView(R.id.substation_frag_notes_et)
    EditText notes;
    @BindView(R.id.substation_frag_save_btn)
    Button saveBtn;
    private Feature selectedFeature;
    private FeatureLayer selectedLayer;
    private FeatureTable selectedTable;
    private GeodatabaseFeatureTable selectedOfflineFeatureTable;
    private String objectID;
    private Map<String, String> types = null;
    private ArrayList<String> typesList = null;
    private List<CodedValue> codedValues;
    private CodedValueDomain typeDomain;
    private HashMap<String, String> codeValue;
    private ArrayList<String> codeList;
    private File mFileTemp;

    public MeterFragment() {
    }


    public static MeterFragment newInstance(MapActivity current, MapPresenter presenter, OnlineQueryResult selectedResult, boolean onlineData) {
        mCurrent = current;
        mPresenter = presenter;
        mSelectedResult = selectedResult;
        mOnlineData = onlineData;
        return new MeterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            ButterKnife.bind(this, view);
            setHasOptionsMenu(true);
            if (mCurrent.onlineData) {
                loadFeature();
            } else {
                selectedLayer = mSelectedResult.getFeatureLayer();
                selectedOfflineFeatureTable = mSelectedResult.getGeodatabaseFeatureTable();
                selectedFeature = mSelectedResult.getFeature();
                init();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFeature() {
        try {
            Log.i(TAG, "loadFeature(): is called");

            Utilities.showLoadingDialog(mCurrent);
            ArcGISFeature feature = mSelectedResult.getFeature();
            feature.loadAsync();

            feature.addDoneLoadingListener(() -> {
                try {
                    Log.i(TAG, "loadFeature(): feature is loaded");
                    Utilities.dismissLoadingDialog();
                    selectedFeature = feature;
                    int count = 0;
                    Map<String, Object> attr = selectedFeature.getAttributes();
                    for (String key : attr.keySet()) {
                        try {
                            if (attr.get(key) == null) {
                                count++;
                            } else {
                                Log.i(TAG, "loadFeature(): " + key + " = " + attr.get(key));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Log.i(TAG, "loadFeature(): count = " + count + " attr size = " + attr.size());
//                    if ((count + 2) == attr.size()) {
//                        loadFeature();
//                    } else {
                    init();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utilities.dismissLoadingDialog();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        try {
            super.onCreateOptionsMenu(menu, inflater);
            ActionBar actionBar = mCurrent.getSupportActionBar();

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                if (mSelectedResult.getFeatureLayer() != null && mSelectedResult.getFeatureLayer().getName() != null && !mSelectedResult.getFeatureLayer().getName().isEmpty()) {
                    actionBar.setTitle("Update " + mSelectedResult.getFeatureLayer().getName());
                }
            }

            Log.i(TAG, "onCreateOptionsMenu(): is called");
            inflater.inflate(R.menu.menu_fragment_edit, menu);
            mCurrent.menuItemOverflow.setVisible(false);

            mCurrent.item_load_previous_offline.setVisible(false);
            mCurrent.menuItemOffline.setVisible(false);
            mCurrent.menuItemSync.setVisible(false);
            mCurrent.menuItemOnline.setVisible(false);
            mCurrent.menuItemGoOfflineMode.setVisible(false);
            mCurrent.menuItemGoOnlineMode.setVisible(false);
            mCurrent.menuItemOverflow.setVisible(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i(TAG, " onOptionsItemSelected(): is called");
        if (item.getItemId() == R.id.menu_save) {
            Log.i(TAG, " onOptionsItemSelected(): save menu item is called");
            saveChanges();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            mCurrent.hideFragmentFromActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        try {


            initSpinner(X_Y_CoordinatesSpinner, Columns.Meter.X_Y_Coordinates_1_points);
            initSpinner(backerSize, Columns.Meter.Backer__Size);
            initSpinner(C_T_ratio, Columns.Meter.C_T_Ratio);
            initSpinner(customerTypeSp, Columns.Meter.Customer_Type);
            initSpinner(Manufacture, Columns.Meter.Manufacture);
            initSpinner(meterTypeSortDigitalMechan, Columns.Meter.Meter_Type__Sort_Digital_Mechan);
            initSpinner(meterBoxTypeSingleDouble_q, Columns.Meter.Meter_Box_Type__single_double_q);
            initSpinner(meterWorkTypeNormal, Columns.Meter.Meter_Work_Type__Normal___C_T__);
            initSpinner(serialNo, Columns.Meter.Serial_No);
            initSpinner(substationFeederNo, Columns.Meter.Substation_Feeder_No);
            initSpinner(subscriptionNo, Columns.Meter.Subscription_No);
            initSpinner(voltageType, Columns.Meter.Voltage_Type__MV_LV);
            initSpinner(substationNo, Columns.Meter.Substation_No);
            initSpinner(Smart, Columns.Meter.Smart);
            initNotes(notes, Columns.Meter.Notes);

            saveBtn.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initNotes(EditText notes, String notes1) {
        try {
            String note = (String) selectedFeature.getAttributes().get(notes1);

            if (note != null) {
                notes.setText(note);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSpinner(Spinner spinner, String columnName) {
        try {
            ArrayList<String> typesList = new ArrayList<>();
            ArrayList<String> codeList = new ArrayList<>();
            CodedValueDomain typeDomain;
            List<CodedValue> codedValues;
            if (mCurrent.onlineData) {
                typeDomain = (CodedValueDomain) mSelectedResult.getServiceFeatureTable().getField(columnName).getDomain();
                codedValues = typeDomain.getCodedValues();
            }else{
                typeDomain = (CodedValueDomain) mSelectedResult.getGeodatabaseFeatureTable().getField(columnName).getDomain();
                codedValues = typeDomain.getCodedValues();
            }

            for (CodedValue codedValue : codedValues) {
                typesList.add(codedValue.getName());
                codeList.add(codedValue.getCode().toString());
            }
            ArrayAdapter adapter = new ArrayAdapter<String>(mCurrent, android.R.layout.simple_spinner_dropdown_item, typesList);
            spinner.setAdapter(adapter);

            int code = 0;

            try {
                if (selectedFeature.getAttributes() == null) {
                    Log.i(TAG, "initSpinner(): selectedFeature.getAttributes() == null");

                } else if (selectedFeature.getAttributes().get(columnName) == null) {
                    Log.i(TAG, "initSpinner(): selectedFeature.getAttributes().get(columnName) == null");

                }
                code = ((Integer) selectedFeature.getAttributes().get(columnName)) - 1;
                Log.i(TAG, "initSpinner(): column name = " + columnName + " code = " + code);

            } catch (Exception e) {
                e.printStackTrace();
            }
            spinner.setSelection(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            saveChanges();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveChanges() {
        try {
            Log.i(TAG, " saveChanges(): is called");
            Utilities.showLoadingDialog(mCurrent);
            String note = notes.getText() != null ? notes.getText().toString() : "";

            MeterModel meterModel = new MeterModel();
            meterModel.setX_Y_Coordinates_1_points(X_Y_CoordinatesSpinner.getSelectedItemPosition() + 1);
            meterModel.setBacker_Size(backerSize.getSelectedItemPosition() + 1);
            meterModel.setC_T_Ratio(C_T_ratio.getSelectedItemPosition() + 1);
            meterModel.setCustomer_Type(customerTypeSp.getSelectedItemPosition() + 1);
            meterModel.setManufacture(Manufacture.getSelectedItemPosition() + 1);
            meterModel.setMeter_Type__Sort_Digital_Mechan(meterTypeSortDigitalMechan.getSelectedItemPosition() + 1);
            meterModel.setMeter_Box_TypeSingleDoubleQ(meterBoxTypeSingleDouble_q.getSelectedItemPosition() + 1);
            meterModel.setMeter_Work_Type__NormalCT(meterWorkTypeNormal.getSelectedItemPosition() + 1);
            meterModel.setSerial_No(serialNo.getSelectedItemPosition() + 1);
            meterModel.setSubstation_Feeder_No(substationFeederNo.getSelectedItemPosition() + 1);
            meterModel.setSubscription_No(subscriptionNo.getSelectedItemPosition() + 1);
            meterModel.setVoltage_Type__MV_LV(voltageType.getSelectedItemPosition() + 1);
            meterModel.setSubstation_No(substationNo.getSelectedItemPosition() + 1);
            meterModel.setSmart(Smart.getSelectedItemPosition() + 1);
            meterModel.setNotes(notes.getText().toString());

            meterModel.setNotes(note);
            if (mOnlineData) {
                mPresenter.updateMeterOnline(mSelectedResult, meterModel);
            } else {
                mPresenter.updateMeterOffline(mSelectedResult, meterModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
