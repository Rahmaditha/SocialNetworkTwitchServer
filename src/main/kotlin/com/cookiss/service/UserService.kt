package com.cookiss.service

import com.cookiss.data.models.User
import com.cookiss.data.repository.user.UserRepository
import com.cookiss.data.requests.CreateAccountRequest
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.util.ApiResponseMessages
import io.ktor.server.application.*
import io.ktor.server.response.*

class UserService(
    private val repository: UserRepository
) {

    suspend fun doesUserWithEmailExist(email: String): Boolean{
        return repository.getUserByEmail(email) != null
    }

    fun validateCreateAccountRequest(request: CreateAccountRequest): ValidationEvent{
        if(request.email.isBlank() || request.password.isBlank() || request.username.isBlank()){
            return ValidationEvent.ErrorFieldEmpty
        }
        return ValidationEvent.Success
    }

    sealed class ValidationEvent{
        object ErrorFieldEmpty : ValidationEvent()
        object Success: ValidationEvent()
    }

    suspend fun createUser(request: CreateAccountRequest){
        repository.createUser(
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
    }
}