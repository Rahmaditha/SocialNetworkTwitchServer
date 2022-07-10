package com.cookiss.service

import com.cookiss.data.models.Post
import com.cookiss.data.repository.post.PostRepository
import com.cookiss.data.requests.CreatePostRequest
import com.cookiss.util.Constants

class PostService(
    private val repository: PostRepository
) {

    suspend fun createPostIfUserExist(request: CreatePostRequest): Boolean{
        return repository.createPostIfUserExist(
            Post(
                imageUrl = "",
                userId = request.userId,
                timestamp = System.currentTimeMillis(),
                description = request.description
            )
        )
    }

    suspend fun getPostsForFollows(
        userId: String,
        page : Int = 0,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Post>{
        return repository.getPostByFollows(userId, page, pageSize)
    }
}