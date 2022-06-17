package com.cookiss.di

import com.cookiss.controller.user.UserController
import com.cookiss.controller.user.UserControllerImpl
import com.cookiss.data.util.Constants
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        val client = KMongo.createClient().coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }

    single<UserController> {
        UserControllerImpl(get())
    }
}