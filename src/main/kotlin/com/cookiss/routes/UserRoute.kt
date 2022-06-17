package com.cookiss.routes

import com.cookiss.controller.user.UserController
import com.cookiss.data.models.User
import com.cookiss.data.requests.CreateAccountRequest
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.data.util.ApiResponseMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoutes(){
    val userController: UserController by inject()

    route("/api/user/create"){
        post {
            val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val doesUserExist = userController.getUserByEmail(request.email) != null
            if(doesUserExist){
                call.respond(
                    BasicApiResponse(
                        message = ApiResponseMessages.USER_ALREADY_EXISTS,
                        successful = false)
                )
                return@post
            }
            if(request.email.isBlank() || request.password.isBlank() || request.username.isBlank()){
                call.respond(
                    BasicApiResponse(
                        message = ApiResponseMessages.FIELDS_BLANK,
                        successful = false)
                )
                return@post
            }
            userController.createUser(
                User(
                    email = request.email,
                    username = request.username,
                    password = request.password,
                    profileImageUrl = "",
                    bio = "",
                    githubUrl = null,
                    instagramUrl = null,
                    linkedInUrl = null
                )
            )
            call.respond(
                BasicApiResponse(
                    successful = true
                )
            )
        }
    }
}