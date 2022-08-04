package com.cookiss.service

import com.cookiss.data.repository.follow.FollowRepository
import com.cookiss.data.repository.likes.LikesRepository
import com.cookiss.data.repository.user.UserRepository
import com.cookiss.data.responses.UserResponseItem

class LikeService(
    private val likeRepository: LikesRepository,
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {

    suspend fun likeParent(userId: String, parentId: String, parentType: Int): Boolean{
        return likeRepository.likeParent(userId, parentId, parentType)
    }

    suspend fun unlikeParent(userId: String, parentId: String, parentType: Int): Boolean{
        return likeRepository.unlikeParent(userId, parentId, parentType)
    }

    suspend fun deleteLikesorParent(parentId: String){
        likeRepository.deleteLikesForParent(parentId)
    }

    suspend fun getUsersWhoLikedParent(parentId: String, userId: String): List<UserResponseItem>{
        val userIds = likeRepository.getLikesForParent(parentId).map { it.userId }
        val users = userRepository.getUsers(userIds)

        val followsByUser = followRepository.getFollowsByUser(userId)
        return users.map { user ->
            val isFollowing = followsByUser.find {
                it.followedUserId == user.id
            } != null
            UserResponseItem(
                userName = user.username,
                profilePictureUrl = user.profileImageUrl,
                bio = user.bio,
                isFollowing = true
            )
        }
    }
}