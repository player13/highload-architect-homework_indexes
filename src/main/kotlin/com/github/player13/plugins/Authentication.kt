package com.github.player13.plugins

import com.github.player13.exception.UserNotFoundException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic

const val BASIC_AUTH_SCOPE = "basic"

fun Application.configureAuthentication() {
    install(Authentication) {
        basic(BASIC_AUTH_SCOPE) {
            realm = "social network"
            validate { credentials ->
                val user = try {
                    userRepository.readByUsername(credentials.name)
                } catch (e: UserNotFoundException) {
                    null
                }
                user?.takeIf { it.password == credentials.password }
                    ?.let { UserIdPrincipal(it.username) }
            }
        }
    }
}
