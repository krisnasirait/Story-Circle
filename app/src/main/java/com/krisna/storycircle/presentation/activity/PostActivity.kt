package com.krisna.storycircle.presentation.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.krisna.storycircle.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getImage()

    }

    private fun getImage() {
        val photoFilePath = intent.getStringExtra("photo")
        val photoOrientation = intent.getIntExtra("orientation", 0)
        val bitmap = BitmapFactory.decodeFile(photoFilePath)
        binding.ivImagePost.setImageBitmap(bitmap)
        binding.ivImagePost.rotation = photoOrientation * 90f
    }
}