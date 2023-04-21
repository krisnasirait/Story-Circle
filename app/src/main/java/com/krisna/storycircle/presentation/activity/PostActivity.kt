package com.krisna.storycircle.presentation.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import com.krisna.storycircle.databinding.ActivityPostBinding
import com.krisna.storycircle.presentation.viewmodel.StoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private val storyViewModel: StoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getImage()
        setupActionBar()
        binding.btnUploadStory.setOnClickListener {
            postStory()

            storyViewModel.addStory.observe(this) { addStory ->
                if (addStory?.error == false) {
                    Toast.makeText(this, "Story posted", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this, "Failed to post story", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun postStory() {
        val bearerToken = getSharedPreferences("credentials", Context.MODE_PRIVATE)
            .getString("bearerToken", "") ?: ""
        val description = binding.etDescription.text.toString()
        val photoFilePath = intent.getStringExtra("photo")
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)
        val photoFile = photoFilePath?.let { File(it) }

        if (bearerToken.isNotEmpty() && photoFile != null) {
            storyViewModel.postStory(bearerToken, description, photoFile, lat, lon)
        }

        storyViewModel.isLoading.observe(this) {
            binding.lottieLoading.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun getImage() {
        val photoFilePath = intent.getStringExtra("photo")
        val exif = ExifInterface(photoFilePath!!)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        val bitmap = BitmapFactory.decodeFile(photoFilePath)
        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> bitmap.rotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> bitmap.rotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> bitmap.rotate(270f)
            else -> bitmap
        }
        binding.ivImagePost.setImageBitmap(rotatedBitmap)
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "New Post"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }
}