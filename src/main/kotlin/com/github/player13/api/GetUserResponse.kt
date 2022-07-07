package com.github.player13.api

import com.github.player13.domain.Sex

data class GetUserResponse(
    val username: String,
    val firstName: String,
    val lastName: String,
    val sex: Sex,
    val age: Short,
    val city: String,
    val interests: List<String>,
)
