package com.cookiss.routes

import com.cookiss.data.models.Post
import com.cookiss.data.models.User
import com.cookiss.data.repository.post.PostRepository
import com.cookiss.data.requests.CreateAccountRequest
import com.cookiss.data.requests.CreatePostRequest
import com.cookiss.data.requests.DeletePostRequest
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.plugins.email
import com.cookiss.service.LikeService
import com.cookiss.service.PostService
import com.cookiss.service.UserService
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
    postService: PostService,
    userService: UserService
){
    authenticate {
        post("/api/post/create"){
            val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            ifEmailBelongsToUser(
                userId = request.userId,
                validateEmail = userService::doesEmailBelongToUserId
            ){
                val didUserExist = postService.createPostIfUserExist(request)
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
}

fun Route.getPostForFollows(
    postService: PostService,
    userService: UserService
){
    authenticate {
        get {
            val userId = call.parameters[QueryParams.PARAM_USER_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?:
                Constants.DEFAULT_POST_PAGE_SIZE

            ifEmailBelongsToUser(
                userId = userId,
                validateEmail = userService::doesEmailBelongToUserId
            ){
                val posts = postService.getPostsForFollows(userId, page, pageSize)
                call.respond(
                    HttpStatusCode.OK,
                    posts
                )
            }
        }
    }
}

fun Route.deletePost(
    postService: PostService,
    userService: UserService,
    likeService: LikeService
){
    delete("/api/post/delete"){
        val request = call.receiveOrNull<DeletePostRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@delete
        }

        val post = postService.getPost(request.postId)
        if(post == null){
            call.respond(
                HttpStatusCode.NotFound
            )
            return@delete
        }
        ifEmailBelongsToUser(
            userId = post.userId,
            validateEmail = userService::doesEmailBelongToUserId
        ){
            postService.deletePost(request.postId)
            likeService.deleteLikesorParent(request.postId)
            call.respond(HttpStatusCode.OK)
        }

    }
}
