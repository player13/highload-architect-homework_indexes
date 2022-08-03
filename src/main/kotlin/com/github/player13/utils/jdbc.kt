package com.github.player13.utils

import java.sql.Connection

fun <T> Connection.useWithoutAutoCommit(block: (Connection) -> T): T =
    use {
        if (!it.autoCommit) {
            block(it)
        } else {
            it.autoCommit = false
            try {
                block(it)
            } finally {
                it.autoCommit = true
            }
        }
    }

fun <T> Connection.useReadOnly(block: (Connection) -> T): T =
    use {
        if (it.isReadOnly) {
            block(it)
        } else {
            it.isReadOnly = true
            try {
                block(it)
            } finally {
                it.isReadOnly = false
            }
        }
    }
