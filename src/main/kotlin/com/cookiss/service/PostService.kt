package com.cookiss.service

import com.cookiss.data.models.Post
import com.cookiss.data.repository.post.PostRepository
import com.cookiss.data.requests.CreatePostRequest

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
}