package com.krisna.storycircle.presentation.activity.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

            authViewModel.registerUser(name, email, password)

            authViewModel.registerUser.observe(this) {
                if (it?.error  == false) {
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    Toast.makeText(this, it?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }


        authViewModel.isLoading.observe(this) {
            binding.lottieLoading.visibility =
                if (it) android.view.View.VISIBLE else android.view.View.GONE
        }
    }
}