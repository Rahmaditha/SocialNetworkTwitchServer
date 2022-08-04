package com.cookiss.di

import com.cookiss.data.repository.activity.ActivityRepository
import com.cookiss.data.repository.activity.ActivityRepositoryImpl
import com.cookiss.data.repository.comment.CommentRepository
import com.cookiss.data.repository.comment.CommentRepositoryImpl
import com.cookiss.data.repository.follow.FollowRepository
import com.cookiss.data.repository.follow.FollowRepositoryImpl
import com.cookiss.data.repository.likes.LikesRepository
import com.cookiss.data.repository.likes.LikesRepositoryImpl
import com.cookiss.data.repository.post.PostRepository
import com.cookiss.data.repository.post.PostRepositoryImpl
import com.cookiss.data.repository.user.UserRepository
import com.cookiss.data.repository.user.UserRepositoryImpl
import com.cookiss.service.*
import com.cookiss.util.Constants
import com.google.gson.Gson
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        val client = KMongo.createClient().coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }

    single<UserRepository> {
        UserRepositoryImpl(get())
    }

    single<FollowRepository> {
        FollowRepositoryImpl(get())
    }

    single<PostRepository> {
        PostRepositoryImpl(get())
    }

    single<LikesRepository> {
        LikesRepositoryImpl(get())
    }

    single<CommentRepository> {
        CommentRepositoryImpl(get())
    }

    single<ActivityRepository> {
        ActivityRepositoryImpl(get())
    }

    single {
        UserService(get(), get())
    }
    single {
        FollowService(get())
    }
    single {
        PostService(get())
    }
    single {
        LikeService(get())
    }
    single {
        CommentService(get(), get())
    }
    single {
        ActivityService(get(), get(), get())
    }

    single{
        Gson()
    }

}