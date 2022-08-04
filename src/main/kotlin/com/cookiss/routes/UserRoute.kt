package com.cookiss.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cookiss.data.repository.user.UserRepository
import com.cookiss.data.models.User
import com.cookiss.data.requests.CreateAccountRequest
import com.cookiss.data.requests.LoginRequest
import com.cookiss.data.requests.UpdateProfileRequest
import com.cookiss.data.responses.AuthResponse
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.service.PostService
import com.cookiss.service.UserService
import com.cookiss.util.ApiResponseMessages
import com.cookiss.util.Constants
import com.cookiss.util.QueryParams
import com.cookiss.util.save
import com.google.gson.Gson
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.litote.kmongo.mul
import java.io.File
import java.nio.file.Paths
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

fun Route.searchUser(userService: UserService){
    authenticate {
        get("/api/user/search") {
            val query = call.parameters[QueryParams.PARAM_QUERY]
            if(query == null || query.isBlank()){
                call.respond(
                    HttpStatusCode.OK,
                    listOf<User>()
                )
                return@get
            }
            val searchResults = userService.searchForUsers(query, call.userId)
            call.respond(
                HttpStatusCode.OK,
                searchResults
            )
        }
    }
}

fun Route.getUserProfile(userService: UserService){
    authenticate {
        get("/api/user/profile") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID]
            if(userId == null || userId.isBlank()){
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val profileResponse = userService.getUserProfile(userId, call.userId)
            if(profileResponse == null){
                call.respond(HttpStatusCode.OK, BasicApiResponse(
                    successful = false,
                    message = ApiResponseMessages.USER_NOT_FOUND)
                )
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                profileResponse
            )
        }
    }
}

fun Route.getPostsForProfile(
    postService: PostService,
){
    authenticate {
        get("/api/user/posts") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

            val posts = postService.getPostsForProfile(call.userId, page, pageSize)
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}

fun Route.updateUserProfile(userService: UserService){
    val gson: Gson by inject ()
    authenticate {
        put("/api/user/update") {
            val multipart = call.receiveMultipart()
            var updateProfileRequest : UpdateProfileRequest ?= null
            var fileName: String? = null
            multipart.forEachPart { partData ->
                when(partData){
                    is PartData.FormItem -> {
                        if(partData.name == "update_profile_data"){
                            updateProfileRequest = gson.fromJson(
                                partData.value,
                                UpdateProfileRequest::class.java
                            )
                        }

                    }
                    is PartData.FileItem -> {

                        fileName = partData.save(Constants.PROFILE_PICTURE_PATH)
                    }
                    is PartData.BinaryItem -> Unit
                    is PartData.BinaryChannelItem -> Unit
                }

            }

            val profilePictureUrl = "${Constants.BASE_URL}profile_pictures/$fileName"

            updateProfileRequest?.let { request ->
                val updateKnowledged = userService.updateUser(
                    userId = call.userId,
                    profileImageUrl = profilePictureUrl,
                    updateProfileRequest = request
                )

                if(updateKnowledged){
                    call.respond(HttpStatusCode.OK, BasicApiResponse(
                        successful = true
                    ))
                }else{
                    File("${Constants.PROFILE_PICTURE_PATH}/$fileName").delete()
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        BasicApiResponse(
                            successful = false
                        )
                    )
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }


        }
    }
}