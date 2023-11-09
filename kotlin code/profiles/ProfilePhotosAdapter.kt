package com.ndr.unlockwithface.profiles

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ndr.unlockwithface.databinding.ListProfilesBinding


class ProfilePhotosAdapter(private val context: Context, private val onPhotoClick: (ProfilePhotos) -> Unit
) : ListAdapter<ProfilePhotos, ProfilePhotosAdapter.PhotoViewHolder> (Companion) {

    inner class PhotoViewHolder(val binding: ListProfilesBinding): RecyclerView.ViewHolder(binding.root)


    companion object : DiffUtil.ItemCallback<ProfilePhotos>() {
        override fun areItemsTheSame(oldItem: ProfilePhotos, newItem: ProfilePhotos): Boolean {
            return oldItem.name == newItem.name
        }
        override fun areContentsTheSame(oldItem: ProfilePhotos, newItem: ProfilePhotos): Boolean {
            return oldItem.name == newItem.name && oldItem.bitmap.sameAs(newItem.bitmap)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePhotosAdapter.PhotoViewHolder {
        return PhotoViewHolder(
            ListProfilesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProfilePhotosAdapter.PhotoViewHolder, position: Int) {
        val photo = currentList[position]
        Glide.with(context).load(photo.bitmap).into(holder.binding.profileItem)
        holder.binding.apply {
            profileItem.setOnClickListener{
                onPhotoClick(photo)
            }
        }

    }


}