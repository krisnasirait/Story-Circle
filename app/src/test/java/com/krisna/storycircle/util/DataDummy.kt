package com.krisna.storycircle.util

import com.krisna.storycircle.data.model.response.allstory.Story

object DataDummy {
    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                "name + $i",
                "$i",
                42.001,
                56.02,
                "name $i",
                "photoUrl https://story.circle/$i",
            )
            items.add(story)
        }
        return items
    }
}