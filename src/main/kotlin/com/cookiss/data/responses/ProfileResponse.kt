package com.cookiss.data.responses

data class ProfileResponse(
    val username: String,
    val bio: String,
    val followerCount: Int,
    val followingCount: Int,
    val postCount: Int,
    val profilePictureUrl: String,
    val topSkillLinks: List<String>,
    val githubUrl: String?,
    val instagramUrl: String?,
    val linkedInUrl: String?,
    val isOwnProfile: Boolean,
    val isFollowing: Boolean
)
