package com.krisna.storycircle.presentation.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.krisna.storycircle.data.model.request.LoginUserRequestBody
import com.krisna.storycircle.databinding.ActivitySplashBinding
import com.krisna.storycircle.presentation.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashTime = 4000L
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkCredentials()
    }

    private fun checkCredentials() {
        val sharedPref = getSharedPreferences("credentials", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", null)
        val password = sharedPref.getString("password", null)

        if (email != null && password != null) {
            authViewModel.loginUser(LoginUserRequestBody(email, password))

            authViewModel.loginUser.observe(this) { loginUser ->
                loginUser?.let {
                    val bearerToken = loginUser.loginResult.token
                    with(sharedPref.edit()) {
                        putString("bearerToken", bearerToken)
                        apply()
                    }
                }
            }

            authViewModel.isLoading.observe(this) { isLoading ->
                if (!isLoading) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }

            authViewModel.errorMessage.observe(this) { errorMessage ->
                if (errorMessage != null) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, OnBoardingActivity::class.java)
                startActivity(intent)
                finish()
            }, splashTime)
        }
    }


}