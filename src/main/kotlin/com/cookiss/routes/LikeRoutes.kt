package com.cookiss.routes

import com.cookiss.data.requests.LikeUpdateRequest
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.data.util.ParentType
import com.cookiss.service.ActivityService
import com.cookiss.service.LikeService
import com.cookiss.util.ApiResponseMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.likeParent(
    likeService: LikeService,
    activityService: ActivityService
){
    authenticate {
        post("api/like") {
            val request = call.receiveOrNull<LikeUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userId = call.userId
            val likeSuccesful = likeService.likeParent(userId, request.parentId, request.parentType)
            if(likeSuccesful){
                activityService.addLikeActivity(
                    userId,
                    ParentType.fromType(request.parentType),
                    request.parentId
                )

                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = true
                    )
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

fun Route.unlikeParent(
    likeService: LikeService
){
    authenticate {
        delete ("api/unlike") {
            val request = call.receiveOrNull<LikeUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val unlikeSuccesful = likeService.unlikeParent(call.userId, request.parentId, request.parentType)
            if(unlikeSuccesful){
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = true
                    )
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