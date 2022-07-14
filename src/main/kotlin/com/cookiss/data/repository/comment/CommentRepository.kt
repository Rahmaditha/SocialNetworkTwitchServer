package com.cookiss.data.repository.comment

import com.cookiss.data.models.Comment
import com.cookiss.data.responses.CommentResponse

interface CommentRepository {

    suspend fun createComment(comment: Comment): String

    suspend fun deleteComment(commentId: String) : Boolean

    suspend fun getCommentsForPost(postId: String, ownUserId: String): List<CommentResponse>

    suspend fun getComment(commentId: String): Comment?

    suspend fun deleteCommentsFromPost(postId: String) : Boolean
}