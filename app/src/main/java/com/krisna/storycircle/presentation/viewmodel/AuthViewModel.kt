package com.krisna.storycircle.presentation.viewmodel

import androidx.lifecycle.*
import com.krisna.storycircle.data.model.response.RegisterResponse
import com.krisna.storycircle.data.repository.StoryCircleRepository
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

class AuthViewModel(
    private val storyCircleRepository: StoryCircleRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _registerUser = MutableLiveData<RegisterResponse?>()
    val registerUser: LiveData<RegisterResponse?> = _registerUser

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            runCatching {
                _isLoading.value = true
                storyCircleRepository.registerUser(name, email, password)
            }.onSuccess {
                _isLoading.value = false
                _registerUser.value = it
            }.onFailure {
                _isLoading.value = false
                _errorMessage.value = it.message
            }
        }
    }

    companion object {
        fun createViewModel(viewModelStoreOwner: ViewModelStoreOwner): AuthViewModel {
            return ViewModelProvider(viewModelStoreOwner, getKoin().get())[AuthViewModel::class.java]
        }
    }
}