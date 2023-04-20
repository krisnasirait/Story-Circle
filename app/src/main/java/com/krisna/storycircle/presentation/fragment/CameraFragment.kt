package com.krisna.storycircle.presentation.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.krisna.storycircle.databinding.FragmentCameraBinding
import com.krisna.storycircle.presentation.activity.PostActivity
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraSelector: CameraSelector
    private var currentOrientation = ExifInterface.ORIENTATION_NORMAL


    private var photoFile: File? = null

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCamera()
        binding.fabCapture.setOnClickListener {
            takePhoto()
        }
    }

    override fun onResume() {
        super.onResume()
        setUpCamera()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun setUpCamera() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            val requestPermissionLauncher =
                registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        startCamera()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Camera permission is required to use the camera.",
                            Toast.LENGTH_SHORT
                        ).show()
                        activity?.finish()
                    }
                }
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        photoFile = createFile(requireContext())

        imageCapture.targetRotation = Surface.ROTATION_0

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile!!).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to capture image.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    showPreview(photoFile!!)
                }
            })
    }

    private fun createFile(context: Context): File {
        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Toast.makeText(requireContext(), "Image saved", Toast.LENGTH_SHORT).show()
        return File(storageDir, fileName)
    }

    private fun showPreview(photoFile: File) {
        var bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        binding.previewView.visibility = View.GONE
        binding.fabCapture.visibility = View.GONE
        binding.previewImage.visibility = View.VISIBLE

        if (bitmap.width > bitmap.height) {
            val matrix = Matrix()
            matrix.postRotate(90f)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        binding.previewImage.setImageBitmap(bitmap)

        binding.btnRotatePict.visibility = View.VISIBLE
        binding.btnCancel.visibility = View.VISIBLE
        binding.fabPost.visibility = View.VISIBLE

        binding.fabPost.setOnClickListener {
            val intent = Intent(requireContext(), PostActivity::class.java)
            intent.putExtra("photo", photoFile.absolutePath)
            intent.putExtra("orientation", currentOrientation.toFloat())
            Log.d("photosSent", "showPreview: $currentOrientation")
            startActivity(intent)
        }

        binding.btnRotatePict.setOnClickListener {
            val rotatedFile = rotateImage(photoFile)
            val rotatedBitmap = BitmapFactory.decodeFile(rotatedFile.absolutePath)
            binding.previewImage.setImageBitmap(rotatedBitmap)
        }

        binding.btnCancel.setOnClickListener {
            photoFile.delete()
            binding.previewView.visibility = View.VISIBLE
            binding.fabCapture.visibility = View.VISIBLE
            binding.previewImage.visibility = View.GONE
            binding.btnCancel.visibility = View.GONE
            binding.btnRotatePict.visibility = View.GONE
            binding.fabPost.visibility = View.GONE
        }
    }

    private fun rotateImage(file: File): File {
        val ei = ExifInterface(file.absolutePath)
        var orientation = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        orientation = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> ExifInterface.ORIENTATION_ROTATE_180
            ExifInterface.ORIENTATION_ROTATE_180 -> ExifInterface.ORIENTATION_ROTATE_270
            ExifInterface.ORIENTATION_ROTATE_270 -> ExifInterface.ORIENTATION_NORMAL
            else -> ExifInterface.ORIENTATION_ROTATE_90
        }
        currentOrientation = orientation
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        val outputStream = FileOutputStream(file)
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }
}