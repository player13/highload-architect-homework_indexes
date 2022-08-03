package com.github.player13.plugins

import com.github.player13.api.GetUserResponse
import com.github.player13.api.UserRegistrationRequest
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
import org.mindrot.jbcrypt.BCrypt

fun Application.configureRouting() {

    routing {
        post("/users") {
            val user = call.receive<UserRegistrationRequest>()
            try {
                userService.register(user.toDomain(), BCrypt.hashpw(user.password, BCrypt.gensalt()))
                call.response.status(HttpStatusCode.Created)
            } catch (e: UserAlreadyExistsException) {
                call.response.status(HttpStatusCode.BadRequest)
            }
        }

        authenticate(BASIC_AUTH_SCOPE) {
            get("/users") {
                val firstName = call.request.queryParameters["firstName"]
                val lastName = call.request.queryParameters["lastName"]
                if (firstName.isNullOrBlank() && lastName.isNullOrBlank()) {
                    call.respond(userService.getAll().map { it.toDto() })
                } else {
                    call.respond(userService.findByFirstAndLastNamePrefix(firstName ?: "", lastName ?: ""))
                }
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
    GetUserResponse(
        username = username,
        firstName = firstName,
        lastName = lastName,
        sex = sex,
        age = age,
        city = city,
        interests = interests,
    )

private fun UserRegistrationRequest.toDomain() =
    User(
        username = username,
        firstName = firstName,
        lastName = lastName,
        sex = sex,
        age = age,
        city = city,
        interests = interests,
    )
