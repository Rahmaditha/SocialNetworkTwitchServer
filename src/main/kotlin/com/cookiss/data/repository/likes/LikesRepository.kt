package com.cookiss.data.repository.likes

import com.cookiss.data.util.ParentType

interface LikesRepository {

    suspend fun likeParent(userId: String, parentId: String, parentType: Int): Boolean

    suspend fun unlikeParent(userId:String, parentId: String, parentType: Int): Boolean

    suspend fun deleteLikesForParent(parentId: String)
}