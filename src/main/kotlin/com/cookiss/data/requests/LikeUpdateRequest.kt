package com.cookiss.data.requests

import com.cookiss.data.util.ParentType

data class LikeUpdateRequest(
    val parentId: String,
    val parentType: Int
)
