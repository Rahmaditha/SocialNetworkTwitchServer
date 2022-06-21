package com.cookiss.plugins

import com.cookiss.data.repository.follow.FollowRepository
import com.cookiss.data.repository.user.UserRepository
import com.cookiss.routes.createUserRoute
import com.cookiss.routes.followUser
import com.cookiss.routes.loginUser
import com.cookiss.routes.unfollowUser
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val followRepository: FollowRepository by inject()
    routing {
        //User routes
        createUserRoute(userRepository)
        loginUser(userRepository)

        //Following routes
        followUser(followRepository)
        unfollowUser(followRepository)
    }
}
