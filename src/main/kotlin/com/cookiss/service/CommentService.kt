package com.cookiss.service

import com.cookiss.data.models.Comment
import com.cookiss.data.repository.comment.CommentRepository
import com.cookiss.data.requests.CreateCommentRequest
import com.cookiss.util.Constants

class CommentService(
    private val commentRepository: CommentRepository,
    private val repository: CommentRepository
) {
    suspend fun createComment(createCommentRequest: CreateCommentRequest, userId: String): ValidationEvents{
        createCommentRequest.apply {
            if(comment.isBlank() ||  postId.isBlank()){
                return ValidationEvents.ErrorFieldEmpty
            }
            if(comment.length > Constants.MAX_COMMENT_LENGTH){
                return ValidationEvents.ErrorCommentTooLong
            }
        }
        repository.createComment(
            Comment(
                comment = createCommentRequest.comment,
                userId = userId,
                postId = createCommentRequest.postId,
                timestamp = System.currentTimeMillis()
            )
        )
        return ValidationEvents.Success
    }

    suspend fun deleteComment(commentId: String): Boolean{
        return repository.deleteComment(commentId)
    }

    suspend fun getCommentById(commentId: String): Comment?{
        return repository.getComment(commentId)
    }

    suspend fun getCommentsForPost(postId: String): List<Comment>{
        return repository.getCommentsForPost(postId)
    }

    suspend fun deleteCommentsForPost(postId: String) {
        commentRepository.deleteCommentsFromPost(postId)
    }

    sealed class ValidationEvents{
        object ErrorFieldEmpty: ValidationEvents()
        object ErrorCommentTooLong: ValidationEvents()
        object Success: ValidationEvents()
    }
}