package com.cookiss.service

import com.cookiss.data.repository.follow.FollowRepository
import com.cookiss.data.requests.FollowUpdateRequest

class FollowService(
    private val followRepository: FollowRepository
) {

    suspend fun followUserIfExist(request: FollowUpdateRequest, followingUserId: String): Boolean{
        return followRepository.followUserIfExists(
            followingUserId,
            request.followedUserId
        )
    }

    suspend fun unfollowUserIfExist(request: FollowUpdateRequest, followingUserId: String): Boolean {
        return followRepository.unfollowUserIfExist(
            followingUserId,
            request.followedUserId
        )
    }
}