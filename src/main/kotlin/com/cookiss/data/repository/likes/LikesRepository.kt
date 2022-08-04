package com.cookiss.data.repository.likes

import com.cookiss.data.models.Like
import com.cookiss.data.util.ParentType
import com.cookiss.util.Constants

interface LikesRepository {

    suspend fun likeParent(userId: String, parentId: String, parentType: Int): Boolean

    suspend fun unlikeParent(userId:String, parentId: String, parentType: Int): Boolean

    suspend fun deleteLikesForParent(parentId: String)
    suspend fun getLikesForParent(
        parentId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): List<Like>
}