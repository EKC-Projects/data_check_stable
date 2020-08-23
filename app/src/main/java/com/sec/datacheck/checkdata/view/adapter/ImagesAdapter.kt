package com.sec.datacheck.checkdata.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sec.datacheck.databinding.OfflineRvRowItemBinding
import com.squareup.picasso.Picasso
import java.io.File

class ImagesAdapter(private val images: ArrayList<File>) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(OfflineRvRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = images.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(images[position])
    fun addImage(image: File) {
        images.add(image)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: OfflineRvRowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(image: File) {
            try {
                Picasso.get().load(image).into(binding.offlineAttachmentsRvRowItemImageView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}