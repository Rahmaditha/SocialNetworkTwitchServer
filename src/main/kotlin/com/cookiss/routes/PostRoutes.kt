package com.cookiss.routes

import com.cookiss.data.models.Post
import com.cookiss.data.models.User
import com.cookiss.data.repository.post.PostRepository
import com.cookiss.data.requests.CreateAccountRequest
import com.cookiss.data.requests.CreatePostRequest
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.service.PostService
import com.cookiss.util.ApiResponseMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createPostRoute(postService: PostService){
    post("/api/post/create"){
        val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val didUserExist = postService.createPostIfUserExist(request)

        if(didUserExist){
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