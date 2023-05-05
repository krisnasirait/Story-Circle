package com.krisna.storycircle.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.krisna.storycircle.data.model.response.allstory.Story
import com.krisna.storycircle.data.repository.StoryCircleRepository
import com.krisna.storycircle.presentation.adapter.StoryPagingAdapter
import com.krisna.storycircle.util.DataDummy
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule(testCoroutineDispatcher)

    @Mock
    private lateinit var storyCircleRepository: StoryCircleRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `when fetching Story amount of data is matching as expected`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<Story> = StoriesPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<Story>>()

        expectedStory.value = data
        Mockito.`when`(storyCircleRepository.getStoryPaging()).thenReturn(expectedStory)

        val pagingViewModel = StoryViewModel(storyCircleRepository)
        val actualStory: PagingData<Story> = runBlocking {
            pagingViewModel.getStoryPaging().asFlow().first()
        }

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
    }

    @Test
    fun `when fetching Story returns first data successfully`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<Story> = StoriesPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<Story>>()

        expectedStory.value = data
        Mockito.`when`(storyCircleRepository.getStoryPaging()).thenReturn(expectedStory)

        val pagingViewModel = StoryViewModel(storyCircleRepository)
        val actualStory: PagingData<Story> =
            runBlocking { pagingViewModel.getStoryPaging().asFlow().first() }

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when fetching Story Should Return Zero If No Data`() = runTest {
        val dummyStory = emptyList<Story>()
        val data: PagingData<Story> = StoriesPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<Story>>()

        expectedStory.value = data
        Mockito.`when`(storyCircleRepository.getStoryPaging()).thenReturn(expectedStory)

        val pagingViewModel = StoryViewModel(storyCircleRepository)
        val actualStory: PagingData<Story> =
            runBlocking { pagingViewModel.getStoryPaging().asFlow().first() }

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(0, differ.snapshot().size)
    }

}

class StoriesPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

class CoroutineTestRule(
    private val testDispatcher: TestCoroutineDispatcher
) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
