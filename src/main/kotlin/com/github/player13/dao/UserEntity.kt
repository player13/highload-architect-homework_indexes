package com.github.player13.dao

data class UserEntity(
    val id: Long? = null,
    val username: String,
    val password: String,

    val firstName: String,
    val lastName: String,
    val age: Short,

    val cityId: Long,
)
