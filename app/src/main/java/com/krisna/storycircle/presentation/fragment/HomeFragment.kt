package com.krisna.storycircle.presentation.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.krisna.storycircle.databinding.FragmentHomeBinding
import com.krisna.storycircle.presentation.activity.DetailStoryActivity
import com.krisna.storycircle.presentation.adapter.StoryAdapter
import com.krisna.storycircle.presentation.viewmodel.StoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(), StoryAdapter.OnItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private val storyViewModel: StoryViewModel by viewModel()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupRecyclerView()
        setupViewModelObservers()
        setupActionBar()
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "Home"
            setDisplayHomeAsUpEnabled(false)
        }
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter(this)
        binding.rvStoryList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = storyAdapter
        }
    }

    private fun setupViewModelObservers()  {
        val bearerToken = requireActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE)
            .getString("bearerToken", "") ?: ""

        storyViewModel.getAllStory(bearerToken, null, null, null)
        storyViewModel.isLoading.observe(requireActivity()) {
            binding.lottieLoading.visibility = if (it) View.VISIBLE else View.GONE
        }

        storyViewModel.listStory.observe(requireActivity()) { storyList ->
            storyAdapter.setData(storyList ?: emptyList())
        }
    }

    override fun onResume() {
        super.onResume()
        val bearerToken = requireActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE)
            .getString("bearerToken", "") ?: ""

        storyViewModel.getAllStory(bearerToken, null, null, null)
        storyViewModel.isLoading.observe(requireActivity()) {
        }

        storyViewModel.listStory.observe(requireActivity()) { storyList ->
            storyAdapter.setData(storyList ?: emptyList())
        }
    }

    override fun onStoryClicked(id: String) {
        val intent = Intent(activity, DetailStoryActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }
}