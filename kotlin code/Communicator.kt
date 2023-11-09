package com.ndr.unlockwithface

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.ndr.unlockwithface.profiles.ProfilePhotos

interface Communicator {
    // Handling fragments
    fun replaceFragment(fragment: Fragment)
    fun replaceNavBarId(id: Int)
    fun sendBitmap(fragment: Fragment, data: Bitmap)

    // Handling internal storage
    fun saveBitmap(filename: String, bitmap: Bitmap): Boolean
    suspend fun loadsBitmap(): List<ProfilePhotos>
    fun deleteBitmap(filename: String)

    fun loadFaces() : MutableList<ProfilePhotos>

}