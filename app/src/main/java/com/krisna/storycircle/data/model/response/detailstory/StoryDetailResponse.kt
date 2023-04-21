package com.krisna.storycircle.data.model.response.detailstory

data class StoryDetailResponse(
    val error: Boolean,
    val message: String,
    val story: Story
)