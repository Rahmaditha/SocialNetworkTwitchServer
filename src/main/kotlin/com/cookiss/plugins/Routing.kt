package com.cookiss.plugins

import com.cookiss.data.repository.follow.FollowRepository
import com.cookiss.data.repository.post.PostRepository
import com.cookiss.data.repository.user.UserRepository
import com.cookiss.routes.*
import com.cookiss.service.FollowService
import com.cookiss.service.LikeService
import com.cookiss.service.PostService
import com.cookiss.service.UserService
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userService: UserService by inject()

    val followService: FollowService by inject()

    val posService: PostService by inject()

    val likeService: LikeService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    routing {
        //User routes
        createUserRoute(userService)
        loginUser(
            userService,
            jwtIssuer,
            jwtAudience,
            jwtSecret
        )

        //Following routes
        followUser(followService)
        unfollowUser(followService)

        //PostRoutes
        createPostRoute(posService, userService)
        getPostForFollows(posService, userService)
        deletePost(posService, userService)

        //Like Routes
        likeParent(likeService, userService)
        unlikeParent(likeService, userService)
    }
}
