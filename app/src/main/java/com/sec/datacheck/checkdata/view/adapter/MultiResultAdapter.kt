package com.sec.datacheck.checkdata.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sec.datacheck.R
import com.sec.datacheck.checkdata.model.models.OnlineQueryResult
import com.sec.datacheck.databinding.NewMultiResultRowItemBinding

class MultiResultAdapter(private val items: ArrayList<OnlineQueryResult>,
                         private val context: Context,
                         private val listener: MultiResultListener) : RecyclerView.Adapter<MultiResultAdapter.MultiResultViewHolder>() {

    private var selectedResult: OnlineQueryResult? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiResultViewHolder {
        return MultiResultViewHolder(NewMultiResultRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        ), listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MultiResultViewHolder, position: Int) = holder.bind(items[position], selectedResult, context)

    fun updateSelectedItem(selected: OnlineQueryResult) {
        selectedResult = selected
        notifyDataSetChanged()
    }

    class MultiResultViewHolder(
            private val binding: NewMultiResultRowItemBinding,
            private val listener: MultiResultListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OnlineQueryResult, selected: OnlineQueryResult?, context: Context) {
            binding.result = item
            binding.editMultiResultRowItemFeatureTitle.setCompoundDrawables(item.drawable, null, null, null)
            binding.editMulitResultRowItemFeatureContainer.setOnClickListener {
                listener.onItemSelected(item, adapterPosition)
            }
            binding.editMultiResultRowItemFeatureEditIc.setOnClickListener {
                listener.onEditItemSelected(item)
            }

            selected?.let {
                if (item.objectID == it.objectID) {
                    //grey background
                    binding.newEditRowItemContainer.setBackgroundColor(context.resources.getColor(R.color.bg_grey_light))
                } else {
                    //white background
                    binding.newEditRowItemContainer.setBackgroundColor(context.resources.getColor(R.color.white))
                }
            }


        }
    }

    interface MultiResultListener {
        fun onItemSelected(selectedItem: OnlineQueryResult?, position: Int)
        fun onEditItemSelected(onlineQueryResult: OnlineQueryResult?)
    }

}