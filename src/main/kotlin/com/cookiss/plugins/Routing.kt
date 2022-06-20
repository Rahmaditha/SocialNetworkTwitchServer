package com.cookiss.plugins

import com.cookiss.data.repository.user.UserRepository
import com.cookiss.routes.createUserRoute
import com.cookiss.routes.loginUser
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()

    routing {
        createUserRoute(userRepository)
        loginUser(userRepository)
    }
}
