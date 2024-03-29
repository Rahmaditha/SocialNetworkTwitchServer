package com.cookiss.data.repository.follow

import com.cookiss.data.models.Following

interface FollowRepository {
    suspend fun followUserIfExists(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun unfollowUserIfExist(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun getFollowsByUser(userId: String): List<Following>

    suspend fun doesUserFollow(followingUserId: String, followedUserId: String): Boolean
}