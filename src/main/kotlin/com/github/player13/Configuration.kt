package com.github.player13

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.Application
import io.ktor.server.config.HoconApplicationConfig

val Application.config by lazy {
    ConfigurationContainer(HoconApplicationConfig(ConfigFactory.load()))
}

class ConfigurationContainer(
    private val config: HoconApplicationConfig,
    private val prefix: String? = null,
) {
    operator fun get(key: String) =
        ConfigurationContainer(
            config,
            key.withPrefix(),
        )

    fun stringProperty(key: String) =
        config.propertyOrNull(key.withPrefix())?.getString()

    private fun String.withPrefix() =
        prefix.takeUnless { it.isNullOrBlank() }?.let { "$prefix.$this" } ?: this
}