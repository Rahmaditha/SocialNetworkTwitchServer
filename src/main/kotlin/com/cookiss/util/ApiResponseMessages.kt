package com.cookiss.util

object ApiResponseMessages {

    const val USER_ALREADY_EXISTS = "A user with this email already exists."
    const val FIELDS_BLANK = "The fields may not be empty."
    const val INVALID_CREDENTIALS = "Oops, that is not correct, please try again"
    const val USER_NOT_FOUND = "User not found"
    const val COMMENT_TOO_LONG = "The comment length must not exceed ${Constants.MAX_COMMENT_LENGTH} characters"

}