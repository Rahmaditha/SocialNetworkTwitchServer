package com.cookiss.data.repository.follow

import com.cookiss.data.models.Following
import com.cookiss.data.models.User
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class FollowRepositoryImpl(
    private val db: CoroutineDatabase
) : FollowRepository{

    private val following = db.getCollection<Following>()
    private val users = db.getCollection<User>()

    override suspend fun followUserIfExists(
        followingUserId: String,
        followedUserId: String
    ): Boolean {
        val doesFollowingUserExist = users.findOneById(followingUserId) != null
        val doesFollowedUserExist = users.findOneById(followedUserId) != null

        if(!doesFollowingUserExist || !doesFollowedUserExist){
            return false
        }
        following.insertOne(
            Following(followingUserId, followedUserId)
        )
        return true
    }

    override suspend fun unfollowUserIfExist(followingUserId: String, followedUserId: String): Boolean {
        val deleteResult = following.deleteOne(
            and(
                Following::followingUserId eq followingUserId,
                Following::followedUserId eq followedUserId
            )
        )
        return deleteResult.deletedCount > 0
    }

    //Menit 1:28:15
}