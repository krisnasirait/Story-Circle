package com.krisna.storycircle.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.krisna.storycircle.data.model.request.RegisterRequestBody
import com.krisna.storycircle.databinding.ActivityRegisterBinding
import com.krisna.storycircle.presentation.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.customEditText.setOnRegisterClickListener {
            val name = binding.customEditText.getName()
            val email = binding.customEditText.getEmail()
            val password = binding.customEditText.getPassword()

            authViewModel.registerUser(RegisterRequestBody(name, email, password))
        }

        setupObservers()
    }

    private fun setupObservers() {
        authViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        authViewModel.registerUser.observe(this) { registerResponse ->
            registerResponse?.let {
                showRegistrationSuccess()
            }
        }

        authViewModel.isLoading.observe(this) { isLoading ->
            binding.lottieLoading.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showRegistrationSuccess() {
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}