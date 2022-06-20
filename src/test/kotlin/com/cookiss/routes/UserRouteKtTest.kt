package com.cookiss.routes

import com.cookiss.data.models.User
import com.cookiss.data.requests.CreateAccountRequest
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.util.ApiResponseMessages
import com.cookiss.di.testModule
import com.cookiss.plugins.configureSerialization
import com.cookiss.repository.user.FakeUserRepository
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class UserRouteKtTest: KoinTest{
    private val userRepository by inject<FakeUserRepository>()

    private val gson = Gson()
    @BeforeTest
    fun setUp(){
        startKoin {
            modules(testModule)
        }
    }

    @AfterTest
    fun tearDown(){
        stopKoin()
    }

    @Test
    fun `Create user, no body attached, responds with BadRequest`(){
        withTestApplication(
            moduleFunction = {
                install(Routing){
                    createUserRoute(userRepository)
                }
            }
        ){
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "api/user/create"
            )

            assertThat(request.response.status()).isEqualTo(HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun `Create user, user already exist, responds with unsuccessful`() = runBlocking {
        val user = User(
            email = "test@test.com",
            username = "test",
            password = "test",
            profileImageUrl = "",
            bio = "",
            githubUrl = null,
            instagramUrl = null,
            linkedInUrl = null
        )

        userRepository.createUser(user)
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing){
                    createUserRoute(userRepository)
                }
            }
        ){
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "api/user/create"
            ){
                addHeader("Content-Type", "application/json")
                val request = CreateAccountRequest(
                    email = "test@test.com",
                    username = "asdf",
                    password = "asdf"
                )
                setBody(gson.toJson(request))
            }

            val response = gson.fromJson<BasicApiResponse>(
                request.response.content ?: "",
                BasicApiResponse::class.java
            )
            assertThat(response.successful).isFalse()
            assertThat(response.message).isEqualTo(ApiResponseMessages.USER_ALREADY_EXISTS)
        }
    }

    @Test
    fun `Create user, email is empty, responds with unsuccessful`() = runBlocking {
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing){
                    createUserRoute(userRepository)
                }
            }
        ){
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "api/user/create"
            ){
                addHeader("Content-Type", "application/json")
                val request = CreateAccountRequest(
                    email = "",
                    username = "",
                    password = ""
                )
                setBody(gson.toJson(request))
            }

            val response = gson.fromJson<BasicApiResponse>(
                request.response.content ?: "",
                BasicApiResponse::class.java
            )
            assertThat(response.successful).isFalse()
            assertThat(response.message).isEqualTo(ApiResponseMessages.FIELDS_BLANK)
        }
    }

    @Test
    fun `Create user, valid data, responds with successful`() = runBlocking {
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing){
                    createUserRoute(userRepository)
                }
            }
        ){
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "api/user/create"
            ){
                addHeader("Content-Type", "application/json")
                val request = CreateAccountRequest(
                    email = "test@test.com",
                    username = "test",
                    password = "test"
                )
                setBody(gson.toJson(request))
            }

            val response = gson.fromJson<BasicApiResponse>(
                request.response.content ?: "",
                BasicApiResponse::class.java
            )
            assertThat(response.successful).isTrue()

            runBlocking {
                val isUserInDb = userRepository.getUserByEmail("test@test.com") != null
                assertThat(isUserInDb).isTrue()
            }
        }
    }
}