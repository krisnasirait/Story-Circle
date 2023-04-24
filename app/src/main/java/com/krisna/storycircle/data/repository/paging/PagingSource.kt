package com.krisna.storycircle.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.krisna.storycircle.data.model.response.allstory.Story
import com.krisna.storycircle.data.remote.ApiService

class AllStoriesPagingSource(
    private val apiService: ApiService,
    private val token: String,
    private val location: Int?
) : PagingSource<Int, Story>() {

    companion object {
        private const val INITIAL_PAGE = 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        val page = params.key ?: INITIAL_PAGE
        val pageSize = params.loadSize

        return try {
            val response = apiService.getAllStories(token, page, pageSize, location)
            if (response.isSuccessful) {
                val stories = response.body()?.listStory ?: emptyList()
                LoadResult.Page(
                    data = stories,
                    prevKey = if (page == INITIAL_PAGE) null else page - 1,
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
        return state.anchorPosition
    }
}