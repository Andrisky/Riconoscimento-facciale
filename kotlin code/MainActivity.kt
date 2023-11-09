package com.ndr.unlockwithface

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ndr.unlockwithface.camera.CameraFragment
import com.ndr.unlockwithface.databinding.ActivityMainBinding
import com.ndr.unlockwithface.profiles.ProfilePhotos
import com.ndr.unlockwithface.profiles.ProfilesFragment
import com.ndr.unlockwithface.settings.SettingsFragment
import com.ndr.unlockwithface.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : AppCompatActivity(), Communicator {

    // Binding
    private lateinit var binding: ActivityMainBinding

    // DataStore
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Request camera permissions
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // First fragment shown
        replaceNavBarId(R.id.nav_about)
        // Datastore init
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]


        binding.bottomNavBar.setOnItemSelectedListener {
            when (it) {
                // Fragments
                R.id.nav_camera -> {
                    // Replace fragment
                    replaceFragment(CameraFragment())
                }
                R.id.nav_profiles -> {
                    // Replace fragment
                    replaceFragment(ProfilesFragment())
                }
                R.id.nav_settings -> {
                    // Replace fragment
                    replaceFragment(SettingsFragment())
                }
                R.id.nav_about -> {
                    // Replace fragment
                    replaceFragment(AboutFragment())
                }
            }
        }

    }


    override fun replaceNavBarId(id: Int) {
        binding.bottomNavBar.setItemSelected(id)
    }

    override fun replaceFragment(fragment: Fragment) {
        binding.fragmentContainer.visibility = View.VISIBLE

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    override fun sendBitmap(fragment: Fragment, data: Bitmap) {
        val bundle = Bundle()
        bundle.putParcelable("bitmap", data)
        fragment.arguments = bundle
        replaceFragment(fragment)
    }

    override fun saveBitmap(filename: String, bitmap: Bitmap): Boolean {
        return try {
            openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                    throw IOException("Bitmap doesn't save")
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun loadsBitmap(): List<ProfilePhotos> {
        return withContext(Dispatchers.IO) {
            val files = filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ProfilePhotos(it.name, bitmap)
            } ?: listOf()
        }
    }

    override fun deleteBitmap(filename: String) {
        try {
            deleteFile(filename)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun loadFaces(): MutableList<ProfilePhotos> {
        val profiles: MutableList<ProfilePhotos> = ArrayList()
        val files = filesDir.listFiles()
        files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
            val bytes = it.readBytes()
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val profile = ProfilePhotos(it.name, bitmap)
            profiles.add(profile)
        } ?: listOf()
        return profiles
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }


}



