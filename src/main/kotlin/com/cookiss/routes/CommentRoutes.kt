package com.cookiss.routes

import com.cookiss.data.requests.CreateCommentRequest
import com.cookiss.data.requests.DeleteCommentRequest
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.service.CommentService
import com.cookiss.service.LikeService
import com.cookiss.service.UserService
import com.cookiss.util.ApiResponseMessages
import com.cookiss.util.QueryParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createComment(
    commentService: CommentService
){
    authenticate {
        post("/api/comment/create") {
            val request = call.receiveOrNull<CreateCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            when(commentService.createComment(request, call.userId)){
                is CommentService.ValidationEvents.ErrorFieldEmpty -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(
                            successful = false,
                            message = ApiResponseMessages.FIELDS_BLANK
                        )
                    )
                }
                is CommentService.ValidationEvents.ErrorCommentTooLong -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(
                            successful = false,
                            message = ApiResponseMessages.COMMENT_TOO_LONG
                        )
                    )
                }
                is CommentService.ValidationEvents.Success -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(
                            successful = true
                        )
                    )
                }
            }

        }
    }
}

fun Route.getCommentsForPost(
    commentService: CommentService
){
    authenticate {
        get("/api/comment/get") {
            val postId = call.parameters[QueryParams.PARAM_POST_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val comments = commentService.getCommentsForPost(postId)
            call.respond(HttpStatusCode.OK, comments)
        }
    }
}

fun Route.deleteComment(
    commentService: CommentService,
    likeService: LikeService
){
    authenticate {
        delete("/api/comment/delete") {
            val request = call.receiveOrNull<DeleteCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val comment = commentService.getCommentById(request.commentId)

            if(call.userId != comment?.userId){
                call.respond(
                    HttpStatusCode.Unauthorized,
                    return@delete
                )
            }else{
                val deleted = commentService.deleteComment(request.commentId)
                if(deleted){
                    likeService.deleteLikesorParent(request.commentId)
                    call.respond(HttpStatusCode.OK, BasicApiResponse(
                        successful = true
                    ))
                }else{
                    call.respond(HttpStatusCode.OK, BasicApiResponse(
                        successful = false
                    ))
                }
            }

        }
    }
}