package com.d108.sduty.model.dto

data class Timeline(var profile: Profile, var story: Story, var cntReply: Int, var replies: List<Reply>, var likes: Boolean, var scrap: Boolean, var jobHashtag: String, var subjectHashtag: List<String>) {
}