package com.krisna.storycircle.presentation.customviews

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.krisna.storycircle.R


class CustomRegisterEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: AppCompatButton

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (s.hashCode() == etEmail.text.hashCode() && !TextUtils.isEmpty(email) && !email.isValidEmail()) {
                etEmail.error = "Please enter a valid email address"
            } else {
                etEmail.error = null
            }

            if (s.hashCode() == etPassword.text.hashCode() && password.isNotEmpty() && password.length < 8) {
                etPassword.error = "Password must be at least 8 characters"
            } else {
                etPassword.error = null
            }

            if (validateForm(name, email, password)) {
                btnRegister.isEnabled = true
                btnRegister.setBackgroundResource(R.drawable.custom_btn_rounded_enabled)
            } else {
                btnRegister.isEnabled = false
                btnRegister.setBackgroundResource(R.drawable.custom_btn_rounded_disabled)
            }
            btnRegister.isEnabled = validateForm(name, email, password)
        }

        override fun afterTextChanged(s: Editable?) {
            // do nothing
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_register_edit_text, this, true)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)

        etName.addTextChangedListener(textWatcher)
        etEmail.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)

        btnRegister.isEnabled = false
        btnRegister.setBackgroundResource(R.drawable.custom_btn_rounded_disabled)
        btnRegister.setOnClickListener {
            // handle register button click
        }
    }

    fun validateForm(name: String, email: String, password: String): Boolean {
        return name.isNotEmpty() && email.isValidEmail() && password.length >= 8
    }

    private fun String.isValidEmail(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    fun getName(): String {
        return etName.text.toString().trim()
    }

    fun getEmail(): String {
        return etEmail.text.toString().trim()
    }

    fun getPassword(): String {
        return etPassword.text.toString().trim()
    }

    fun setOnRegisterClickListener(listener: OnClickListener) {
        btnRegister.setOnClickListener(listener)
    }
}