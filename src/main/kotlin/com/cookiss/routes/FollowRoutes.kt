package com.cookiss.routes

import com.cookiss.data.repository.follow.FollowRepository
import com.cookiss.data.requests.FollowUpdateRequest
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.service.FollowService
import com.cookiss.util.ApiResponseMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.followUser(followService: FollowService){
    authenticate {
        post("/api/following/follow"){
            val request = call.receiveOrNull<FollowUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val didUserExist = followService.followUserIfExist(request, call.userId)
            if(didUserExist) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(successful = true)
                )
            } else{
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
            }
        }
    }
}

fun Route.unfollowUser(followService: FollowService){
    authenticate {
        delete("/api/following/unfollow"){
            val request = call.receiveOrNull<FollowUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val didUserExist = followService.unfollowUserIfExist(
                request,
                call.userId
            )

            if(didUserExist){
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(successful = true)
                )
            }else{
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
            }
        }
    }
}
