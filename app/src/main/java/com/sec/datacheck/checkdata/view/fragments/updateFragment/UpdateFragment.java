package com.sec.datacheck.checkdata.view.fragments.updateFragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Domain;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.github.clans.fab.FloatingActionButton;
import com.sec.datacheck.R;
import com.sec.datacheck.checkdata.model.models.Columns;
import com.sec.datacheck.checkdata.model.models.OnlineQueryResult;
import com.sec.datacheck.checkdata.view.POJO.FieldModel;
import com.sec.datacheck.checkdata.view.activities.map.MapActivity;
import com.sec.datacheck.checkdata.view.activities.map.MapPresenter;
import com.sec.datacheck.checkdata.view.adapter.AttachmentRVAdapter;
import com.sec.datacheck.checkdata.view.adapter.UpdateFragRecAdapter;
import com.sec.datacheck.checkdata.view.utils.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UpdateFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "UpdateFragment";
    @BindView(R.id.fragment_update_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.fragment_update_notes_et)
    EditText notesET;

    @BindView(R.id.fragment_update_save_btn)
    Button saveBtn;

    @BindView(R.id.fragment_update_take_picture_fab)
    FloatingActionButton mTakePictureFAB;

    @BindView(R.id.offline_attachments_recycler_view)
    RecyclerView mOfflineAttachmentsRV;

    private List<File> adapterData;
    private AttachmentRVAdapter mOfflineAttachmentRVAdapter;


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
    private final String IMAGE_FOLDER_NAME = "SEC_Check_Data";

    private static final String JPG = "jpg";
    private static final String MP4 = "mp4";

    private UpdateFragRecAdapter recAdapter;

    private ArrayList<FieldModel> fields;

    public UpdateFragment() {
        // Required empty public constructor
    }

    public static UpdateFragment newInstance(MapActivity current, MapPresenter presenter, OnlineQueryResult selectedResult, boolean onlineData) {
        mCurrent = current;
        mPresenter = presenter;
        mOnlineData = onlineData;
        mSelectedResult = selectedResult;
        return new UpdateFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update, container, false);
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
        } else if (item.getItemId() == R.id.menu_gallery) {
            openGallery();
        } else if (item.getItemId() == R.id.menu_camera) {
            takePicture();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

            ButterKnife.bind(this, view);

            setHasOptionsMenu(true);
            init();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        try {
            initSelectedResult();

            try {
                adapterData = new ArrayList<>();
                loadImages();
                LinearLayoutManager mGridLayoutManager = new LinearLayoutManager(mCurrent, RecyclerView.HORIZONTAL, false);
                mOfflineAttachmentRVAdapter = new AttachmentRVAdapter(adapterData, mCurrent);
                mOfflineAttachmentsRV.setLayoutManager(mGridLayoutManager);
                mOfflineAttachmentsRV.setAdapter(mOfflineAttachmentRVAdapter);
                mOfflineAttachmentsRV.setNestedScrollingEnabled(true);

                mTakePictureFAB.setOnClickListener(this);

            } catch (Exception e) {
                e.printStackTrace();
            }

            saveBtn.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNotes() {
        try {
            String notes = (String) selectedFeature.getAttributes().get(Columns.Notes);
            notesET.setText(notes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSelectedResult() {
        if (mOnlineData) {
            selectedFeature = mSelectedResult.getFeature();
            selectedLayer = mSelectedResult.getFeatureLayer();
            selectedTable = mSelectedResult.getServiceFeatureTable();
            objectID = mSelectedResult.getObjectID();

            mSelectedResult.getFeature().loadAsync();
            mSelectedResult.getFeature().addDoneLoadingListener(() -> {
                try {
                    initRecyclerView();
                    setNotes();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            selectedFeature = mSelectedResult.getFeatureOffline();
            selectedLayer = mSelectedResult.getFeatureLayer();
            selectedOfflineFeatureTable = mSelectedResult.getGeodatabaseFeatureTable();
            objectID = mSelectedResult.getObjectID();
            initRecyclerView();
            setNotes();
        }
    }

    private void initRecyclerView() {
        try {
            Log.i(TAG, "initRecyclerView: is called");
            fields = new ArrayList<>();
            fields = extractData();

            fields = mPresenter.sortFields(fields);

            recAdapter = new UpdateFragRecAdapter(fields, mCurrent);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mCurrent);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(recAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ArrayList<FieldModel> extractData() {
        try {
            Log.i(TAG, "extractData: is called");
            List<Field> fieldList = null;
            if (mOnlineData) {
                fieldList = selectedTable.getFields();
            } else {
                fieldList = selectedOfflineFeatureTable.getFields();
            }

            if (fieldList != null && !fieldList.isEmpty()) {
                for (Field field : fieldList) {
                    if (field != null && isValidField(field.getName())) {
                        FieldModel fieldModel = new FieldModel();

                        fieldModel.setTitle(field.getName());
                        fieldModel.setAlias(field.getAlias());
                        if (field.getDomain() != null && field.getDomain() instanceof CodedValueDomain) {
                            if (isCheckDomain(field.getDomain())) {//Yes, No, N / A Domain

                                //if domain doesn't have data field
                                if (!isDomainHasDataField(field.getName(), fieldList)) {//TODO Domain Must Have default value
                                    CodedValueDomain codedValueDomain = (CodedValueDomain) field.getDomain();
                                    fieldModel.setChoiceDomain(codedValueDomain);
                                    fieldModel.setType(3);//Domain hasn't data field to check
                                    fieldModel.setSelectedDomainIndex(selectedFeature.getAttributes().get(field.getName()));
                                } else {
                                    //else if domain has data field
                                    CodedValueDomain codedValueDomain = (CodedValueDomain) field.getDomain();
                                    fieldModel.setChoiceDomain(codedValueDomain);
                                    fieldModel.setType(1);//Domain has data field to check
                                    fieldModel.setSelectedDomainIndex(selectedFeature.getAttributes().get(field.getName()));
                                }
                            } else {
                                //any other Domain
                                if (hasCheckDomain(field.getName(), fieldList)) {
                                    CodedValueDomain codedValueDomain = (CodedValueDomain) field.getDomain();
                                    boolean founded = false;
                                    for (CodedValue codedValue : codedValueDomain.getCodedValues()) {
                                        if (selectedFeature.getAttributes().get(field.getName()) != null && codedValue.getCode().equals(selectedFeature.getAttributes().get(field.getName()))) {
                                            fieldModel.setTextValue(codedValue.getName());
                                            founded = true;
                                            break;
                                        }
                                    }
                                    if (!founded) {
                                        fieldModel.setTextValue(mCurrent.getString(R.string.null_));
                                    }
                                    fieldModel.setType(2);
                                } else {
                                    CodedValueDomain codedValueDomain = (CodedValueDomain) field.getDomain();
                                    fieldModel.setChoiceDomain(codedValueDomain);
                                    fieldModel.setType(3);//Domain hasn't data field to check
                                    fieldModel.setSelectedDomainIndex(selectedFeature.getAttributes().get(field.getName()));
                                }
                            }
                        } else {
                            fieldModel.setTextValue(String.valueOf(selectedFeature.getAttributes().get(field.getName())));
                            fieldModel.setType(2);
                        }
                        fields.add(fieldModel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fields;
    }

    private boolean hasCheckDomain(String name, List<Field> fieldList) {
        name += "_Check";

        for (Field field : fieldList) {
            if (field.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDomainHasDataField(String name, List<Field> fieldList) {
        String[] nameSplit = name.split("_Check");
        if (name.endsWith("_Check") && nameSplit != null && nameSplit.length > 0) {
            String _name = nameSplit[0];
            for (Field field : fieldList) {
                if (field.getName().equals(_name)) {
                    Log.e(TAG, "isDomainHasDataField: field " + _name + " has data field");
                    return true;
                }
            }
        } else {
            Log.e(TAG, "isDomainHasDataField: field " + name + " hasn't data field");
        }
        return false;
    }

    private boolean isValidField(String name) {
        try {
            switch (name) {
                case "GlobalID":
                    return false;
                case "Note":
                    return false;
                case "created_user":
                    return false;
                case "created_date":
                    return false;
                case "last_edited_user":
                    return false;
                case "last_edited_date":
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean isCheckDomain(Domain domain) {
        try {
            Log.i(TAG, "isCheckDomain: is called");

            if (domain.getName().equals(getString(R.string.domain_check_name))) {
                Log.i(TAG, "isCheckDomain: domain is check domain");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "isCheckDomain: domain is not check domain");
        return false;
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.equals(saveBtn)) {
                saveChanges();
            } else if (v.equals(mTakePictureFAB)) {
                takePicture();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveChanges() {
        try {
            Log.i(TAG, " saveChanges(): is called");
            Utilities.showLoadingDialog(mCurrent);
            String note = notesET.getText() != null ? notesET.getText().toString() : "";
            fields = recAdapter.getFields();
            List<Integer> positions = recAdapter.getDomainCheckPositions();

            ArrayList<FieldModel> finalCheckFields = new ArrayList<>();
            for (Integer position : positions) {

                FieldModel field = fields.get(position);
                Log.e(TAG, "saveChanges: field " + field.getTitle() + " type = " + field.getType());
                finalCheckFields.add(field);
            }
//
            if (mCurrent.onlineData) {
                mPresenter.updateOnline(mSelectedResult, finalCheckFields, note);
            } else {
                mPresenter.updateOffline(mSelectedResult, finalCheckFields, note);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadImages() {
        try {
            String pointFolderName = "";
            if (mCurrent.onlineData) {
                pointFolderName = (selectedLayer.getName());
                Log.i(TAG, "loadImages(): layer name = " + pointFolderName);
            } else {
                pointFolderName = (selectedLayer.getName());
                Log.i(TAG, "loadImages(): layer name = " + pointFolderName);
            }


            Date d = new Date();
            String date = new SimpleDateFormat("dd_MM_yyyy", Locale.ENGLISH).format(d);

            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + File.separator + "AJC_Collector_COMPRESSED_Images" + File.separator;
//            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + File.separator + IMAGE_FOLDER_NAME + File.separator;
            Log.i(TAG, "loadImages(): path = " + path);
            File folder = new File(path);
            if (folder.exists()) {
                File[] allFiles = folder.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
                    }
                });

                if (allFiles != null && allFiles.length > 0) {
                    Log.i(TAG, "loadImages(): images count = " + allFiles.length);
                    for (File file : allFiles) {
                        if (file.getPath().contains("_" + mSelectedResult.getObjectID() + ".png") || file.getPath().contains("_" + mSelectedResult.getObjectID() + ".jpg") || file.getPath().contains("_" + mSelectedResult.getObjectID() + ".jpeg")) {
                            Log.i(TAG, "loadImages(): image path = " + file.getPath() + " contains objectID = " + mSelectedResult.getObjectID());
                            adapterData.add(file);
                        } else {
                            Log.i(TAG, "loadImages(): image path = " + file.getPath() + " doesn't contains objectID = " + mSelectedResult.getObjectID());
                        }
                    }

                    for (File file : adapterData) {
                        Log.i(TAG, "loadImages(): image path = " + file.getPath());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File[] getFolders(File path) {
        return path.listFiles();
    }

    private void takePicture() {
        try {

            if (ActivityCompat.checkSelfPermission(mCurrent, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mCurrent, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);

            } else if (ActivityCompat.checkSelfPermission(mCurrent, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mCurrent, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
            } else {

                String pointName;
                try {
                    pointName = String.valueOf(objectID);
                } catch (Exception e) {
                    e.printStackTrace();
                    pointName = "1"; // TODO Remove
                }
                String pointFolderName = "";

                if (mCurrent.onlineData)
                    pointFolderName = (selectedLayer.getName());
                else {
                    Log.i(TAG, "layer name = " + selectedLayer.getName());
                    pointFolderName = (selectedLayer.getName());
                }

                createFile(pointName, pointFolderName, JPG, "IMG");
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoURI = FileProvider.getUriForFile(mCurrent, getString(R.string.app_package_name), mFileTemp);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    List<ResolveInfo> resInfoList = mCurrent.getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        mCurrent.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }
                startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PICTURE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFile(String name, String layerFolderName, String extension, String type) {
        try {

            Date d = new Date();
            TEMP_PHOTO_FILE_NAME = "Image_" + new SimpleDateFormat("dd_MM_yyyy", Locale.ENGLISH).format(d) + layerFolderName + "_" + name + "." + extension;


            File rootFolder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM), IMAGE_FOLDER_NAME);

            if (!rootFolder.exists()) {
                if (rootFolder.mkdir()) {
                    Log.i(TAG, "createFile(): rootFolder created");
                } else {
                    Log.i(TAG, "createFile(): rootFolder director not created");
                }
            }

            File dateFolderName = new File(rootFolder, new SimpleDateFormat("dd_MM_yyyy", Locale.ENGLISH).format(d));

            if (!dateFolderName.exists()) {

                if (dateFolderName.mkdir()) {
                    Log.i(TAG, "createFile(): dateFolderName directory created");

                } else {
                    Log.i(TAG, "createFile(): dateFolderName directory not created");
                }
            }

            File layerFolder = new File(dateFolderName.getPath(), layerFolderName);

            if (!layerFolder.exists()) {
                if (layerFolder.mkdir()) {
                    Log.i(TAG, "createFile(): layerFolder directory is created = " + layerFolder.toString());
                } else {
                    Log.i(TAG, "createFile(): layerFolder directory not created");
                }
            }


            File pointFolder = new File(layerFolder.getPath(), name);
            if (!pointFolder.exists()) {
                if (pointFolder.mkdir()) {
                    Log.i(TAG, "createFile(): pointFolder directory is created = " + pointFolder.toString());
                } else {
                    Log.i(TAG, "createFile(): pointFolder director not created");
                }
            }

            mFileTemp = new File(pointFolder.getPath() + File.separator +
                    type + "_" + new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss", Locale.ENGLISH).format(d) + layerFolderName + "_" + name + "." + extension.trim());

            Log.i(TAG, "createFile(): pointFolder directory is created = " + pointFolder.toString());

            // rename image...
            Log.i(TAG, "file createFile " + mFileTemp.getPath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult(): is called");
        Log.i(TAG, "onActivityResult(): requestCode = " + requestCode);
        Log.i(TAG, "onActivityResult(): resultCode = " + resultCode);

        if ((resultCode == Activity.RESULT_OK)) {
            switch (requestCode) {
                case REQUEST_CODE_TAKE_PICTURE:
                    addAttachmentToRecyclerView();

                    break;
                case REQUEST_CODE_GALLERY:
                    if (data != null) {
                        try {

                            if (data != null && data.getData() != null) {

                                Uri uri = data.getData();

                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mCurrent.getContentResolver(), uri);
                                    Log.d(TAG, String.valueOf(bitmap));
                                    writeBitmapInFile(bitmap);
                                    addAttachmentToRecyclerView();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
//                            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                            writeBitmapInFile(bitmap);
//                            addAttachmentToRecyclerView();

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
//                        String path = data.getStringExtra(CropImage.IMAGE_PATH);
//                        if (path == null) {
//                            return;
//                        }
//                            hsAttachments.setVisibility(View.VISIBLE);
//                            tvAttachment.setText(getString(R.string.attachments));
//                            addAttachmentToFeature(mFileTemp);
                        }
                    }
                    break;

                case REQUEST_CODE_VIDEO:
                    try {
//                        InputStream inputStream = editorActivity.getContentResolver().openInputStream(data.getData());
//                        FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
//                        copyStream(inputStream, fileOutputStream);
//                        fileOutputStream.close();
//                        inputStream.close();
//                        mFileTemp = new File(getRealPathFromURI(data.getData()));
//                        Log.d("video",mFileTemp.getPath());
//                        checkVideoSize(mFileTemp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

//                case REQUEST_CODE_CROP_IMAGE:
//                    break;

            }
        }
    }

    private void addAttachmentToRecyclerView() {
        try {
            mOfflineAttachmentsRV.setVisibility(View.VISIBLE);
            if (mFileTemp != null && mFileTemp.getPath() != null) {
                Log.i(TAG, "addAttachmentToRecyclerView(): mFile Temp != null");
                File newFile = compressImage(mFileTemp.getPath(), mCurrent, "", "");
                mOfflineAttachmentRVAdapter.addImageBitmap(newFile);
                mOfflineAttachmentRVAdapter.notifyDataSetChanged();
            } else {
                Log.i(TAG, "addAttachmentToRecyclerView(): mFile Temp = null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeBitmapInFile(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(mFileTemp);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void openGallery() {

        String pointName = objectID;
        String pointFolderName = "";
        if (mCurrent.onlineData) {
            pointFolderName = (selectedLayer.getName());
            Log.i(TAG, "openGallery(): layer folder name = " + pointFolderName);
        } else {
            Log.i(TAG, "layer name = " + selectedLayer.getName());
            pointFolderName = (selectedLayer.getName());
        }
        createFile(pointName, pointFolderName, JPG, "IMG");
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//
//        Uri photoURI = FileProvider.getUriForFile(mCurrent, getString(R.string.app_package_name), mFileTemp);
//        photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }

    public static Bitmap decodeScaledBitmapFromSdCard(String filePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    private static File compressImage(String filePath, Context context, String imageClassType, String customerCode) {

//        String filePath = getRealPathFromURI(imageUri, context);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        try {
            Canvas canvas = new Canvas(Objects.requireNonNull(scaledBitmap));
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        } catch (Exception e) {
            e.printStackTrace();
        }
//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;

        File file = getImageFile();
        String filename = getFilename(imageClassType, context, customerCode);

        File mImageFile = new File(file.getPath(), filename);

        try {
            out = new FileOutputStream(mImageFile);

//          write the compressed bitmap at the destination specified by filename.
            Objects.requireNonNull(scaledBitmap).compress(Bitmap.CompressFormat.PNG, 80, out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mImageFile;
    }

    private static File getImageFile() {
        final String IMAGES_FOLDER_NAME = "AJC_Collector_COMPRESSED_Images";
        File mediaStorageDir = null;

        try {

            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM), IMAGES_FOLDER_NAME);

            if (!mediaStorageDir.exists())
                mediaStorageDir.mkdir();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaStorageDir;
    }

    private static String getFilename(String type, Context context, String customerCode) {
        String uriSting = null;


        try {
            Date d = new Date();
            uriSting = "Image_" + new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.ENGLISH).format(d) + "_" + mSelectedResult.getObjectID() + ".png";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return uriSting;
    }

}
