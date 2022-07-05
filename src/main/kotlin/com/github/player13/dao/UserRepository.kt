package com.github.player13.dao

import com.github.player13.domain.User
import com.github.player13.exception.CityNotFoundException
import com.github.player13.exception.NotInsertedException
import com.github.player13.exception.UserAlreadyExistsException
import com.github.player13.exception.UserNotFoundException
import com.zaxxer.hikari.pool.HikariPool
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

class UserRepository(
    private val pool: HikariPool
) {

    fun create(user: User) {
        pool.connection.use { connection ->
            val existingUser = connection.selectUserByUsername(user.username)
            if (existingUser != null) {
                throw UserAlreadyExistsException()
            }

            val statement = connection.createStatement()
            val cityId = connection.selectCityByName(user.city)?.id
                ?: run {
                    connection.insert(user.toCityEntity())
                    statement.selectLastInsertedId()
                }

            val userId = run {
                connection.insert(user.toUserEntity(cityId))
                statement.selectLastInsertedId()
            }

            user.toInterestEntities(userId)
                .takeUnless { it.isEmpty() }
                ?.let {
                    connection.insert(it)
                }
        }
    }


    fun readAll(): List<User> =
        pool.connection.use { connection ->
            val statement = connection.createStatement()
            val users = statement.selectAllUsers()
            val cities = statement.selectAllCities().associateBy { it.id }
            val interests = statement.selectAllInterests().groupBy { it.userId }
            users.map { user: UserEntity ->
                User(
                    username = user.username,
                    password = user.password,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    age = user.age,
                    city = cities[user.cityId]?.name ?: throw CityNotFoundException(),
                    interests = interests[user.id]?.map { it.description } ?: listOf(),
                )
            }
        }

    fun readByUsername(username: String): User =
        pool.connection.use { connection ->
            val user = connection.selectUserByUsername(username) ?: throw UserNotFoundException()
            val city = connection.selectCityById(user.cityId) ?: throw CityNotFoundException()
            val interests = connection.selectInterestsByUserId(user.id!!)
            User(
                username = user.username,
                password = user.password,
                firstName = user.firstName,
                lastName = user.lastName,
                age = user.age,
                city = city.name,
                interests = interests.map { it.description },
            )
        }

    private fun Connection.insert(user: UserEntity) {
        prepareStatement(
            """
            insert into user (username, password, first_name, last_name, age, city_id)
            values (?, ?, ?, ?, ?, ?)
            """.trimIndent()
        ).use {
            it.setString(1, user.username)
            it.setString(2, user.password)
            it.setString(3, user.firstName)
            it.setString(4, user.lastName)
            it.setShort(5, user.age)
            it.setLong(6, user.cityId)
            it.executeUpdate()
        }
    }

    private fun Connection.insert(city: CityEntity) {
        prepareStatement("insert into city (name) values (?)").use {
            it.setString(1, city.name)
            it.executeUpdate()
        }
    }

    private fun Connection.insert(interests: List<InterestEntity>) {
        prepareStatement("insert into interest (user_id, description) values (?, ?)").use { statement ->
            interests.forEach {
                statement.setLong(1, it.userId)
                statement.setString(2, it.description)
                statement.addBatch()
            }
            statement.executeBatch()
        }
    }

    private fun Statement.selectAllCities(): List<CityEntity> =
        executeQuery("select * from city").use {
            buildList {
                while (it.next()) {
                    add(it.toCityEntity())
                }
            }
        }

    private fun Connection.selectCityByName(name: String): CityEntity? =
        prepareStatement("select * from city where name = ?").use { statement ->
            statement.setString(1, name)
            statement.execute()
            statement.resultSet.use {
                if (it.next()) {
                    it.toCityEntity()
                } else {
                    null
                }
            }
        }

    private fun Connection.selectCityById(id: Long): CityEntity? =
        prepareStatement("select * from city where id = ?").use { statement ->
            statement.setLong(1, id)
            statement.execute()
            statement.resultSet.use {
                if (it.next()) {
                    it.toCityEntity()
                } else {
                    null
                }
            }
        }

    private fun Statement.selectAllUsers(): List<UserEntity> =
        executeQuery("select * from user").use {
            buildList {
                while (it.next()) {
                    add(it.toUserEntity())
                }
            }
        }

    private fun Connection.selectUserByUsername(username: String): UserEntity? =
        prepareStatement("select * from user where username = ?").use { statement ->
            statement.setString(1, username)
            statement.execute()
            statement.resultSet.use {
                if (it.next()) {
                    it.toUserEntity()
                } else {
                    null
                }
            }
        }

    private fun Statement.selectAllInterests(): List<InterestEntity> =
        executeQuery("select * from interest").use {
            buildList {
                while (it.next()) {
                    add(it.toInterestEntity())
                }
            }
        }

    private fun Connection.selectInterestsByUserId(userId: Long): List<InterestEntity> =
        prepareStatement("select * from interest where user_id = ?").use { statement ->
            statement.setLong(1, userId)
            statement.execute()
            statement.resultSet.use {
                buildList {
                    while (it.next()) {
                        add(it.toInterestEntity())
                    }
                }
            }
        }

    private fun Statement.selectLastInsertedId(): Long =
        executeQuery("select last_insert_id()").use {
            if (it.next()) {
                it.getLong(1)
            } else {
                throw NotInsertedException()
            }
        }

    private fun ResultSet.toUserEntity() =
        UserEntity(
            id = getLong("id"),
            username = getString("username"),
            password = getString("password"),
            firstName = getString("first_name"),
            lastName = getString("last_name"),
            age = getShort("age"),
            cityId = getLong("city_id"),
        )

    private fun ResultSet.toCityEntity() =
        CityEntity(
            id = getLong("id"),
            name = getString("name"),
        )

    private fun ResultSet.toInterestEntity() =
        InterestEntity(
            id = getLong("id"),
            userId = getLong("user_id"),
            description = getString("description"),
        )

    private fun User.toUserEntity(cityId: Long) =
        UserEntity(
            username = username,
            password = password,
            firstName = firstName,
            lastName = lastName,
            age = age,
            cityId = cityId,
        )

    private fun User.toCityEntity() =
        CityEntity(
            name = city,
        )

    private fun User.toInterestEntities(userId: Long) =
        interests.map {
            InterestEntity(
                userId = userId,
                description = it
            )
        }

}
