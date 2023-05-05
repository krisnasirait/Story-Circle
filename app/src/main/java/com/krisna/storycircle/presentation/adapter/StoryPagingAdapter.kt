package com.krisna.storycircle.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.krisna.storycircle.data.model.response.allstory.Story
import com.krisna.storycircle.databinding.ItemStoryBinding

class StoryPagingAdapter(
    private val itemClicklistener: OnItemClickListener,
) : PagingDataAdapter<Story, StoryPagingAdapter.StoryPagingViewHolder>(StoryDiffCallback()) {

    inner class StoryPagingViewHolder(
        private val binding: ItemStoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Story) {
            binding.apply {
                tvName.text = item.name
                Glide.with(root)
                    .load(item.photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivImage)
                root.setOnClickListener {
                    itemClicklistener.onStoryClicked(item.id)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: StoryPagingViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryPagingViewHolder {
        return StoryPagingViewHolder(
            ItemStoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    interface OnItemClickListener {
        fun onStoryClicked(id: String)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}

class StoryDiffCallback : DiffUtil.ItemCallback<Story>() {
    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem == newItem
    }
}