package com.ndr.unlockwithface.camera

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ndr.unlockwithface.Communicator
import com.ndr.unlockwithface.databinding.FragmentPreviewBinding


class PreviewFragment : Fragment() {

    //  Binding
    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!

    // Communicator
    private lateinit var communicator: Communicator

    // Bitmap
    private var bitmap : Bitmap?= null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        communicator = activity as Communicator

        bitmap = arguments?.getParcelable("bitmap")
        binding.previewImage.setImageBitmap(bitmap)


        return binding.root
    }


}