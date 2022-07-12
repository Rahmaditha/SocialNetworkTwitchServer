package com.cookiss.routes

import com.cookiss.data.requests.CreatePostRequest
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.service.CommentService
import com.cookiss.service.LikeService
import com.cookiss.service.PostService
import com.cookiss.util.ApiResponseMessages
import com.cookiss.util.Constants
import com.cookiss.util.QueryParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createPostRoute(
    postService: PostService
){
    authenticate {
        post("/api/post/create"){
            val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userId = call.userId

            val didUserExist = postService.createPostIfUserExist(request, userId)
            if(!didUserExist){
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
            }else{
                call.respond(
                    BasicApiResponse(
                        successful = true
                    )
                )
            }

        }
    }
}

fun Route.getPostForFollows(
    postService: PostService,
){
    authenticate {
        get("/api/post/get") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

            val posts = postService.getPostsForFollows(call.userId, page, pageSize)
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}

fun Route.deletePost(
    postService: PostService,
    commentService: CommentService,
    likeService: LikeService
){
    authenticate {
        delete("/api/post/delete"){
            val postId = call.parameters["postId"] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val post = postService.getPost(postId)
            print(post)
            if(post == null){
                call.respond(
                    HttpStatusCode.NotFound
                )
                return@delete
            }
            if (post.userId == call.userId) {
                postService.deletePost(postId)
                likeService.deleteLikesorParent(postId)
                commentService.deleteCommentsForPost(postId)
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }

        }
    }
}
