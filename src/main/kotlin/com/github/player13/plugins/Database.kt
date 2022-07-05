package com.github.player13.plugins

import com.github.player13.config
import com.github.player13.dao.UserRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.pool.HikariPool
import io.ktor.server.application.Application

private lateinit var repository: UserRepository

val userRepository: UserRepository
    get() = repository

fun Application.configureDatabase() {
    val config = HikariConfig().apply {
        with(config["jdbc"]) {
            jdbcUrl = stringProperty("url")
            username = stringProperty("username")
            password = stringProperty("password")
            maximumPoolSize = 10
        }
    }
    val pool = HikariPool(
        config
    )

    repository = UserRepository(pool)
}
