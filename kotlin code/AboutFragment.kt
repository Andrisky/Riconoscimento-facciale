package com.ndr.unlockwithface


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ndr.unlockwithface.databinding.FragmentAboutBinding
import com.ndr.unlockwithface.settings.SettingsFragment
import com.ndr.unlockwithface.settings.SettingsViewModel

const val about: String = "This application has been developed as a project" +
        " for the Digital Systems course at the University of Bologna by Andrea Lo Russo. " +
        "Its purpose, purely academic, is to perform facial recognition in embedded systems such as smartphones..." +
        "\n\n\nBefore starting, it is mandatory to provide permissions to allow the application to access " +
        "the device's front camera and set the security code in the Settings section... " +
        "\n\n\nOnce the permission has been granted and the code set, " +
        "it will be possible to access the Camera section to capture the face you wish to recognize: " +
        "faces captured in the Camera section can be managed within the Profiles section. "

class AboutFragment : Fragment() {

    // Binding
    private var _binding: FragmentAboutBinding?=null
    private val binding get() = _binding!!

    // Communicator
    private lateinit var communicator: Communicator

    // DataStore
    private lateinit var settingsViewModel: SettingsViewModel





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        communicator = activity as Communicator

        // Pictures in the rv
        val pictures = communicator.loadFaces()

        // ActionBar name
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "About"

        // About
        binding.aboutAbstract.text = about

        // Datastore init
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        // Resetting PIN and Simulating
        settingsViewModel.readFromDataStore.observe(viewLifecycleOwner) { myPin ->

            if(myPin != "none"){

                binding.aboutTitle.setOnLongClickListener{
                    settingsViewModel.saveToDataStore("none")
                    Toast.makeText(context, "The pin has been reset", Toast.LENGTH_SHORT).show()
                    communicator.replaceNavBarId(R.id.nav_settings)
                    true

                }
            }
            if(myPin!="none" && pictures.size > 0){

                binding.aboutTitle.setOnClickListener {
                    binding.aboutTitle.animate().apply {
                        duration = 10
                        alpha(0.3f)
                    }.withEndAction{
                        binding.aboutTitle.animate().apply {
                            duration = 10
                            alpha(1.0f)
                        }.withEndAction{
                            Intent(activity, FakeHomeActivity::class.java).apply {
                                startActivity(this)
                            }
                        }
                    }

                }
            }

        }

        return binding.root
    }

}


