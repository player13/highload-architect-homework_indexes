package com.github.player13.dao

import com.github.player13.domain.Sex

data class UserEntity(
    val id: Long? = null,
    val username: String,
    val encryptedPassword: String,

    val firstName: String,
    val lastName: String,
    val sex: Sex,
    val age: Short,

    val cityId: Long,
)
