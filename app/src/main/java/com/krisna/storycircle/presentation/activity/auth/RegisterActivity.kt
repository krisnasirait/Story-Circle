package com.krisna.storycircle.presentation.activity.auth

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.krisna.storycircle.databinding.ActivityRegisterBinding
import com.krisna.storycircle.di.ViewModelFactory
import com.krisna.storycircle.presentation.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels(
        factoryProducer = {
            ViewModelFactory.getInstance(this)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            authViewModel.registerUser(
                binding.textInputName.text.toString(),
                binding.textInputEmail.text.toString(),
                binding.textInputPassword.text.toString()
            )
        }
    }
}