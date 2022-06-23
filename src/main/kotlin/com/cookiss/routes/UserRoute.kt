package com.cookiss.routes

import com.cookiss.data.repository.user.UserRepository
import com.cookiss.data.models.User
import com.cookiss.data.requests.CreateAccountRequest
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.service.UserService
import com.cookiss.util.ApiResponseMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createUserRoute(userService: UserService){

    post("/api/user/create"){
        val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        if(userService.doesUserWithEmailExist(request.email)){
            call.respond(
                BasicApiResponse(
                    message = ApiResponseMessages.USER_ALREADY_EXISTS,
                    successful = false)
            )
            return@post
        }

        when(userService.validateCreateAccountRequest(request)){
            is UserService.ValidationEvent.ErrorFieldEmpty -> {
                call.respond(
                    BasicApiResponse(
                        message = ApiResponseMessages.FIELDS_BLANK,
                        successful = false)
                )
                return@post
            }
            is UserService.ValidationEvent.Success -> {
                userService.createUser(request)
                call.respond(
                    BasicApiResponse(
                        successful = false)
                )
                return@post
            }
        }
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