package com.github.player13.plugins

import com.github.player13.service.UserService
import io.ktor.server.application.Application

private lateinit var service: UserService

val userService: UserService
    get() = service

fun Application.configureServices() {
    service = UserService(userRepository)
}
