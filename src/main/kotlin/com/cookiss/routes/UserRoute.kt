package com.cookiss.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cookiss.data.repository.user.UserRepository
import com.cookiss.data.models.User
import com.cookiss.data.requests.CreateAccountRequest
import com.cookiss.data.requests.LoginRequest
import com.cookiss.data.responses.AuthResponse
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.service.UserService
import com.cookiss.util.ApiResponseMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

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

fun Route.loginUser(
    userService: UserService,
    jwtIssuer: String,
    jwtAudience: String,
    jwtSecret: String

){
    post("/api/user/login") {
        val request = call.receiveOrNull<LoginRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if(request.email.isBlank() || request.password.isBlank()){
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userService.getUserByEmail(request.email) ?: kotlin.run {
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = false,
                    message = ApiResponseMessages.INVALID_CREDENTIALS
                )
            )
            return@post
        }
        val isCorrectPassword = userService.isValidPassword(
            enteredPassword = request.password,
            actualPassword = user.password
        )
        if(isCorrectPassword){
            val expiresIn = 1000L * 60L * 60L * 24L * 365L
            val token = JWT.create()
                .withClaim("userId", user.id)
                .withIssuer(jwtIssuer)
                .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
                .withAudience(jwtAudience)
                .sign(Algorithm.HMAC256(jwtSecret))

            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    token = token
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