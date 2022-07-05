package com.github.player13.service

import com.github.player13.dao.UserRepository
import com.github.player13.domain.User

class UserService(
    private val userRepository: UserRepository
) {
    fun register(user: User) =
        userRepository.create(user)

    fun getAll() =
        userRepository.readAll()

    fun getByUsername(username: String) =
        userRepository.readByUsername(username)
}