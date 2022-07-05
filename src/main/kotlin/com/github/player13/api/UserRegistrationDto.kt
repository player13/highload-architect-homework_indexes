package com.github.player13.api

data class UserRegistrationDto(
    val username: String,
    val password: String,

    val firstName: String,
    val lastName: String,
    val age: Short,
    val city: String,
    val interests: List<String>,
)
