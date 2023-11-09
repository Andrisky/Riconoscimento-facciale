package com.ndr.unlockwithface.fakehome


import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.ndr.unlockwithface.Communicator
import com.ndr.unlockwithface.databinding.FragmentFaceRecognitionBinding
import com.ndr.unlockwithface.ml.EmbeddingModelV0
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class FaceRecognitionFragment : Fragment(), ImageAnalysis.Analyzer{

    // Binding
    private var _binding: FragmentFaceRecognitionBinding?=null
    private val binding get() = _binding!!

    // Communicator
    private lateinit var communicator: Communicator

    // Context
    private lateinit var safeContext: Context

    // Image capture
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor : ExecutorService


    // Face detector
    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .build()
    private val detector = FaceDetection.getClient(options)

    // Anchor list
    private val anchors: MutableList<Bitmap> = ArrayList()

    // Feature list
    private val features : MutableList<FloatArray> = ArrayList()

    // Model
    private lateinit var model: EmbeddingModelV0



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFaceRecognitionBinding.inflate(inflater, container, false)

        // Init communicator
        communicator = activity as Communicator


        // Init model
        model = EmbeddingModelV0.newInstance(safeContext)

        // Populating of the anchor's list
        val pictures = communicator.loadFaces()
        for(pic in pictures){
            anchors.add(pic.bitmap)
        }

        // Extracting features from the anchors
        for(btp in anchors){
            val feature = computeFeatures(model, btp)
            features.add(feature)
        }


        //PIN
        binding.buttonPin.setOnClickListener{
            binding.buttonPin.animate().apply {
                duration = 10
                alpha(0.3f)
            }.withEndAction{
                binding.buttonPin.animate().apply {
                    duration = 10
                    alpha(1.0f)
                }.withEndAction{
                    communicator.replaceFragment(PinFragment())
                }
            }
        }

        startCamera()
        cameraExecutor = Executors.newSingleThreadExecutor()


        return binding.root
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, this)
                }

            // Select front camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalysis)//, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e("error", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(safeContext))
    }


    override fun analyze(imageProxy: ImageProxy) {

//        val mediaImage = imageProxy.image
        var bitmap = imageProxy.toBitmap()

        // Rotate Bitmap
        val matrix = Matrix()
        matrix.postRotate(90f)
        matrix.postScale(1F, -1F)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        val image = InputImage.fromBitmap(bitmap, 0)
        detector.process(image)
            .addOnSuccessListener { faces ->
                Log.d("facesSize", faces.size.toString())
                // Init model
//                val model = EmbeddingModelV0.newInstance(safeContext)

                if(faces.size == 1){
                    val bounds = faces[0].boundingBox

                    // Crop the  detected face
                    val x = bounds.left.coerceAtLeast(0)
                    val y = bounds.top.coerceAtLeast(0)
                    val w = bounds.width()
                    val h = bounds.height()

                    val croppedFace = Bitmap.createBitmap(
                        bitmap,
                        x, y,
                        if (x + w > bitmap.width) bitmap.width - x else w,
                        if (y + h > bitmap.height) bitmap.height - y else h
                    )

                    //Resize the cropped face
                    val resizedFace : Bitmap = Bitmap.createScaledBitmap(croppedFace, 105,105, true)
                    val shownResizedFace : Bitmap = Bitmap.createScaledBitmap(croppedFace, 300,300, true)

                    binding.facePreview.visibility = View.VISIBLE
                    binding.facePreview.setImageBitmap(shownResizedFace)

                    // Face recognition
                    val input = computeFeatures(model, resizedFace)
//                    val anchor = computeFeatures(model, anchors[0])

//                    val distance = distance(anchor, input)

                    for(anchor in features){
                        val distance =distance(anchor, input)
                        Log.d("distance", distance.toString())
                        binding.textViewDistance.visibility = View.VISIBLE
                        binding.textViewDistance.text = distance.toString()
                        if(distance < 150){
                            communicator.replaceFragment(BlankFragment())
                            break
                        }
                    }


                }else{
                    binding.textViewDistance.visibility = View.INVISIBLE
                    binding.facePreview.visibility = View.INVISIBLE
                }

            }
            .addOnFailureListener {e->
                Log.e("faces", "Face analysis fail.", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun ImageProxy.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val uBuffer = planes[1].buffer // U
        val vBuffer = planes[2].buffer // V

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun computeFeatures(model: EmbeddingModelV0, bitmap : Bitmap): FloatArray {

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 105, 105, 3), DataType.FLOAT32)

        // Create byte Buffer
        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        var byteBuffer = tensorImage.buffer
        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Return an array of 4096 elements
        return outputFeature0.floatArray

    }

    private fun distance(anchor: FloatArray, input : FloatArray) : Double{
        var distance = 0.0
        for(i in anchor.indices){
            distance +=(anchor[i] - input[i])*(anchor[i] - input[i])
        }
        return distance
    }
}


