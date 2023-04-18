package com.krisna.storycircle.presentation.activity.auth

import android.content.Intent
import android.os.Bundle
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

        authViewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        authViewModel.registerUser.observe(this) { registerResponse ->
            if (registerResponse != null) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }


        authViewModel.isLoading.observe(this) {
            binding.lottieLoading.visibility =
                if (it) android.view.View.VISIBLE else android.view.View.GONE
        }
    }
}