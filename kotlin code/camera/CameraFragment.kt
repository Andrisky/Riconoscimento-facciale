package com.ndr.unlockwithface.camera

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.ndr.unlockwithface.Communicator
import com.ndr.unlockwithface.R
import com.ndr.unlockwithface.databinding.FragmentCameraBinding
import com.ndr.unlockwithface.settings.SettingsViewModel
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

const val LENGTH_PIN : Int = 6
const val title : String = "Missing PIN"
const val message: String = "Before starting, it is mandatory to set the security PIN. As long as the security PIN is not set the camera cannot be used." +
        "\n\nBy clicking on the \"OK\" button you will be redirected to the settings page to set the security PIN."

class CameraFragment : Fragment() {
    // Binding
    private var _binding: FragmentCameraBinding?=null
    private val binding get() = _binding!!

    // DataStore
    private lateinit var settingsViewModel: SettingsViewModel

    // Communicator
    private lateinit var communicator: Communicator

    //    Image capture
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor : ExecutorService

    // Context
    private lateinit var safeContext: Context

    // FaceDetector
    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .build()
    private val detector = FaceDetection.getClient(options)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        communicator = activity as Communicator
        binding.buttonTakePhoto.visibility = View.GONE

        // ActionBar name
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Camera"

        // Datastore init
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        // Handling camera starting
        settingsViewModel.readFromDataStore.observe(viewLifecycleOwner){myPin ->
//            Toast.makeText(context, myPin, Toast.LENGTH_LONG).show()
            if(myPin == "none"){
//                binding.buttonTakePhoto.visibility = View.GONE

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

                titleTextView.text = title
                messageTextView.text = message

                alertButton.setOnClickListener {
                    communicator.replaceNavBarId(R.id.nav_settings)
                    alert.dismiss()
                }

            }else {
                startCamera()
                binding.buttonTakePhoto.visibility = View.VISIBLE

                binding.buttonTakePhoto.setOnClickListener {
                    binding.buttonTakePhoto.animate().apply {
                        duration = 10
                        alpha(0.3f)
                    }.withEndAction{
                        binding.buttonTakePhoto.animate().apply {
                            duration = 10
                            alpha(1.0f)
                        }.withEndAction{
                            takePhoto()
                        }
                    }

                }
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()


        return binding.root
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()

            // Select front camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
//                cameraProvider.bindToLifecycle(this, cameraSelector, useCaseGroup)

            } catch(exc: Exception) {
                Log.e("error", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(safeContext))
    }

    private fun takePhoto() {

        // Foreground Animation
        binding.root.postDelayed({
            binding.root.foreground = ColorDrawable(Color.WHITE)
            binding.root.postDelayed({
                binding.root.foreground = null
            }, 50)
        }, 100)


//        val imageCapture = imageCapture ?: return

        imageCapture?.takePicture(ContextCompat.getMainExecutor(safeContext), object :
            ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {

                // ImageProxy2Bitmap
                val planeProxy = image.planes[0]
                val buffer : ByteBuffer = planeProxy.buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                val bitmap : Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                // Rotate Bitmap
                val matrix = Matrix()
                matrix.postRotate(90f)
                matrix.postScale(1F, -1F)
                val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                // Alert dialog
                binding.viewFinder.visibility = View.INVISIBLE
//                binding.viewFinder.alpha = 0.5F
                binding.buttonTakePhoto.visibility = View.INVISIBLE
                val dialogBinding = layoutInflater.inflate(R.layout.alert_preview, null)
                val alert = Dialog(safeContext)
                alert.setContentView(dialogBinding)
                alert.setCancelable(false)
                alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                alert.show()

                // Binding
                val imageView = dialogBinding.findViewById<ShapeableImageView>(R.id.previewShapeableImage)
                val buttonBack = dialogBinding.findViewById<Button>(R.id.buttonPreviewBack)
                val buttonSave = dialogBinding.findViewById<Button>(R.id.buttonPreviewSave)
                val pin = dialogBinding.findViewById<TextInputEditText>(R.id.previewPin)
                val pinLayout = dialogBinding.findViewById<TextInputLayout>(R.id.previewPinLayout)
                val titlePreview = dialogBinding.findViewById<TextView>(R.id.previewTitle)
                val messagePreview = dialogBinding.findViewById<TextView>(R.id.previewMessage)

                titlePreview.text = "Save"
                messagePreview.text = "Save the picture just made?"

                imageView.setImageBitmap(rotatedBitmap)

                // Discard Picture and refresh camera fragment
                buttonBack.setOnClickListener {
                    communicator.replaceFragment(CameraFragment())
                    alert.dismiss()
                }

                // Handling condition  to save Picture
                settingsViewModel.readFromDataStore.observe(viewLifecycleOwner){myPin ->
                    buttonSave.setOnClickListener {
                        if(pin.text!!.length < LENGTH_PIN){
                            pinLayout.error = "Wrong security PIN (security PIN must contain 6 numbers)"
                        }
                        else if(pin.text.toString() != myPin.toString()){
                            pinLayout.error = "Wrong security PIN"
                        }
                        else if (pin.text.toString() == myPin.toString()){

                            // Detect face
                            val image = InputImage.fromBitmap(rotatedBitmap, 0)
                            detector.process(image)
                                .addOnSuccessListener { faces ->
                                    if(faces.size == 1){
                                        // Crop and resize the detected face
                                        val bounds = faces[0].boundingBox

                                        val x = bounds.left.coerceAtLeast(0)
                                        val y = bounds.top.coerceAtLeast(0)
                                        val w = bounds.width()
                                        val h = bounds.height()

                                        val croppedFace = Bitmap.createBitmap(
                                            rotatedBitmap,
                                            x,
                                            y,
                                            if (x + w > rotatedBitmap.width) rotatedBitmap.width - x else w,
                                            if (y + h > rotatedBitmap.height) rotatedBitmap.height - y else h
                                        )
                                        val resizedFace : Bitmap = Bitmap.createScaledBitmap(croppedFace, 105,105, true)
                                        // SAVE PICTURE
                                        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis())
                                        communicator.saveBitmap(name, resizedFace) // rotatedBitmap
                                        // Replace Fragment
                                        communicator.replaceNavBarId(R.id.nav_profiles)
                                        alert.dismiss()

                                    }else if (faces.size == 0){
                                        pinLayout.error = "No face detected"
                                    }else if(faces.size > 1){
                                        pinLayout.error = "More than one face detected"
                                    }

                                }
                                .addOnFailureListener{ e ->
                                    Log.e("error", "Error", e)
                                }
                        }

                        // Handling input errors
                        pin.doOnTextChanged { text, _, _, _ ->
                            if(text!!.toString() != myPin || text!!.length < LENGTH_PIN){
                                pinLayout.error = null
                            }
                        }
                    }
                }

                super.onCaptureSuccess(image)
            }

        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }








}