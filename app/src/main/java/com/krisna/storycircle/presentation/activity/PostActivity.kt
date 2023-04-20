package com.krisna.storycircle.presentation.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.krisna.storycircle.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getImage()
        setupActionBar()
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