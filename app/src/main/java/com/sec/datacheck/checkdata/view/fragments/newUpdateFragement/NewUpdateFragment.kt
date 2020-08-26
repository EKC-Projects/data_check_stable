package com.sec.datacheck.checkdata.view.fragments.newUpdateFragement

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sec.datacheck.R
import com.sec.datacheck.checkdata.model.models.Columns
import com.sec.datacheck.checkdata.model.models.OConstants
import com.sec.datacheck.checkdata.model.models.OnlineQueryResult
import com.sec.datacheck.checkdata.model.requestPermissions
import com.sec.datacheck.checkdata.view.POJO.FieldModel
import com.sec.datacheck.checkdata.view.activities.map.MapViewModel
import com.sec.datacheck.checkdata.view.adapter.*
import com.sec.datacheck.checkdata.view.utils.Utilities
import com.sec.datacheck.databinding.DefaultFeatureBinding
import com.sec.datacheck.databinding.FragmentNewUpdateBinding
import kotlinx.android.synthetic.main.default_feature.view.*
import java.io.IOException

class NewUpdateFragment : Fragment(), FeatureHeadClickListener, FeatureFieldClickListener {

    val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
    }
    lateinit var fieldsAdapter: FieldsAdapter
    lateinit var headsAdapter: FeatureHeadsAdapter
    lateinit var imagesAdapter: ImagesAdapter
    lateinit var binding: FragmentNewUpdateBinding
    lateinit var defaultBinding: DefaultFeatureBinding
    var selectedFieldsPosition = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_update, container, false)
        defaultBinding = DataBindingUtil.findBinding(binding.root.new_update_feature_container)!!
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = NewUpdateFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        init()
    }

    private fun init() {
        try {
            displayFields()
            displayFeaturesHeads()
            displayImages()
            displayNotes()

            defaultBinding.takePictureFab.setOnClickListener {
                takePicture()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun takePicture() {
        try {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions()
            } else {

                val pointName = viewModel.objectID

                val pointFolderName: String = if (viewModel.selectedLayer != null) {
                    viewModel.selectedLayer?.name!!
                } else if (viewModel.onlineData) {
                    viewModel.selectedTable?.displayName!!
                } else {
                    viewModel.selectedOfflineFeatureTable?.displayName!!
                }

                viewModel.createFile(pointName!!, pointFolderName, OConstants.PNG, "IMG")
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val photoURI = FileProvider.getUriForFile(requireContext(), getString(R.string.app_package_name), viewModel.mFileTemp)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                } else {
                    val resInfoList = requireActivity().packageManager.queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY)
                    for (resolveInfo in resInfoList) {
                        val packageName = resolveInfo.activityInfo.packageName
                        requireContext().grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }
                startActivityForResult(cameraIntent, OConstants.REQUEST_CODE_TAKE_PICTURE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun displayImages() {
        imagesAdapter = ImagesAdapter(viewModel.imagesList)
        defaultBinding.imagesRecyclerView.adapter = imagesAdapter
        defaultBinding.imagesRecyclerView.isNestedScrollingEnabled = true
        if (viewModel.imagesList.isNotEmpty()) {
            defaultBinding.imagesRecyclerView.visibility = View.VISIBLE
        } else {
            defaultBinding.imagesRecyclerView.visibility = View.GONE
        }
    }

    private fun displayFeaturesHeads() {
        try {
            headsAdapter = FeatureHeadsAdapter(viewModel.featuresList, this, requireContext(), viewModel.onlineData)
            defaultBinding.featuresHeadsRecyclerView.adapter = headsAdapter
            defaultBinding.featuresHeadsRecyclerView.isNestedScrollingEnabled = true
            if (viewModel.featuresList.isNullOrEmpty()) {
                headsAdapter.setSelectedItem(viewModel.featuresList[0])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun displayFields() {
        try {
            viewModel.liveDataFields.observe(viewLifecycleOwner, Observer {
                try {
                    if (!it.isNullOrEmpty() && !it[selectedFieldsPosition].isNullOrEmpty()) {
                        fieldsAdapter = FieldsAdapter(viewModel.fields[selectedFieldsPosition], this, requireContext())
                        defaultBinding.fieldsRecyclerView.adapter = fieldsAdapter
                        defaultBinding.fieldsRecyclerView.isNestedScrollingEnabled = true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun displayNotes() {
        try {
            if (viewModel.selectedFeature?.attributes?.get(Columns.Notes) != null && viewModel.selectedFeature?.attributes?.get(Columns.Notes).toString().isNotEmpty()) {
                defaultBinding.notesEt.setText(viewModel.selectedFeature?.attributes?.get(Columns.Notes).toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_edit, menu)

        try {
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_save) {
            saveChanges()
            return true
        } else if (item.itemId == R.id.menu_gallery) {
//            openGallery()
        } else if (item.itemId == R.id.menu_camera) {
            takePicture()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveChanges() {
        try {
            val notes = if (defaultBinding.notesEt.text != null && defaultBinding.notesEt.text.toString().isNotEmpty()) {
                defaultBinding.notesEt.text.toString()
            } else {
                ""
            }
            if (viewModel.onlineData) {
                Utilities.showLoadingDialog(requireActivity())
                viewModel.updateOnline(notes)
            }else{

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onFeatureHeadSelected(selectedFeature: OnlineQueryResult, position: Int) {
        if ((headsAdapter.getSelectedItem() != null && headsAdapter.getSelectedItem() != selectedFeature)
                || (headsAdapter.getSelectedItem() == null)) {
            headsAdapter.setSelectedItem(selectedFeature)
            viewModel.selectedResult = selectedFeature
            selectedFieldsPosition = position
            displayFields()
        }
    }

    override fun onItemSelectedSelected(selectedField: FieldModel, value: String) {
        viewModel.updateDomainField(selectedField, value, selectedFieldsPosition)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                OConstants.REQUEST_CODE_TAKE_PICTURE -> addImageToList()
                OConstants.REQUEST_CODE_GALLERY -> if (data != null) {
                    try {
                        if (data.data != null) {
                            val uri = data.data
                            try {
                                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                                viewModel.writeBitmapInFile(bitmap)
                                addImageToList()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    } finally {
                    }
                }
            }
        }
    }

    private fun addImageToList() {
        try {
            viewModel.compressImage(viewModel.mFileTemp.path)?.let { image ->
                imagesAdapter.addImage(image)
                if (defaultBinding.imagesRecyclerView.visibility == View.GONE) {
                    defaultBinding.imagesRecyclerView.visibility == View.VISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}