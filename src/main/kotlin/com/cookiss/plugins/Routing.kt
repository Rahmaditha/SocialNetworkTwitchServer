package com.cookiss.plugins

import com.cookiss.data.repository.follow.FollowRepository
import com.cookiss.data.repository.post.PostRepository
import com.cookiss.data.repository.user.UserRepository
import com.cookiss.routes.*
import com.cookiss.service.UserService
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val userService: UserService by inject()
    val followRepository: FollowRepository by inject()
    val postRepository: PostRepository by inject()
    routing {
        //User routes
        createUserRoute(userService)
        loginUser(userRepository)

        //Following routes
        followUser(userService)
        unfollowUser(followRepository)

        //PostRoutes
        createPostRoute(postRepository)
    }
}
