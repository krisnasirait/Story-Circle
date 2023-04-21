package com.krisna.storycircle.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.krisna.storycircle.data.model.response.allstory.Story
import com.krisna.storycircle.databinding.ItemStoryBinding

class StoryAdapter(
    private val itemClicklistener: OnItemClickListener,
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    private val storyList = mutableListOf<Story?>()

    inner class StoryViewHolder(
        private val binding: ItemStoryBinding
    ): RecyclerView.ViewHolder(binding.root) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder(
            ItemStoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        storyList[position]?.let { holder.bind(it) }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Story?>) {
        storyList.clear()
        storyList.addAll(data)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onStoryClicked(id: String)
    }
}