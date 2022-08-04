package com.cookiss.data.requests

data class UpdateProfileRequest(
    val username:String,
    val bio:String,
    val githubUrl:String,
    val instagramUrl:String,
    val linkedInUrl:String,
    val skills:List<String>
)
