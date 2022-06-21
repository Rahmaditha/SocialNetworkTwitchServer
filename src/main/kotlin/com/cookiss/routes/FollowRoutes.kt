package com.cookiss.routes

import com.cookiss.data.repository.follow.FollowRepository
import com.cookiss.data.requests.FollowUpdateRequest
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.util.ApiResponseMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.followUser(followRepository: FollowRepository){
    post("/api/following/follow"){
        val request = call.receiveOrNull<FollowUpdateRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val didUserExist = followRepository.followUserIfExists(
            request.followingUserId,
            request.followedUserId
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

fun Route.unfollowUser(followRepository: FollowRepository){
    delete("/api/following/unfollow"){
        val request = call.receiveOrNull<FollowUpdateRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@delete
        }

        val didUserExist = followRepository.unfollowUserIfExist(
            request.followingUserId,
            request.followedUserId
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