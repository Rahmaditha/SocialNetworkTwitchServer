package com.cookiss.data.repository.likes

import com.cookiss.data.models.Like
import com.cookiss.data.models.User
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class LikesRepositoryImpl(
    db: CoroutineDatabase
) : LikesRepository {

    val likes = db.getCollection<Like>()
    private val users = db.getCollection<User>()
    override suspend fun likeParent(userId: String, parentId: String): Boolean {
        val doesUserExist = users.findOneById(userId) != null
        if(doesUserExist){
            likes.insertOne(
                Like(
                    userId = userId,
                    parentId = parentId
                )
            )
            return true
        }else{
            return false
        }
    }

    override suspend fun unlikeParent(userId: String, parentId: String): Boolean {
        val doesUserExist = users.findOneById(userId) != null
        return if(doesUserExist){
            likes.deleteOne(
                and(
                    Like::userId eq userId,
                    Like::parentId eq parentId
                )
            )
            true
        }else{
            false
        }
    }

    override suspend fun deleteLikesForParent(parentId: String) {
        likes.deleteMany(Like::parentId eq parentId)
    }
}

//1:41:05