package com.sec.datacheck.checkdata.view.fragments.newUpdateFragement

import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sec.datacheck.R
import com.sec.datacheck.checkdata.model.models.OnlineQueryResult
import com.sec.datacheck.checkdata.view.POJO.FieldModel
import com.sec.datacheck.checkdata.view.activities.map.MapViewModel
import com.sec.datacheck.checkdata.view.adapter.FeatureFieldClickListener
import com.sec.datacheck.checkdata.view.adapter.FeatureHeadClickListener
import com.sec.datacheck.checkdata.view.adapter.FeatureHeadsAdapter
import com.sec.datacheck.checkdata.view.adapter.FieldsAdapter
import com.sec.datacheck.databinding.DefaultFeatureBinding
import com.sec.datacheck.databinding.FragmentNewUpdateBinding
import kotlinx.android.synthetic.main.default_feature.view.*

class NewUpdateFragment : Fragment(), FeatureHeadClickListener, FeatureFieldClickListener {

    val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
    }
    lateinit var fieldsAdapter: FieldsAdapter
    lateinit var headsAdapter: FeatureHeadsAdapter
    lateinit var binding: FragmentNewUpdateBinding
    lateinit var defaultBinding: DefaultFeatureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
            fieldsAdapter = FieldsAdapter(viewModel.fields, this, requireContext())
            defaultBinding.fieldsRecyclerView.adapter = fieldsAdapter
            defaultBinding.fieldsRecyclerView.isNestedScrollingEnabled = true

            headsAdapter = FeatureHeadsAdapter(viewModel.featuresList, this, requireContext(), viewModel.onlineData)
            defaultBinding.featuresHeadsRecyclerView.adapter = headsAdapter
            defaultBinding.featuresHeadsRecyclerView.isNestedScrollingEnabled = true
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
//            saveChanges()
            return true
        } else if (item.itemId == R.id.menu_gallery) {
//            openGallery()
        } else if (item.itemId == R.id.menu_camera) {
//            takePicture()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onFeatureHeadSelected(selectedFeature: OnlineQueryResult) {
        if ((headsAdapter.getSelectedItem() != null && headsAdapter.getSelectedItem() != selectedFeature)
                || (headsAdapter.getSelectedItem() == null)) {
            headsAdapter.setSelectedItem(selectedFeature)
            viewModel.selectedResult = selectedFeature
            viewModel.prepareSelectedFeature()
            fieldsAdapter.notifyDataSetChanged()
        }
    }

    override fun onItemSelectedSelected(selectedField: FieldModel) {
    }
}