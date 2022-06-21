package com.cookiss.data.repository.follow

interface FollowRepository {
    suspend fun followUserIfExists(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun unfollowUserIfExist(
        followingUserId: String,
        followedUserId: String
    ): Boolean
}