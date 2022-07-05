package com.github.player13

import com.github.player13.plugins.configureAuthentication
import com.github.player13.plugins.configureDatabase
import com.github.player13.plugins.configureRouting
import com.github.player13.plugins.configureSerialization
import com.github.player13.plugins.configureServices
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>): Unit =
    EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureAuthentication()
    configureDatabase()
    configureRouting()
    configureSerialization()
    configureServices()
}
