package com.sec.datacheck.checkdata.view.fragments.FuseCutOutFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

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
import com.sec.datacheck.checkdata.view.POJO.FuseCutOutModel;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class FuseCutOutFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "FuseCutOutFragment";
    @BindView(R.id.fuse_cut_out_x_y_frag_coordinates_1_points_sp)
    Spinner X_Y_CoordinatesSpinner_1_pointsSpinner;

    @BindView(R.id.fuse_cut_out_frag_Fuse_Cut_Out_No_sp)
    Spinner FuseCutOutNoSpinner;

    @BindView(R.id.fuse_cut_out_frag_Ratio_Amp_sp)
    Spinner rationAmpSpinner;

    @BindView(R.id.fuse_cut_out_frag_Type_sp)
    Spinner typeSpinner;

    @BindView(R.id.fuse_cut_out_frag_Electricity_Status__Open_Close_sp)
    Spinner electricityStatusOpenCloseSpinner;

    @BindView(R.id.fuse_cut_out_frag_Voltage_sp)
    Spinner voltageLevelSpinner;

    @BindView(R.id.fuse_cut_out_frag_notes_et)
    EditText notes;

    @BindView(R.id.fuse_cut_out_frag_save_btn)
    Button saveBtn;

    private static MapActivity mCurrent;
    private static MapPresenter mPresenter;
    private static OnlineQueryResult mSelectedResult;

    private Feature selectedFeature;
    private FeatureLayer selectedLayer;
    private FeatureTable selectedTable;
    private GeodatabaseFeatureTable selectedOfflineFeatureTable;
    private String objectID;
    private static boolean mOnlineData;

    private Map<String, String> types = null;
    private ArrayList<String> typesList = null;
    private List<CodedValue> codedValues;
    private CodedValueDomain typeDomain;
    private HashMap<String, String> codeValue;
    private ArrayList<String> codeList;
    private File mFileTemp;
    private static String TEMP_PHOTO_FILE_NAME;
    private static final int REQUEST_CODE_GALLERY = 1;
    private static final int REQUEST_CODE_TAKE_PICTURE = 2;
    private static final int WRITE_EXTERNAL_STORAGE = 3;
    private static final int READ_EXTERNAL_STORAGE = 4;
    private static final int REQUEST_CODE_VIDEO = 5;
    private static final int REQUEST_CODE_AUDIO = 6;
    private final String IMAGE_FOLDER_NAME = "AJC_Collector";

    private static final String JPG = "jpg";
    private static final String MP4 = "mp4";

    public FuseCutOutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fuse_cut_out, container, false);
    }


    public static FuseCutOutFragment newInstance(MapActivity current, MapPresenter presenter, OnlineQueryResult selectedResult, boolean onlineData) {
        mCurrent = current;
        mPresenter = presenter;
        mSelectedResult = selectedResult;
        mOnlineData = onlineData;
        return new FuseCutOutFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            ButterKnife.bind(this, view);
            setHasOptionsMenu(true);

            if (mCurrent.onlineData) {
                loadFeature();
            }
            else {
                loadFeatureOffline();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFeatureOffline() {
        try {
            selectedLayer = mSelectedResult.getFeatureLayer();
            selectedOfflineFeatureTable = mSelectedResult.getGeodatabaseFeatureTable();
            selectedFeature = mSelectedResult.getFeatureOffline();


            init();
        }catch (Exception e){
            e.getStackTrace();
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

            initSpinner(X_Y_CoordinatesSpinner_1_pointsSpinner, Columns.FuseCutOut.X_Y_Coordinates_1_points);
            initSpinner(FuseCutOutNoSpinner, Columns.FuseCutOut.Fuse_Cut_Out_No);
            initSpinner(rationAmpSpinner, Columns.FuseCutOut.Ratio_Amp);
            initSpinner(typeSpinner, Columns.FuseCutOut.Type);
            initSpinner(electricityStatusOpenCloseSpinner, Columns.FuseCutOut.Electricity_Status__Open_Close);
            initSpinner(voltageLevelSpinner, Columns.FuseCutOut.Voltage);

            initNotes(notes, Columns.FuseCutOut.Notes);

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
                if (selectedFeature.getAttributes() == null){
                    Log.i(TAG, "initSpinner(): selectedFeature.getAttributes() == null");

                }else if (selectedFeature.getAttributes().get(columnName) == null){
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

            FuseCutOutModel fuseCutOutModel = new FuseCutOutModel();
            fuseCutOutModel.setX_Y_Coordinates_1_points(X_Y_CoordinatesSpinner_1_pointsSpinner.getSelectedItemPosition() + 1);
            fuseCutOutModel.setFuse_Cut_Out_No(FuseCutOutNoSpinner.getSelectedItemPosition() + 1);
            fuseCutOutModel.setRatio_Amp(rationAmpSpinner.getSelectedItemPosition() + 1);
            fuseCutOutModel.setType(typeSpinner.getSelectedItemPosition() + 1);
            fuseCutOutModel.setElectricity_Status__Open_Close(electricityStatusOpenCloseSpinner.getSelectedItemPosition() + 1);
            fuseCutOutModel.setVoltage(voltageLevelSpinner.getSelectedItemPosition() + 1);

            fuseCutOutModel.setNotes(note);

            if (mCurrent.onlineData) {
                mPresenter.updateFuseCutOutOnline(mSelectedResult, fuseCutOutModel);
            }else{
                mPresenter.updateFuseCutOutOffline(mSelectedResult, fuseCutOutModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
