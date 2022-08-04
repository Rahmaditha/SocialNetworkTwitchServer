package com.cookiss.service

import com.cookiss.data.models.User
import com.cookiss.data.repository.follow.FollowRepository
import com.cookiss.data.repository.user.UserRepository
import com.cookiss.data.requests.CreateAccountRequest
import com.cookiss.data.requests.LoginRequest
import com.cookiss.data.requests.UpdateProfileRequest
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.data.responses.ProfileResponse
import com.cookiss.data.responses.UserResponseItem
import com.cookiss.util.ApiResponseMessages
import com.cookiss.util.Constants
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.litote.kmongo.replaceOne

class UserService(
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {

    suspend fun doesUserWithEmailExist(email: String): Boolean{
        return userRepository.getUserByEmail(email) != null
    }

    suspend fun getUserProfile(userId: String, callerUserId: String): ProfileResponse?{
        val user = userRepository.getUserById(userId) ?: return null
        return ProfileResponse(
            username = user.username,
            bio = user.bio,
            followerCount = user.followerCount,
            followingCount = user.followingCount,
            postCount = user.postCount,
            profilePictureUrl = user.profileImageUrl,
            topSkillLinks = user.skills,
            githubUrl = user.githubUrl,
            instagramUrl = user.instagramUrl,
            linkedInUrl = user.linkedInUrl,
            isOwnProfile = userId == callerUserId,
            isFollowing = if(userId != callerUserId){
                followRepository.doesUserFollow(callerUserId, userId)
            }else{
                false
            }
        )
    }

    suspend fun searchForUsers(query: String, userId: String): List<UserResponseItem>{
        val users = userRepository.searchForUsers(query)
        val followsByUser = followRepository.getFollowsByUser(userId)
        return users.map { user ->
            val isFollowing = followsByUser.find {
                it.followedUserId == user.id
            } != null
            UserResponseItem(
                userName = user.username,
                profilePictureUrl = user.profileImageUrl,
                bio = user.bio,
                isFollowing = true
            )
        }
    }

    suspend fun getUserByEmail(email: String): User?{
        return userRepository.getUserByEmail(email)
    }

    fun isValidPassword(enteredPassword: String, actualPassword: String): Boolean{
        return enteredPassword == actualPassword
    }

    suspend fun updateUser(
        userId: String,
        profileImageUrl:String,
        updateProfileRequest: UpdateProfileRequest
    ): Boolean{
        return userRepository.updateUser(userId, profileImageUrl, updateProfileRequest)
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
        userRepository.createUser(
            User(
                email = request.email,
                username = request.username,
                password = request.password,
                profileImageUrl = Constants.DEFAULT_PROFILE_PICTURE_PATH,
                bannerUrl = Constants.DEFAULT_BANNER_IMAGE_PATH,
                bio = "",
                githubUrl = null,
                instagramUrl = null,
                linkedInUrl = null,

            )
        )
    }

    suspend fun doesPasswordForUserMatch(request: LoginRequest): Boolean {
        return userRepository.doesPasswordForUserMatch(request.email, request.password)
    }
}