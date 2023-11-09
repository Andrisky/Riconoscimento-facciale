package com.ndr.unlockwithface.fakehome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ndr.unlockwithface.Communicator
import com.ndr.unlockwithface.databinding.FragmentBlankBinding


class BlankFragment : Fragment() {
    // Binding
    private var _binding: FragmentBlankBinding?=null
    private val binding get() = _binding!!

    // Communicator
    private lateinit var communicator: Communicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBlankBinding.inflate(inflater, container, false)

        // Communicator init
        communicator = activity as Communicator

        Toast.makeText(context, "Device Unlocked", Toast.LENGTH_SHORT).show()

        binding.buttonLock.setOnClickListener{
            binding.buttonLock.animate().apply {
                duration = 10
                alpha(0.3f)
            }.withEndAction {
                binding.buttonLock.animate().apply {
                    duration = 10
                    alpha(1.0f)
                }.withEndAction {
                    communicator.replaceFragment(FaceRecognitionFragment())
                }
            }
        }

        return binding.root



    }


}