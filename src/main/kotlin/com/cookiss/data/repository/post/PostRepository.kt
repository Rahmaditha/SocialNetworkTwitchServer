package com.cookiss.data.repository.post

import com.cookiss.data.models.Post
import com.cookiss.util.Constants

interface PostRepository {

    suspend fun createPostIfUserExist(post: Post): Boolean

    suspend fun deletePost(postId: String)

    suspend fun getPostByFollows(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): List<Post>

    suspend fun getPost(postId: String): Post?


}