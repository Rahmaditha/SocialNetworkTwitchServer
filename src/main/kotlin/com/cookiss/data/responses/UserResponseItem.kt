package com.cookiss.data.responses

data class UserResponseItem(
    val userName: String,
    val profilePictureUrl: String,
    val bio: String,
    val isFollowing: Boolean
)
