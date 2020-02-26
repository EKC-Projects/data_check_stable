package com.sec.datacheck.checkdata.view.fragments.MvMetering;

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
import com.sec.datacheck.checkdata.view.POJO.MvMeteringModel;
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

public class MvMeteringFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MvMeteringFragment";
    private static final int REQUEST_CODE_GALLERY = 1;
    private static final int REQUEST_CODE_TAKE_PICTURE = 2;
    private static final int WRITE_EXTERNAL_STORAGE = 3;
    private static final int READ_EXTERNAL_STORAGE = 4;
    private static final int REQUEST_CODE_VIDEO = 5;
    private static final int REQUEST_CODE_AUDIO = 6;
    private static final String JPG = "jpg";
    private static final String MP4 = "mp4";
    private static MapActivity mCurrent;
    private static MapPresenter mPresenter;
    private static OnlineQueryResult mSelectedResult;
    private static boolean mOnlineData;
    private static String TEMP_PHOTO_FILE_NAME;
    private final String IMAGE_FOLDER_NAME = "AJC_Collector";
    @BindView(R.id.mv_metering_frag_Type_of_the_equipment_1_points_sp)
    Spinner TypeOfTheEquipment;
    @BindView(R.id.mv_metering_frag_equipment_sp)
    Spinner equipment;
    @BindView(R.id.mv_metering_frag_manufacture_of_equipment_sp)
    Spinner manufactureOfEquipment;
    @BindView(R.id.mv_metering_frag_notes_et)
    EditText notes;
    @BindView(R.id.mv_metering_frag_save_btn)
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

    public MvMeteringFragment() {
    }

    public static MvMeteringFragment newInstance(MapActivity current, MapPresenter presenter, OnlineQueryResult selectedResult, boolean onlineData) {
        mCurrent = current;
        mPresenter = presenter;
        mSelectedResult = selectedResult;
        if (mSelectedResult == null)
            Log.e(TAG, "newInstance: null" );
        else
            Log.e(TAG, "newInstance: not null" );
        mOnlineData = onlineData;
        return new MvMeteringFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mv_metering, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            ButterKnife.bind(this, view);
            setHasOptionsMenu(true);
            loadFeature();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFeature() {
        try {
            Log.i(TAG, "loadFeature(): is called");

            Utilities.showLoadingDialog(mCurrent);
            ArcGISFeature feature = mSelectedResult.getFeature();
            if (mSelectedResult==null)
                Log.e(TAG, "loadFeature: null" );
            else
                Log.e(TAG, "loadFeature: not null" );
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

            initSpinner(TypeOfTheEquipment, Columns.MV_Metering.Type_of_the_equipment);
            initSpinner(equipment, Columns.MV_Metering.Equipment);
            initSpinner(manufactureOfEquipment, Columns.MV_Metering.Manufacture_of_equipment);
            initNotes(notes, Columns.MV_Metering.Notes);

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

            CodedValueDomain typeDomain = (CodedValueDomain) mSelectedResult.getServiceFeatureTable().getField(columnName).getDomain();
            List<CodedValue> codedValues = typeDomain.getCodedValues();

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

            MvMeteringModel mvMeteringModel = new MvMeteringModel();
            mvMeteringModel.setType_of_the_equipment(TypeOfTheEquipment.getSelectedItemPosition() + 1);
            mvMeteringModel.setEquipment(equipment.getSelectedItemPosition() + 1);
            mvMeteringModel.setManufacture_of_equipment(manufactureOfEquipment.getSelectedItemPosition() + 1);


            mvMeteringModel.setNotes(note);
            if (mOnlineData) {
                mPresenter.updateMvMeteringOnline(mSelectedResult, mvMeteringModel);
            } else {
                mPresenter.updateMvMeteringOffline(mSelectedResult, mvMeteringModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
