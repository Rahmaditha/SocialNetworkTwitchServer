package com.cookiss.service

import com.cookiss.data.repository.likes.LikesRepository
import com.cookiss.data.util.ParentType

class LikeService(
    private val repository: LikesRepository
) {

    suspend fun likeParent(userId: String, parentId: String, parentType: Int): Boolean{
        return repository.likeParent(userId, parentId, parentType)
    }

    suspend fun unlikeParent(userId: String, parentId: String, parentType: Int): Boolean{
        return repository.unlikeParent(userId, parentId, parentType)
    }

    suspend fun deleteLikesorParent(parentId: String){
        repository.deleteLikesForParent(parentId)
    }
}