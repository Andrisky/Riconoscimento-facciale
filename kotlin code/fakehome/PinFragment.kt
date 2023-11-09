package com.ndr.unlockwithface.fakehome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ndr.unlockwithface.Communicator
import com.ndr.unlockwithface.R
import com.ndr.unlockwithface.databinding.FragmentBlankBinding
import com.ndr.unlockwithface.databinding.FragmentPinBinding
import com.ndr.unlockwithface.settings.SettingsViewModel


class PinFragment : Fragment() {

    // Binding
    private var _binding: FragmentPinBinding?=null
    private val binding get() = _binding!!

    // DataStore
    private lateinit var settingsViewModel: SettingsViewModel

    // Communicator
    private lateinit var communicator: Communicator




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPinBinding.inflate(inflater, container, false)

        // Communicator init
        communicator = activity as Communicator

        // Datastore init
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        binding.editTextTypePin.showSoftInputOnFocus = false

        settingsViewModel.readFromDataStore.observe(viewLifecycleOwner){myPin ->

            binding.buttonConfirmPin.setOnClickListener{
                binding.buttonConfirmPin.animate().apply {
                    duration = 10
                    alpha(0.3f)
                }.withEndAction {
                    binding.buttonConfirmPin.animate().apply {
                        duration = 10
                        alpha(1.0f)
                    }.withEndAction {
                        if(myPin == binding.editTextTypePin.text.toString()) {
                            communicator.replaceFragment(BlankFragment())
                        }else{
                            Toast.makeText(context, "Wrong PIN", Toast.LENGTH_SHORT).show()
                            binding.editTextTypePin.text?.clear()
                        }

                    }
                }
            }
        }

        binding.buttonDelete.setOnClickListener{
            binding.buttonDelete.animate().apply {
                duration = 10
                alpha(0.3f)
            }.withEndAction {
                binding.buttonDelete.animate().apply {
                    duration = 10
                    alpha(1.0f)
                }.withEndAction {
                    var text: String = binding.editTextTypePin.text.toString()
                    if(text.isNotEmpty()){
                        binding.editTextTypePin.setText(text.substring(0, text.length - 1))
                    }

                }
            }
        }

        binding.buttonConfirmPin.setOnClickListener{
            binding.buttonConfirmPin.animate().apply {
                duration = 10
                alpha(0.3f)
            }.withEndAction {
                binding.buttonConfirmPin.animate().apply {
                    duration = 10
                    alpha(1.0f)
                }.withEndAction {


                }
            }
        }


        binding.pin1.setOnClickListener{
            binding.pin1.animate().apply {
                duration = 10
                alpha(0.3f)
            }.withEndAction {
                binding.pin1.animate().apply {
                    duration = 10
                    alpha(1.0f)
                }.withEndAction {
                    binding.editTextTypePin.append("1")

                }
            }
        }

        binding.pin2.setOnClickListener{
            binding.pin2.animate().apply {
                duration = 10
                alpha(0.3f)
            }.withEndAction {
                binding.pin2.animate().apply {
                    duration = 10
                    alpha(1.0f)
                }.withEndAction {
                    binding.editTextTypePin.append("2")
                }
            }
        }

        binding.pin3.setOnClickListener{
            binding.pin3.animate().apply {
                duration = 10
                alpha(0.3f)
            }.withEndAction {
                binding.pin3.animate().apply {
                    duration = 10
                    alpha(1.0f)
                }.withEndAction {
                    binding.editTextTypePin.append("3")
                }
            }
        }

        binding.pin4.setOnClickListener{
            binding.pin4.animate().apply {
                duration = 10
                alpha(0.3f)
            }.withEndAction {
                binding.pin4.animate().apply {
                    duration = 10
                    alpha(1.0f)
                }.withEndAction {
                    binding.editTextTypePin.append("4")
                }
            }
        }
        binding.pin5.setOnClickListener{
            binding.pin5.animate().apply {
                duration = 10
                alpha(0.3f)
            }.withEndAction {
                binding.pin5.animate().apply {
                    duration = 10
                    alpha(1.0f)
                }.withEndAction {
                    binding.editTextTypePin.append("5")
                }
            }
        }

        binding.pin6.setOnClickListener{
            binding.pin6.animate().apply {
                duration = 10
                alpha(0.3f)
            }.withEndAction {
                binding.pin6.animate().apply {
                    duration = 10
                    alpha(1.0f)
                }.withEndAction {
                    binding.editTextTypePin.append("6")
                }
            }
        }

        binding.pin7.setOnClickListener{
            binding.pin7.animate().apply {
                duration = 10
                alpha(0.3f)
            }.withEndAction {
                binding.pin7.animate().apply {
                    duration = 10
                    alpha(1.0f)
                }.withEndAction {
                    binding.editTextTypePin.append("7")
                }
            }
        }

        binding.pin8.setOnClickListener{
            binding.pin8.animate().apply {
                duration = 10
                alpha(0.3f)
            }.withEndAction {
                binding.pin8.animate().apply {
                    duration = 10
                    alpha(1.0f)
                }.withEndAction {
                    binding.editTextTypePin.append("8")
                }
            }
        }

        binding.pin9.setOnClickListener{
            binding.pin9.animate().apply {
                duration = 10
                alpha(0.3f)
            }.withEndAction {
                binding.pin9.animate().apply {
                    duration = 10
                    alpha(1.0f)
                }.withEndAction {
                    binding.editTextTypePin.append("9")
                }
            }
        }

        binding.pin0.setOnClickListener{
            binding.pin0.animate().apply {
                duration = 10
                alpha(0.3f)
            }.withEndAction {
                binding.pin0.animate().apply {
                    duration = 10
                    alpha(1.0f)
                }.withEndAction {
                    binding.editTextTypePin.append("0")
                }
            }
        }


        return binding.root
    }



}