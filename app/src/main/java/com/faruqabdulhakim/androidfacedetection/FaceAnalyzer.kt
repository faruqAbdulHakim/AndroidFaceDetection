package com.faruqabdulhakim.androidfacedetection

import android.graphics.Matrix
import android.media.Image
import android.util.Log
import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.Lifecycle
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceAnalyzer(
    lifecycle: Lifecycle,
    private val overlay: Overlay
) : ImageAnalysis.Analyzer {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(0.2F)
        .enableTracking()
        .build()

    private val detector = FaceDetection.getClient(options)

    init {
        lifecycle.addObserver(detector)
    }

    override fun analyze(imageProxy: ImageProxy) {
        overlay.setPreviewSize(Size(imageProxy.width, imageProxy.height))
        
//        detect faces
        @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
        val image = InputImage.fromMediaImage(
            imageProxy.image as Image,
            imageProxy.imageInfo.rotationDegrees
        )
        detector.process(image)
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener)
            .addOnCompleteListener { imageProxy.close() }
    }

    private val successListener = OnSuccessListener<List<Face>> { faces ->
        Log.d(TAG, "Number of face detected: " + faces.size)
        overlay.setFaces(faces)
    }

    private val failureListener = OnFailureListener { e ->
        Log.e(TAG, "Face analysis failure.", e)
    }


    companion object {
        private val TAG = FaceAnalyzer::class.java.simpleName
    }
}