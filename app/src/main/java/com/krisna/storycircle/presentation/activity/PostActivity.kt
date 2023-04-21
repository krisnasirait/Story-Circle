package com.krisna.storycircle.presentation.activity

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
                    finish()
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
    }

    private fun getImage() {
        val photoFilePath = intent.getStringExtra("photo")
        val photoOrientation = intent.getIntExtra("orientation", 0)
        val bitmap = BitmapFactory.decodeFile(photoFilePath)
        binding.ivImagePost.setImageBitmap(bitmap)
        binding.ivImagePost.rotation = photoOrientation * 90f
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