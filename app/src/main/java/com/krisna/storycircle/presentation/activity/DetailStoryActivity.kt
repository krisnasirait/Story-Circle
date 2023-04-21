package com.krisna.storycircle.presentation.activity

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.krisna.storycircle.databinding.ActivityDetailStoryBinding
import com.krisna.storycircle.presentation.viewmodel.StoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private val storyViewModel: StoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setView()
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Post"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    private fun setView() {
        val bearerToken = getSharedPreferences("credentials", Context.MODE_PRIVATE)
            .getString("bearerToken", "") ?: ""
        val id = intent.getStringExtra("id") ?: ""

        storyViewModel.getStoryDetail(bearerToken, id)

        storyViewModel.storyDetail.observe(this) { detailStory ->
            binding.tvName.text = detailStory?.story?.name
            Glide.with(this)
                .load(detailStory?.story?.photoUrl)
                .into(binding.ivPhotos)
            binding.tvDescription.text = detailStory?.story?.description
            binding.tvNameBottom.text = detailStory?.story?.name

            val dateFormatted = formatDate(detailStory?.story?.createdAt ?: "")
            binding.tvDate.text = dateFormatted

        }

        storyViewModel.isLoading.observe(this) { isLoading ->
            binding.lottieLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun formatDate(dateString: String): String {
        val dateProcess =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(dateString)
        return SimpleDateFormat("MMMM dd", Locale.getDefault()).format(dateProcess)
    }
}