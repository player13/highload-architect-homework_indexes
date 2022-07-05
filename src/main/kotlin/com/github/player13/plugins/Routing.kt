package com.github.player13.plugins

import com.github.player13.api.UserDto
import com.github.player13.api.UserRegistrationDto
import com.github.player13.domain.User
import com.github.player13.exception.UserAlreadyExistsException
import com.github.player13.exception.UserNotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureRouting() {

    routing {
        post("/users") {
            val user = call.receive<UserRegistrationDto>()
            try {
                userService.register(user.toDomain())
                call.response.status(HttpStatusCode.Created)
            } catch (e: UserAlreadyExistsException) {
                call.response.status(HttpStatusCode.BadRequest)
            }
        }

        authenticate(BASIC_AUTH_SCOPE) {
            get("/users") {
                call.respond(userService.getAll().map { it.toDto() })
            }

            get("/users/{username}") {
                call.parameters["username"]
                    ?.takeUnless { it.isBlank() }
                    ?.let {
                        try {
                            val user = userService.getByUsername(it).toDto()
                            call.respond(user)
                        } catch (e: UserNotFoundException) {
                            call.response.status(HttpStatusCode.NotFound)
                        }
                    }
                    ?: call.response.status(HttpStatusCode.BadRequest)
            }
        }
    }
}

private fun User.toDto() =
    UserDto(
        username = username,
        firstName = firstName,
        lastName = lastName,
        age = age,
        city = city,
        interests = interests,
    )

private fun UserRegistrationDto.toDomain() =
    User(
        username = username,
        password = password,
        firstName = firstName,
        lastName = lastName,
        age = age,
        city = city,
        interests = interests,
    )
