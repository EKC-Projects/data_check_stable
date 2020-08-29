package com.sec.datacheck.checkdata.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sec.datacheck.databinding.OfflineRvRowItemBinding
import com.squareup.picasso.Picasso
import java.io.File

class ImagesAdapter(private val images: ArrayList<File>, private val listener: ImageAdapterListener) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(OfflineRvRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = images.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(images[position], listener)
    fun addImage(image: File) {
        images.add(image)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: OfflineRvRowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(image: File, listener: ImageAdapterListener) {
            try {
                Picasso.get().load(image).into(binding.offlineAttachmentsRvRowItemImageView)
                binding.offlineAttachmentsRvRowItemImageView.setOnClickListener {
                    listener.onImageSelected(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

interface ImageAdapterListener {
    fun onImageSelected(image: File)
}