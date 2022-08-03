package com.github.player13.dao

import com.github.player13.domain.Sex

data class UserEntity(
    val username: String,

    val firstName: String,
    val lastName: String,
    val sex: Sex,
    val age: Short,

    val city: String,
)
