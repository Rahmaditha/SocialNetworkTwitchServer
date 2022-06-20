package com.cookiss.routes

import com.cookiss.data.repository.user.UserRepository
import com.cookiss.data.models.User
import com.cookiss.data.requests.CreateAccountRequest
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.util.ApiResponseMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createUserRoute(userRepository: UserRepository){

    post("/api/user/create"){
        val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val doesUserExist = userRepository.getUserByEmail(request.email) != null
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
        userRepository.createUser(
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

fun Route.loginUser(userRepository: UserRepository){
    post("/api/user/login") {
        val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if(request.email.isBlank() || request.password.isBlank()){
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val isCorrectPassword = userRepository.doesPasswordForUserMatch(
            email = request.email,
            enteredPassword = request.password
        )
        if(isCorrectPassword){
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
                    message = ApiResponseMessages.INVALID_CREDENTIALS
                )
            )
        }
    }
}