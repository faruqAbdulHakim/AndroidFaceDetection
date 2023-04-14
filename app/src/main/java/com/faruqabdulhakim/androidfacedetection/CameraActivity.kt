package com.faruqabdulhakim.androidfacedetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.faruqabdulhakim.androidfacedetection.databinding.ActivityCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var overlay: Overlay
    private lateinit var faceAnalyzer: FaceAnalyzer
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        face overlay
        overlay = Overlay(this)
        overlay.setOrientation(resources.configuration.orientation)
        val layoutOverlay = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        addContentView(overlay, layoutOverlay)

        faceAnalyzer = FaceAnalyzer(lifecycle, overlay)
        faceAnalyzer.faceDetected.observe(this@CameraActivity) {faceDetected ->
            binding.btnTakePhoto.isEnabled = faceDetected == 1
            if (faceDetected == 0) {
                binding.tvMsg.text = "Tidak ada wajah terdeteksi"
            } else if (faceDetected == 1) {
                binding.tvMsg.text = "Wajah terdeteksi"
            } else if (faceDetected > 1) {
                binding.tvMsg.text = "Terlalu banyak wajah yang terdeteksi"
            }
        }

        startCamera()

        binding.btnTakePhoto.setOnClickListener { takePhoto() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.previewView.surfaceProvider)

            val imageAnalysis = ImageAnalysis.Builder().build()
            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(this@CameraActivity), faceAnalyzer
            )

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this@CameraActivity,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )
            } catch (e: Exception) {
                showToast("Gagal membuka kamera")
            }
        }, ContextCompat.getMainExecutor(this@CameraActivity))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val outputDirectory = getOutputDirectory(application)
        val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
            .format(System.currentTimeMillis()) + ".jpg"

        val photoFile = File(outputDirectory, fileName)

        val outputOption = ImageCapture
            .OutputFileOptions
            .Builder(photoFile)
            .build()

        imageCapture.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this@CameraActivity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    showToast("Gagal mengambil gambar")
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    showToast("Berhasil mengambil gambar")
                    val intent = Intent()
                    intent.putExtra(MainActivity.EXTRA_PHOTO, photoFile)
                    setResult(MainActivity.CAMERA_X_RESULT_CODE, intent)
                    finish()
                }
            }
        )
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@CameraActivity, msg, Toast.LENGTH_SHORT).show()
    }

}