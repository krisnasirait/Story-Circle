package com.krisna.storycircle.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krisna.storycircle.data.model.request.RegisterRequestBody
import com.krisna.storycircle.data.model.response.RegisterResponse
import com.krisna.storycircle.data.repository.StoryCircleRepository
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class AuthViewModel(
    private val storyCircleRepository: StoryCircleRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _registerUser = MutableLiveData<RegisterResponse?>()
    val registerUser: LiveData<RegisterResponse?> = _registerUser

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private fun handleRegistrationError(response: Response<RegisterResponse>) {
        val errorMessage = response.errorBody()?.string()
        if (errorMessage != null) {
            try {
                val errorJson = JSONObject(errorMessage)
                val error = errorJson.getString("message")
                Log.d("registerUserMessage", "error $error")
                _errorMessage.value = error
            } catch (e: JSONException) {
                Log.d("registerUserMessage", "JSON parsing error: ${e.message}")
                _errorMessage.value = "Unexpected error occurred"
            }
        } else {
            _errorMessage.value = "Unexpected error occurred"
        }
    }

    fun registerUser(registerRequestBody: RegisterRequestBody) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = storyCircleRepository.registerUser(registerRequestBody)
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _registerUser.value = response.body()
                    Log.d("registerUserMessage", "isSuccess : ${response.body()?.message}")
                } else {
                    _isLoading.value = false
                    handleRegistrationError(response)
                }
            }catch (e: Exception) {
                _isLoading.value = false
                Log.d("registerUserMessage", e.message.toString())
                _errorMessage.value = e.message
            }
        }
    }
}