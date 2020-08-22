package com.sec.datacheck.checkdata.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.esri.arcgisruntime.data.CodedValue
import com.esri.arcgisruntime.data.CodedValueDomain
import com.sec.datacheck.R
import com.sec.datacheck.checkdata.model.Enums
import com.sec.datacheck.checkdata.model.models.OConstants
import com.sec.datacheck.checkdata.model.models.OnlineQueryResult
import com.sec.datacheck.checkdata.view.POJO.FieldModel
import com.sec.datacheck.databinding.FeatureFieldsListRowItemBinding

class FieldsAdapter(private val items: ArrayList<FieldModel>,
                    private val listener: FeatureFieldClickListener?,
                    private val context: Context) : RecyclerView.Adapter<FieldsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FeatureFieldsListRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        ), listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], context)
    fun setSelectedItem(selectedFeature: OnlineQueryResult) {

    }

    class ViewHolder(private val binding: FeatureFieldsListRowItemBinding, private val listener: FeatureFieldClickListener?) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FieldModel, context: Context) {
            if (item.type == Enums.FieldType.DataField.type) {
                binding.fieldValue.visibility = View.VISIBLE
                binding.fieldTitle.text = item.alias
                binding.fieldValue.text = item.textValue

                if (item.isHasCheckDomain && item.checkDomain != null && item.checkDomain.type == Enums.FieldType.DomainWithDataField.type) {
                    binding.checkListSpinner.visibility = View.VISIBLE
                    initSpinner(item.checkDomain, context)
                } else {
                    binding.checkListSpinner.visibility = View.GONE
                }
            } else if (item.type == Enums.FieldType.DomainWithNoDataField.type) {
                binding.checkListSpinner.visibility = View.VISIBLE
                binding.fieldTitle.text = item.alias
                binding.fieldValue.visibility = View.INVISIBLE
                initSpinner(item, context)
                if (item.title == OConstants.SITE_VISITE) {
                    binding.fieldTitle.setTextColor(context.resources.getColor(R.color.orange))
                } else {
                    binding.fieldTitle.setTextColor(context.resources.getColor(R.color.purple))
                }
            }
        }

        private fun initSpinner(fieldModel: FieldModel, context: Context) {
            try {
                val typesList = ArrayList<String>()
                val codeList = ArrayList<String>()
                val typeDomain: CodedValueDomain = fieldModel.choiceDomain
                val codedValues: List<CodedValue> = typeDomain.codedValues
                for (codedValue in codedValues) {
                    typesList.add(codedValue.name)
                    codeList.add(codedValue.code.toString())
                }
                val adapter: ArrayAdapter<*> = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, typesList)
                binding.checkListSpinner.adapter = adapter
                if (fieldModel.selectedDomainIndex != null && fieldModel.selectedDomainIndex as Int != 0) {
                    binding.checkListSpinner.setSelection(fieldModel.selectedDomainIndex as Int - 1)
                } else {
                    binding.checkListSpinner.setSelection(0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

interface FeatureFieldClickListener {
    fun onItemSelectedSelected(selectedField: FieldModel)
}