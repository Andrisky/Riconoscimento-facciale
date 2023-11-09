package com.ndr.unlockwithface.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.ndr.unlockwithface.Communicator
import com.ndr.unlockwithface.R
import com.ndr.unlockwithface.databinding.FragmentSettingsBinding

const val LENGTH_PIN : Int = 6
class SettingsFragment : Fragment() {

    // Binding
    private var _binding: FragmentSettingsBinding?=null
    private val binding get() = _binding!!

    // DataStore
    private lateinit var settingsViewModel: SettingsViewModel

    // Communicator
    private lateinit var communicator: Communicator




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Communicator
        communicator = activity as Communicator

        // Datastore init
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        // ActionBar name
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Settings"

        // Handle the visibility of the EditText Current PIN
        binding.editTextCurrentPin.visibility = View.INVISIBLE
        binding.currentPinLayout.visibility = View.INVISIBLE

        settingsViewModel.readFromDataStore.observe(viewLifecycleOwner){myPin ->
            if(myPin != "none"){
                binding.editTextCurrentPin.visibility = View.VISIBLE
                binding.currentPinLayout.visibility = View.VISIBLE

                //Input errors
                binding.editTextCurrentPin.doOnTextChanged { text, _, _, _ ->
                    if(text.toString() != myPin){
                        binding.currentPinLayout.error = null
                    }
                    if(text!!.length < LENGTH_PIN){
                        binding.currentPinLayout.error = null
                    }
                }
            }

        }

        // Inputs Error
        binding.editTextPin.doOnTextChanged { text, _, _, _ ->
            if(text!!.length < LENGTH_PIN){
                binding.pinLayout.error =  null
            }
        }

        binding.editTextConfirmPin.doOnTextChanged { text, _, _, _ ->
            if(text!!.length < LENGTH_PIN){
                binding.confirmPinLayout.error = null
            }
        }

        // Save PIN
        binding.buttonSave.setOnClickListener { savePin() }


        return binding.root
    }

    private fun savePin() {
        // EditText binding
        val currentPin = binding.editTextCurrentPin.text
        val pin = binding.editTextPin.text
        val confirmPin = binding.editTextConfirmPin.text

        // DataStore handling
        settingsViewModel.readFromDataStore.observe(viewLifecycleOwner){myPin ->
            // myPin == pin
            if(myPin != "none"){
                if(currentPin!!.length < LENGTH_PIN){
                    binding.currentPinLayout.error = "PIN must contain 6 numbers"
                }else if(currentPin.toString() != myPin){
                    binding.currentPinLayout.error = "Wrong current pin!"
                }

                if(pin!!.length< LENGTH_PIN){
                    binding.pinLayout.error = "PIN must contain 6 numbers"
                }
                if(confirmPin!!.length< LENGTH_PIN){
                    binding.confirmPinLayout.error = "PIN must contain 6 numbers"
                }
                if (confirmPin!!.length == LENGTH_PIN && confirmPin.toString() != pin.toString()){
                    binding.confirmPinLayout.error = "PINs typed do not match"
                }

                // save condition
                if(pin.toString() == confirmPin.toString() && currentPin.toString() == myPin && pin!!.length == LENGTH_PIN){
                    settingsViewModel.saveToDataStore(pin.toString())
                    // clean EditText
                    pin.clear()
                    confirmPin.clear()
                    currentPin.clear()

                    //Replace Fragment
                    communicator.replaceNavBarId(R.id.nav_camera)


                }
//          myPin == none
            }else{

                if(pin!!.length < LENGTH_PIN) {
                    binding.pinLayout.error = "PIN must contain 6 numbers"
                }
                if(confirmPin!!.length < LENGTH_PIN)
                    binding.confirmPinLayout.error = "PIN must contain 6 numbers"
                if(confirmPin!!.length == LENGTH_PIN && confirmPin.toString() != pin.toString()){
                    binding.confirmPinLayout.error = "PINs typed do not match"
                }
                // Save condition
                if(pin.toString() == confirmPin.toString() && confirmPin.length == LENGTH_PIN && pin.length == LENGTH_PIN){
                    settingsViewModel.saveToDataStore(pin.toString())
                    // clean EditText
                    pin.clear()
                    confirmPin.clear()

                    //Replace Fragment
                    communicator.replaceNavBarId(R.id.nav_camera)


                }

            }

//            Toast.makeText(context, myPin, Toast.LENGTH_SHORT).show()
        }

    }

}