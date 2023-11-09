package com.ndr.unlockwithface

import android.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.ndr.unlockwithface.databinding.ActivityFakeHomeBinding
import com.ndr.unlockwithface.profiles.ProfilePhotos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer

// Binding
private lateinit var binding: ActivityFakeHomeBinding

class FakeHomeActivity : AppCompatActivity(), Communicator{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFakeHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.black)

//        binding.fragmentContainerView.alpha = 0.5f

        // Full Screen Activity
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window,
            window.decorView.findViewById(R.id.content)).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())

            // When the screen is swiped up at the bottom
            // of the application, the navigationBar shall
            // appear for some time
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        }
    }

    override fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(com.ndr.unlockwithface.R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()
    }



    override suspend fun loadsBitmap(): List<ProfilePhotos> {
        return withContext(Dispatchers.Main){
            val files = filesDir.listFiles()
            files?.filter{it.canRead() && it.isFile && it.name.endsWith(".jpg")}?.map{
                val bytes = it.readBytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ProfilePhotos(it.name, bitmap)
            }?: listOf()
        }
    }

    override fun loadFaces(): MutableList<ProfilePhotos> {
        val profiles : MutableList<ProfilePhotos> = ArrayList()
        val files = filesDir.listFiles()
        files?.filter{it.canRead() && it.isFile && it.name.endsWith(".jpg")}?.map{
            val bytes = it.readBytes()
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val profile = ProfilePhotos(it.name, bitmap)
            profiles.add(profile)
        }?: listOf()
        return profiles
    }

    override fun replaceNavBarId(id: Int) { }
    override fun sendBitmap(fragment: Fragment, data: Bitmap) { }
    override fun saveBitmap(filename: String, bitmap: Bitmap): Boolean { return true }
    override fun deleteBitmap(filename: String) { }

}