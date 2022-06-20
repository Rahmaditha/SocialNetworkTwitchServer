package com.cookiss.di

import com.cookiss.repository.user.FakeUserRepository
import org.koin.dsl.module

internal val testModule = module{
    single { FakeUserRepository() }
}