package com.github.player13.service

import com.github.player13.dao.UserRepository
import com.github.player13.domain.User
import com.github.player13.exception.UserNotFoundException

class UserService(
    private val userRepository: UserRepository,
) {
    fun register(user: User, encryptedPassword: String) =
        userRepository.create(user, encryptedPassword)

    fun getAll() =
        userRepository.readAll()

    fun getByUsername(username: String) =
        userRepository.readByUsername(username)
            ?: throw UserNotFoundException()

    fun findByFirstAndLastNamePrefix(firstNamePrefix: String, lastNamePrefix: String) =
        userRepository.readByFirstAndLastNameLike("$firstNamePrefix%", "$lastNamePrefix%")

    fun findPasswordHashByUsername(username: String) =
        userRepository.readUserPasswordHash(username)
}