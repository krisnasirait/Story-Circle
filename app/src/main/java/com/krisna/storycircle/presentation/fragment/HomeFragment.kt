package com.krisna.storycircle.presentation.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.krisna.storycircle.databinding.FragmentHomeBinding
import com.krisna.storycircle.presentation.activity.DetailStoryActivity
import com.krisna.storycircle.presentation.adapter.LoadingStateAdapter
import com.krisna.storycircle.presentation.adapter.StoryPagingAdapter
import com.krisna.storycircle.presentation.viewmodel.StoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(), StoryPagingAdapter.OnItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private val storyViewModel: StoryViewModel by viewModel()
    private lateinit var storyPagingAdapter: StoryPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)

        setupPagingRecyclerView()
        setupViewModelObservers()
        setupActionBar()

        binding.swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "Home"
            setDisplayHomeAsUpEnabled(false)
        }
    }

    private fun setupPagingRecyclerView() {
        storyPagingAdapter = StoryPagingAdapter(this)
        binding.rvStoryList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = storyPagingAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storyPagingAdapter.retry() }
            )
        }
    }

    private fun setupViewModelObservers() {
        val bearerToken = requireActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE)
            .getString("bearerToken", "") ?: ""

        Log.d("auth", "Bearer Token in HomeFragment: $bearerToken")
        storyViewModel.setToken(bearerToken)

        storyViewModel.stories.observe(viewLifecycleOwner) { pagingData ->
            storyPagingAdapter.submitData(lifecycle, pagingData)
        }

        storyPagingAdapter.addLoadStateListener { loadState ->
            binding.swipeRefreshLayout.isRefreshing = loadState.refresh is LoadState.Loading
            if (loadState.refresh is LoadState.Error) {
                Toast.makeText(requireContext(), "Error loading data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStoryClicked(id: String) {
        val intent = Intent(activity, DetailStoryActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    private fun refresh() {
        storyViewModel.stories.observe(viewLifecycleOwner) { pagingData ->
            storyPagingAdapter.submitData(lifecycle, pagingData)
        }
        storyPagingAdapter.refresh()
        binding.rvStoryList.scrollToPosition(0)
    }

    override fun onResume() {
        super.onResume()

        val bearerToken = requireActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE)
            .getString("bearerToken", "") ?: return
        storyViewModel.setToken(bearerToken)
    }
}