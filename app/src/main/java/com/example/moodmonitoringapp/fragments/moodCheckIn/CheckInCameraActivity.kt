package com.example.moodmonitoringapp.fragments.moodCheckIn

import android.Manifest
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.example.moodmonitoringapp.databinding.ActivityCheckInCameraBinding
import com.example.moodmonitoringapp.fragments.InsetsHelper
import com.example.moodmonitoringapp.fragments.loginSignUp.OtpActivity
import com.example.moodmonitoringapp.util.values.IMAGE_PATH
import com.example.moodmonitoringapp.util.values.REQUEST_CAMERA_CODE
import kotlinx.android.synthetic.main.activity_check_in_camera.*
import java.io.File
import java.io.IOException
import java.util.*

class CheckInCameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckInCameraBinding
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageCapture: ImageCapture
    lateinit var currentPhotoPath: String
    private var isBackCamera = true

    private val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckInCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.rootViewCamera.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        InsetsHelper.handleBottom(true, photoButton)

        init()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                    initCamera()
                } else {
                    showNoPermissions()
                }
            }
            else -> {
                showNoPermissions()
            }
        }
    }

    private fun init() {
        if (hasPermissions()) {
            initCamera()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CAMERA_CODE)
        }

        initListeners()
    }

    private fun initListeners() {
        photoButton.setOnClickListener {
            val filename = "${Date().time.toString(16)}.jpeg"
            val file = File(externalMediaDirs.first(), filename)
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            imageCapture.takePicture(outputFileOptions, { it.run() },
                object: ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        openImage(file.toUri())
                    }

                    override fun onError(exception: ImageCaptureException) {
                        runOnUiThread {
                            Toast.makeText(this@CheckInCameraActivity, "Ошибка при сохранении изображения", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }

    /*@RequiresApi(Build.VERSION_CODES.N)
    private fun initListeners(): File{
        binding.photoButton.setOnClickListener {
            // Create an image file name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile(
                "JPEG_${timeStamp}_", *//* prefix *//*
                ".jpg", *//* suffix *//*
                storageDir *//* directory *//*
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                currentPhotoPath = absolutePath
            }
        }

    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    openImage(file.toUri())
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Toast.makeText(this@CheckInCameraActivity, "Error Occurred", Toast.LENGTH_SHORT).show()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }*/


    private fun initCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(if (isBackCamera) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT)
            .build()

        imageCapture = ImageCapture.Builder()
            .setTargetResolution(Size(720, 1280))
            .setTargetRotation(binding.previewView.display.rotation)
            .build()

        val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        preview.setSurfaceProvider(previewView.surfaceProvider)
        observeCameraState(camera.cameraInfo)
    }

    private fun showNoPermissions() {
        binding.previewView.isVisible = false
        binding.noPermissionLayout.isVisible = true
    }

    private fun openImage(path: Uri) {
        val intent = Intent(this@CheckInCameraActivity, OtpActivity::class.java)
        intent.putExtra(IMAGE_PATH, path)
        startActivity(intent)
        finish()
    }

    private fun hasPermissions() = PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun observeCameraState(cameraInfo: CameraInfo) {
        cameraInfo.cameraState.observe(this) { cameraState ->
            run {
                when (cameraState.type) {
                    CameraState.Type.PENDING_OPEN -> {
                        // Ask the user to close other camera apps
                        Toast.makeText(this,
                            "CameraState: Pending Open",
                            Toast.LENGTH_SHORT).show()
                    }
                    CameraState.Type.OPENING -> {
                        // Show the Camera UI
                        Toast.makeText(this,
                            "CameraState: Opening",
                            Toast.LENGTH_SHORT).show()
                    }
                    CameraState.Type.OPEN -> {
                        // Setup Camera resources and begin processing
                        Toast.makeText(this,
                            "CameraState: Open",
                            Toast.LENGTH_SHORT).show()
                    }
                    CameraState.Type.CLOSING -> {
                        // Close camera UI
                        Toast.makeText(this,
                            "CameraState: Closing",
                            Toast.LENGTH_SHORT).show()
                    }
                    CameraState.Type.CLOSED -> {
                        // Free camera resources
                        Toast.makeText(this,
                            "CameraState: Closed",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }

            cameraState.error?.let { error ->
                when (error.code) {
                    // Open errors
                    CameraState.ERROR_STREAM_CONFIG -> {
                        // Make sure to setup the use cases properly
                        Toast.makeText(this,
                            "Stream config error",
                            Toast.LENGTH_SHORT).show()
                    }
                    // Opening errors
                    CameraState.ERROR_CAMERA_IN_USE -> {
                        // Close the camera or ask user to close another camera app that's using the
                        // camera
                        Toast.makeText(this,
                            "Camera in use",
                            Toast.LENGTH_SHORT).show()
                    }
                    CameraState.ERROR_MAX_CAMERAS_IN_USE -> {
                        // Close another open camera in the app, or ask the user to close another
                        // camera app that's using the camera
                        Toast.makeText(this,
                            "Max cameras in use",
                            Toast.LENGTH_SHORT).show()
                    }
                    CameraState.ERROR_OTHER_RECOVERABLE_ERROR -> {
                        Toast.makeText(this,
                            "Other recoverable error",
                            Toast.LENGTH_SHORT).show()
                    }
                    // Closing errors
                    CameraState.ERROR_CAMERA_DISABLED -> {
                        // Ask the user to enable the device's cameras
                        Toast.makeText(this,
                            "Camera disabled",
                            Toast.LENGTH_SHORT).show()
                    }
                    CameraState.ERROR_CAMERA_FATAL_ERROR -> {
                        // Ask the user to reboot the device to restore camera function
                        Toast.makeText(this,
                            "Fatal error",
                            Toast.LENGTH_SHORT).show()
                    }
                    // Closed errors
                    CameraState.ERROR_DO_NOT_DISTURB_MODE_ENABLED -> {
                        // Ask the user to disable the "Do Not Disturb" mode, then reopen the camera
                        Toast.makeText(this,
                            "Do not disturb mode enabled",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}



