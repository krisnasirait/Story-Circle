package com.krisna.storycircle.presentation.activity.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.krisna.storycircle.data.model.request.LoginUserRequestBody
import com.krisna.storycircle.databinding.ActivityLoginBinding
import com.krisna.storycircle.presentation.activity.MainActivity
import com.krisna.storycircle.presentation.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.customLogin.setOnLoginClickListener {
            val email = binding.customLogin.getEmail()
            val password = binding.customLogin.getPassword()

            authViewModel.loginUser(LoginUserRequestBody(email, password))
        }

        setupObservers()
    }

    private fun setupObservers() {
        authViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        authViewModel.loginUser.observe(this) { loginUser ->
            loginUser?.let {
                showLoginSuccess()
            }
        }

        authViewModel.isLoading.observe(this) { isLoading ->
            binding.lottieLoading.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showLoginSuccess() {
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}