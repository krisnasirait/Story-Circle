package com.krisna.storycircle.data.repository.paging

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.krisna.storycircle.StoryCircleApp
import com.krisna.storycircle.data.model.response.allstory.Story
import com.krisna.storycircle.data.remote.ApiService

class AllStoriesPagingSource(
    private val apiService: ApiService,
    private val location: Int?
) : PagingSource<Int, Story>() {

    private val sharedPreferences =
        StoryCircleApp.context.getSharedPreferences("credentials", Context.MODE_PRIVATE)

    private fun getBearerToken(): String {
        return sharedPreferences?.getString("bearerToken", "") ?: ""
    }

    private val token = "Bearer ${getBearerToken()}"

    companion object {
        private const val INITIAL_PAGE = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        Log.d("auth", "checkCredentials: ${getBearerToken()}")
        val page = params.key ?: INITIAL_PAGE
        val pageSize = params.loadSize

        return try {
            val response = apiService.getAllStories(token,page, pageSize, location)
            if (response.isSuccessful) {
                val stories = response.body()?.listStory ?: emptyList()
                LoadResult.Page(
                    data = stories,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (stories.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(Exception(response.message()))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}