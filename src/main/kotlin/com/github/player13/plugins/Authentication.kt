package com.github.player13.plugins

import com.github.player13.exception.UserNotFoundException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import org.mindrot.jbcrypt.BCrypt

const val BASIC_AUTH_SCOPE = "basic"

fun Application.configureAuthentication() {
    install(Authentication) {
        basic(BASIC_AUTH_SCOPE) {
            realm = "social network"
            validate { credentials ->
                val passwordHash = try {
                    userService.findPasswordHashByUsername(credentials.name)
                } catch (e: UserNotFoundException) {
                    null
                }

                passwordHash?.takeIf { BCrypt.checkpw(credentials.password, it) }
                    ?.let { UserIdPrincipal(credentials.name) }
            }
        }
    }
}
