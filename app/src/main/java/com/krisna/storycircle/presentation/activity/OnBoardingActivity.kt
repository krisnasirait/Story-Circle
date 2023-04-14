package com.krisna.storycircle.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.krisna.storycircle.databinding.ActivityOnBoardingBinding
import com.krisna.storycircle.presentation.activity.auth.LoginActivity

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("prefSC", 0)
        val isFirstTime = sharedPreferences.getString("isFirst", "")

        if(!isFirstTime.isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnGetStarted.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                sharedPreferences?.edit()?.putString("isFirst", "false")?.apply()
                startActivity(it)
                finish()
            }
        }
    }
}