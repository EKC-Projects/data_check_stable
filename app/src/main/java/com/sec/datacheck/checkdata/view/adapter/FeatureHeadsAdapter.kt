package com.sec.datacheck.checkdata.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.sec.datacheck.R
import com.sec.datacheck.checkdata.model.models.OnlineQueryResult
import com.sec.datacheck.databinding.FeaturesHeadsListRowItemBinding

class FeatureHeadsAdapter(private val items: ArrayList<OnlineQueryResult>,
                          private val listener: FeatureHeadClickListener?,
                          private val context: Context,
                          private val online: Boolean) : RecyclerView.Adapter<FeatureHeadsAdapter.ViewHolder>() {

    private var selectedFeature: OnlineQueryResult? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FeaturesHeadsListRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        ), listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], selectedFeature, context, online)

    fun setSelectedItem(item: OnlineQueryResult) {
        selectedFeature = item
        notifyDataSetChanged()
    }

    fun getSelectedItem(): OnlineQueryResult? = selectedFeature

    class ViewHolder(private val binding: FeaturesHeadsListRowItemBinding, private val listener: FeatureHeadClickListener?) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OnlineQueryResult, selectedFeature: OnlineQueryResult?, context: Context, online: Boolean) {
            if (online) {
                binding.featureTitle.text = item.serviceFeatureTable.displayName
            } else {
                binding.featureTitle.text = item.geodatabaseFeatureTable.displayName
            }
            selectedFeature?.let {
                it.objectID?.let { selectItemObjectID ->
                    item.objectID?.let { itemObjectID ->
                        if (selectItemObjectID == itemObjectID) {
                            binding.featureHeadContainer.background = ResourcesCompat.getDrawable(context.resources, R.drawable.bg_button_unpressed, context.resources.newTheme())
                        } else {
                            binding.featureHeadContainer.background = ResourcesCompat.getDrawable(context.resources, R.drawable.bg_button_pressed, context.resources.newTheme())
                        }
                    }
                }
            }
            binding.featureHeadContainer.setOnClickListener {
                listener?.onFeatureHeadSelected(item)
            }
        }

    }
}

interface FeatureHeadClickListener {
    fun onFeatureHeadSelected(selectedFeature: OnlineQueryResult)
}