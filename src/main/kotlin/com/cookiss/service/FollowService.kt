package com.cookiss.service

import com.cookiss.data.repository.follow.FollowRepository
import com.cookiss.data.requests.FollowUpdateRequest

class FollowService(
    private val followRepository: FollowRepository
) {

    suspend fun followUserIfExist(request: FollowUpdateRequest): Boolean{
        return followRepository.followUserIfExists(
            request.followingUserId,
            request.followedUserId
        )
    }

    suspend fun unfollowUserIfExist(request: FollowUpdateRequest): Boolean {
        return followRepository.unfollowUserIfExist(
            request.followingUserId,
            request.followedUserId
        )
    }
}