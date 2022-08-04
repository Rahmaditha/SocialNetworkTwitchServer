package com.cookiss.routes

import com.cookiss.data.requests.CreatePostRequest
import com.cookiss.data.requests.UpdateProfileRequest
import com.cookiss.data.responses.BasicApiResponse
import com.cookiss.service.CommentService
import com.cookiss.service.LikeService
import com.cookiss.service.PostService
import com.cookiss.util.ApiResponseMessages
import com.cookiss.util.Constants
import com.cookiss.util.QueryParams
import com.cookiss.util.save
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*

fun Route.createPostRoute(
    postService: PostService
){
    val gson by inject<Gson>()
    authenticate {
        post("/api/post/create"){
            val multipart = call.receiveMultipart()
            var createPostRequest : CreatePostRequest?= null
            var fileName: String? = null
            multipart.forEachPart { partData ->
                when(partData){
                    is PartData.FormItem -> {
                        if(partData.name == "post_data"){
                            createPostRequest = gson.fromJson(
                                partData.value,
                                CreatePostRequest::class.java
                            )
                        }

                    }
                    is PartData.FileItem -> {
                        fileName = partData.save(Constants.POST_PICTURE_PATH)
                    }
                    is PartData.BinaryItem -> Unit
                    is PartData.BinaryChannelItem -> Unit
                }

            }

            val postPictureUrl = "${Constants.BASE_URL}post_pictures/$fileName"

            createPostRequest?.let { request ->
                val createPostAcknowledged = postService.createPost(
                    request = request,
                    userId   = call.userId,
                    imageUrl = postPictureUrl
                )

                if(createPostAcknowledged){
                    call.respond(HttpStatusCode.OK, BasicApiResponse(
                        successful = true
                    ))
                }else{
                    File("${Constants.POST_PICTURE_PATH}/$fileName").delete()
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        BasicApiResponse(
                            successful = false
                        )
                    )
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
        }
    }
}

fun Route.getPostForFollows(
    postService: PostService,
){
    authenticate {
        get("/api/post/get") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

            val posts = postService.getPostsForFollows(call.userId, page, pageSize)
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}

fun Route.deletePost(
    postService: PostService,
    commentService: CommentService,
    likeService: LikeService
){
    authenticate {
        delete("/api/post/delete"){
            val postId = call.parameters["postId"] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val post = postService.getPost(postId)
            print(post)
            if(post == null){
                call.respond(
                    HttpStatusCode.NotFound
                )
                return@delete
            }
            if (post.userId == call.userId) {
                postService.deletePost(postId)
                likeService.deleteLikesorParent(postId)
                commentService.deleteCommentsForPost(postId)
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }

        }
    }
}
