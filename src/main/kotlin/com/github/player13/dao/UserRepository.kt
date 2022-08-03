package com.github.player13.dao

import com.github.player13.domain.User
import com.github.player13.exception.NoResultException
import com.github.player13.exception.UserAlreadyExistsException
import com.github.player13.exception.UserNotFoundException
import com.github.player13.utils.useReadOnly
import com.github.player13.utils.useWithoutAutoCommit
import com.zaxxer.hikari.pool.HikariPool
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

class UserRepository(
    private val pool: HikariPool,
) {

    fun create(user: User, encryptedPassword: String) {
        pool.connection.useWithoutAutoCommit { connection ->
            try {
                if (connection.existsUserByUsername(user.username)) {
                    throw UserAlreadyExistsException()
                }
                connection.insertUser(user.toUserEntity(), encryptedPassword)
                user.interests.takeUnless { it.isEmpty() }
                    ?.let {
                        connection.insertInterests(user.username, it)
                    }
                connection.commit()
            } catch (e: Exception) {
                connection.rollback()
                throw e
            }
        }
    }

    private fun Connection.existsUserByUsername(username: String): Boolean =
        prepareStatement("select exists(select null from user where username = ?) as result ").use { statement ->
            statement.setString(1, username)
            statement.execute()
            statement.resultSet.use {
                if (it.next()) {
                    it.getBoolean("result")
                } else {
                    throw NoResultException()
                }
            }
        }

    private fun Connection.insertUser(user: UserEntity, encryptedPassword: String) =
        prepareStatement(
            """
            insert into user (username, encrypted_password, first_name, last_name, sex, age, city)
            values (?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()
        ).use {
            it.setString(1, user.username)
            it.setString(2, encryptedPassword)
            it.setString(3, user.firstName)
            it.setString(4, user.lastName)
            it.setString(5, user.sex.name)
            it.setShort(6, user.age)
            it.setString(7, user.city)
            it.executeUpdate()
        }

    private fun Connection.insertInterests(username: String, interests: List<String>) =
        prepareStatement("insert into interest (username, description) values (?, ?)")
            .use { statement ->
                interests.forEach { interest ->
                    statement.setString(1, username)
                    statement.setString(2, interest)
                    statement.addBatch()
                }
                statement.executeBatch()
            }

    fun readAll(): Collection<User> =
        pool.connection.useReadOnly {
            it.createStatement().selectAllUsers().map { (user, interests) ->
                user.toDomain(interests)
            }
        }

    private fun Statement.selectAllUsers() =
        executeQuery(
            """
            select u.username,
                   u.first_name,
                   u.last_name,
                   u.sex,
                   u.age,
                   u.city,
                   i.description as interest
              from user u
                left join interest i on u.username = i.username
                order by u.username, i.id
            """.trimIndent()
        ).use { resultSet ->
            resultSet.toUserMap()
        }

    fun readByUsername(username: String): User? =
        pool.connection.useReadOnly {
            it.selectUserByUsername(username)
        }.firstNotNullOfOrNull { (user, interests) -> user.toDomain(interests) }

    private fun Connection.selectUserByUsername(username: String) =
        prepareStatement(
            """
            select u.username,
                   u.first_name,
                   u.last_name,
                   u.sex,
                   u.age,
                   u.city,
                   i.description as interest
              from user u
                left join interest i on u.username = i.username
                where u.username = ?
                order by u.username, i.id
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, username)
            statement.execute()
            statement.resultSet.use { resultSet ->
                resultSet.toUserMap()
            }
        }

    fun readByFirstAndLastNameLike(firstName: String, lastName: String): List<User> =
        pool.connection.useReadOnly {
            it.selectUserByFirstNameLikeAndLastNameLike(firstName, lastName).map { (user, interests) ->
                user.toDomain(interests)
            }
        }

    private fun Connection.selectUserByFirstNameLikeAndLastNameLike(
        firstName: String,
        lastName: String,
    ) =
        prepareStatement(
            """
            select u.username,
                   u.first_name,
                   u.last_name,
                   u.sex,
                   u.age,
                   u.city,
                   i.description as interest
              from user u
                left join interest i on u.username = i.username
                where u.first_name like ?
                and u.last_name like ?
                order by u.username, i.id
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, firstName)
            statement.setString(2, lastName)
            statement.execute()
            statement.resultSet.use { resultSet ->
                resultSet.toUserMap()
            }
        }

    private fun ResultSet.toUserMap() =
        buildMap {
            while (next()) {
                compute(toUserEntity()) { _, interests: MutableList<String>? ->
                    interests?.also { it.add(getInterest()) }
                        ?: run {
                            getInterest()
                                ?.let { mutableListOf(it) }
                                ?: mutableListOf()
                        }
                }
            }
        }

    private fun ResultSet.getInterest() =
        getString("interest")

    fun readUserPasswordHash(username: String): String {
        val foundUser = pool.connection.useReadOnly {
            it.selectPasswordHashByUsername(username)
        }
        return foundUser ?: throw UserNotFoundException()
    }

    private fun Connection.selectPasswordHashByUsername(username: String): String? =
        prepareStatement("select encrypted_password from user where username = ?").use { statement ->
            statement.setString(1, username)
            statement.execute()
            statement.resultSet.use {
                if (it.next()) {
                    it.getString("encrypted_password")
                } else {
                    null
                }
            }
        }

    private fun ResultSet.toUserEntity() =
        UserEntity(
            username = getString("username"),
            firstName = getString("first_name"),
            lastName = getString("last_name"),
            sex = enumValueOf(getString("sex")),
            age = getShort("age"),
            city = getString("city"),
        )

    private fun User.toUserEntity() =
        UserEntity(
            username = username,
            firstName = firstName,
            lastName = lastName,
            sex = sex,
            age = age,
            city = city,
        )

    private fun UserEntity.toDomain(
        interests: List<String>
    ) = User(
        username = username,
        firstName = firstName,
        lastName = lastName,
        sex = sex,
        age = age,
        city = city,
        interests = interests,
    )
}
