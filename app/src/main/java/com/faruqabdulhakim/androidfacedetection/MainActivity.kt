package com.faruqabdulhakim.androidfacedetection

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.faruqabdulhakim.androidfacedetection.databinding.ActivityMainBinding
import java.io.File
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT_CODE) {
            val photoFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra(EXTRA_PHOTO, File::class.java)
            } else {
                @Suppress("Deprecation")
                it.data?.getSerializableExtra(EXTRA_PHOTO)
            } as? File

            photoFile?.let {
                binding.imageView.setImageBitmap(BitmapFactory.decodeFile(photoFile.path))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            binding.btnCamera.isEnabled = false
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_PERMISSIONS_CODE
            )
        } else {
            binding.btnCamera.isEnabled = true
        }

        binding.btnCamera.setOnClickListener {
            val cameraIntent = Intent(this@MainActivity, CameraActivity::class.java)
            launcherIntentCameraX.launch(cameraIntent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!allPermissionsGranted()) {
            binding.btnCamera.isEnabled = false
            showToast("Permissions not granted!")
        } else {
            binding.btnCamera.isEnabled = true
            showToast("Permissions granted!")
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(applicationContext, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val CAMERA_X_RESULT_CODE = 100
        const val EXTRA_PHOTO = "extra_photo"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private val REQUEST_PERMISSIONS_CODE = 123
    }
}