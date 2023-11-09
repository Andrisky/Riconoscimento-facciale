package com.ndr.unlockwithface.profiles

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.ndr.unlockwithface.Communicator
import com.ndr.unlockwithface.R
import com.ndr.unlockwithface.camera.LENGTH_PIN
import com.ndr.unlockwithface.databinding.FragmentProfilesBinding
import com.ndr.unlockwithface.settings.SettingsViewModel
import kotlinx.coroutines.launch


const val titleDelete : String = "Delete"
const val messageDelete : String = "Delete the selected picture?"
const val titleMissingPIN : String = "Missing PIN"
const val messageMissingPIN : String = "Before starting, it is mandatory to set the security PIN. As long as the security PIN is not set the profiles cannot be shown." +
        "\n\nBy clicking on the \"OK\" button you will be redirected to the settings page to set the security PIN."

class ProfilesFragment : Fragment() {
    // Binding
    private var _binding:FragmentProfilesBinding?=null
    private val binding get() = _binding!!

    // Communicator
    private lateinit var communicator: Communicator

    // Context
    private lateinit var safeContext: Context

    // RecyclerView Adapter
    private lateinit var profilePhotosAdapter : ProfilePhotosAdapter

    // DataStore
    private lateinit var settingsViewModel: SettingsViewModel





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfilesBinding.inflate(inflater, container, false)
        communicator = activity as Communicator

        // Datastore init
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        // ActionBar name
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Profiles"

        // Handling Recycler View Visibility
        settingsViewModel.readFromDataStore.observe(viewLifecycleOwner){myPin ->
            if(myPin == "none"){
                binding.rv.visibility = View.INVISIBLE

                // Alert
                val dialogBinding = layoutInflater.inflate(R.layout.alert_missing_pin, null)
                val alert = Dialog(safeContext)
                alert.setContentView(dialogBinding)
                alert.setCancelable(false)
                alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                alert.show()

                // Binding
                val titleTextView = dialogBinding.findViewById<TextView>(R.id.alertTitle)
                val messageTextView = dialogBinding.findViewById<TextView>(R.id.alertMessage)
                val alertButton = dialogBinding.findViewById<Button>(R.id.alertButton)

                titleTextView.text = titleMissingPIN
                messageTextView.text = messageMissingPIN

                alertButton.setOnClickListener {
                    communicator.replaceNavBarId(R.id.nav_settings)
                    alert.dismiss()
                }
            }
        }

        // Handling setOnClickLister on items
        profilePhotosAdapter = ProfilePhotosAdapter(safeContext){ it ->

            val currentItem = it.name

            val dialogBinding = layoutInflater.inflate(R.layout.alert_delete, null)
            val alert = Dialog(safeContext)
            alert.setContentView(dialogBinding)
            alert.setCancelable(false)
            alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alert.show()

            // Binding
            val buttonYes = dialogBinding.findViewById<Button>(R.id.buttonYes)
            val buttonNo = dialogBinding.findViewById<Button>(R.id.buttonNo)
            val titleAlertDelete = dialogBinding.findViewById<TextView>(R.id.titleDelete)
            val messageAlertDelete = dialogBinding.findViewById<TextView>(R.id.messageDelete)
            val pin = dialogBinding.findViewById<TextInputEditText>(R.id.deletePin)
            val pinLayout = dialogBinding.findViewById<TextInputLayout>(R.id.deletePinLayout)

            titleAlertDelete.text = titleDelete
            messageAlertDelete.text = messageDelete

            buttonNo.setOnClickListener {
                alert.dismiss()
            }
            // Handling condition  to save Picture
            settingsViewModel.readFromDataStore.observe(viewLifecycleOwner){myPin ->
                buttonYes.setOnClickListener {
                    if(pin.text!!.length < LENGTH_PIN){
                        pinLayout.error = "Wrong security PIN (security PIN must contain 6 numbers)"
                    }
                    else if(pin.text.toString() != myPin.toString()){
                        pinLayout.error = "Wrong security PIN"
                    }
                    else if (pin.text.toString() == myPin.toString()){
                        // Delete item
                        communicator.deleteBitmap(currentItem)
                        // Refresh after delete
                        lifecycleScope.launch{
                            val photos = communicator.loadsBitmap()
                            profilePhotosAdapter.submitList(photos)
                        }
                        alert.dismiss()
                    }

                    // Handling input errors
                    pin.doOnTextChanged { text, _, _, _ ->
                        if(text!!.toString() != myPin || text!!.length < LENGTH_PIN){
                            pinLayout.error = null
                        }
                    }
                }
            }

        }

        // RecyclerView
        binding.rv.apply {
            adapter = profilePhotosAdapter
            layoutManager = GridLayoutManager(context, 3)
        }

        // Load Bitmap
        lifecycleScope.launch{
            val photos = communicator.loadsBitmap()
            profilePhotosAdapter.submitList(photos)
        }

        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }






}