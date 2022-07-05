package com.github.player13.api

data class UserDto(
    val username: String,

    val firstName: String,
    val lastName: String,
    val age: Short,
    val city: String,
    val interests: List<String>,
)
