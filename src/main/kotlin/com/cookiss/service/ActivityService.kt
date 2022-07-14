package com.cookiss.service

import com.cookiss.data.models.Activity
import com.cookiss.data.repository.activity.ActivityRepository
import com.cookiss.data.repository.comment.CommentRepository
import com.cookiss.data.repository.post.PostRepository
import com.cookiss.data.util.ActivityType
import com.cookiss.data.util.ParentType
import com.cookiss.util.Constants

class ActivityService(
    private val repository: ActivityRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository
) {

    suspend fun getActivitiesForUser(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_ACTIVITY_PAGE_SIZE
    ): List<Activity> {
        return repository.getActivitiesForUser(userId, page, pageSize)
    }

    suspend fun addCommentActivity(
        byUserId: String,
        postId: String
    ): Boolean {
        val userIdOfPost = postRepository.getPost(postId)?.userId ?: return false

        //if user who commented is the same as user who posted
        if (byUserId == userIdOfPost) {
            return false
        }

        repository.createActivity(
            Activity(
                timestamp = System.currentTimeMillis(),
                byUserId = byUserId,
                toUserId = userIdOfPost,
                type = ActivityType.CommentedOnPost.type,
                parentId = postId
            )
        )
        return true
    }

    suspend fun addLikeActivity(
        byUserId: String,
        parentType: ParentType,
        parentId: String
    ): Boolean {
        val toUserId = when (parentType) {
            is ParentType.Post -> {
                postRepository.getPost(parentId)?.userId
            }
            is ParentType.Comment -> {
                commentRepository.getComment(parentId)?.userId
            }
            is ParentType.None -> return false
        } ?: return false

        if(byUserId == toUserId){
            return false
        }

        repository.createActivity(
            Activity(
                timestamp = System.currentTimeMillis(),
                byUserId = byUserId,
                toUserId = toUserId,
                type = when (parentType) {
                    is ParentType.Post -> ActivityType.LikedPost.type
                    is ParentType.Comment -> ActivityType.LikedComment.type
                    else -> ActivityType.LikedPost.type
                },
                parentId = parentId
            )
        )
        return true
    }

    suspend fun createActivity(activity: Activity) {
        repository.createActivity(activity)
    }

    suspend fun deleteActivity(activityId: String): Boolean {
        return repository.deleteActivity(activityId)
    }
}
//1:29:41