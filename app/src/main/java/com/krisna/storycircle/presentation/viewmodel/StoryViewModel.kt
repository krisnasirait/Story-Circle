package com.krisna.storycircle.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.krisna.storycircle.data.model.response.addstory.AddNewStoryResponse
import com.krisna.storycircle.data.model.response.allstory.Story
import com.krisna.storycircle.data.model.response.detailstory.StoryDetailResponse
import com.krisna.storycircle.data.repository.StoryCircleRepository
import com.krisna.storycircle.data.repository.paging.AllStoriesPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class StoryViewModel(
    private val storyCircleRepository: StoryCircleRepository
) : ViewModel() {

    private var token: String? = null

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _addStory = MutableLiveData<AddNewStoryResponse?>()
    val addStory : LiveData<AddNewStoryResponse?> = _addStory

    private val _listStory = MutableLiveData<List<Story?>?>()
    val listStory : LiveData<List<Story?>?> = _listStory

    private val _storyDetail = MutableLiveData<StoryDetailResponse?>()
    val storyDetail : LiveData<StoryDetailResponse?> = _storyDetail

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage

    val stories: LiveData<PagingData<Story>> = Pager(
        PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            maxSize = 100
        )
    ) {
        AllStoriesPagingSource(storyCircleRepository, token.toString(), null)
    }.flow.cachedIn(viewModelScope).asLiveData()

    fun postStory(token: String, description: String, photoFile: File, lat: Double?, lon: Double?) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val response = storyCircleRepository.addNewStory(token, description, photoFile, lat, lon)
                response.body()
            }.onSuccess { story ->
                withContext(Dispatchers.Main) {
                    _addStory.value = story
                }
            }.onFailure { throwable ->
                withContext(Dispatchers.Main) {
                    _errorMessage.value = throwable.message
                }
            }.also {
                withContext(Dispatchers.Main) {
                    _isLoading.postValue(false)
                }
            }
        }
    }

    fun getAllStory(token: String, page: Int?, size: Int?, location: Int?) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val response = storyCircleRepository.getAllStories(token, page, size, location)
                response.body()
            }.onSuccess { story ->
                withContext(Dispatchers.Main) {
                    _listStory.value = story?.listStory
                }
            }.onFailure { throwable ->
                withContext(Dispatchers.Main) {
                    _errorMessage.value = throwable.message
                }
            }.also {
                withContext(Dispatchers.Main) {
                    _isLoading.postValue(false)
                }
            }
        }
    }

    fun getStoryDetail(token: String, storyId: String) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val response = storyCircleRepository.getStoryDetail(token, storyId)
                response.body()
            }.onSuccess { story ->
                withContext(Dispatchers.Main) {
                    _storyDetail.value = story
                }
            }.onFailure { throwable ->
                withContext(Dispatchers.Main) {
                    _errorMessage.value = throwable.message
                }
            }.also {
                withContext(Dispatchers.Main) {
                    _isLoading.postValue(false)
                }
            }
        }
    }

    fun setToken(token: String) {
        this.token = token
    }
}