package com.krisna.storycircle.data.model.response.allstory

data class AllStoriesResponse(
    val error: Boolean,
    val listStory: List<Story>,
    val message: String
)