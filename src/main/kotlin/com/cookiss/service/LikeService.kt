package com.cookiss.service

import com.cookiss.data.repository.likes.LikesRepository

class LikeService(
    private val repository: LikesRepository
) {

    suspend fun likeParent(userId: String, parentId: String): Boolean{
        return repository.likeParent(userId, parentId)
    }

    suspend fun unlikeParent(userId: String, parentId: String): Boolean{
        return repository.unlikeParent(userId, parentId)
    }

    suspend fun deleteLikesorParent(parentId: String){
        repository.deleteLikesForParent(parentId)
    }
}