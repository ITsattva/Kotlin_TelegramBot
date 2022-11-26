package com.github.kotlintelegrambot.echo.db

import com.github.kotlintelegrambot.echo.entity.User
import com.github.kotlintelegrambot.echo.util.Mapper
import com.github.kotlintelegrambot.echo.util.UserUtils
import com.github.kotlintelegrambot.entities.User as TelegramUser
import java.sql.Connection
import java.sql.DriverManager

object UsersDB {

    private lateinit var connection: Connection
    private const val USER_TABLE = "users"
    private const val QUESTION_SQL = "sql"
    private const val FILE = "users.db"
    var directory = "database"
        set(value) {
            connection = DriverManager.getConnection("jdbc:sqlite:$value/$FILE")
            field = value
        }

    fun addUser(user: TelegramUser?) {
        val name = UserUtils.getFullName(user)
        val id = user?.id ?: 0

        val statement = connection.prepareStatement("INSERT INTO $USER_TABLE (id, name, reputation) VALUES (?, ?, ?)")
        statement.setLong(1, id)
        statement.setString(2, name)
        statement.setInt(3, 0)
        statement.executeUpdate()
        statement.close()
    }

    fun init(): UsersDB {
        println("UsersDB init")
        connection = DriverManager.getConnection("jdbc:sqlite:$directory/$FILE")
        return this
    }

    private fun getUsers(): List<User> {
        val statement = connection.prepareStatement("SELECT * FROM $USER_TABLE")
        val resultSet = statement.executeQuery()
        val userList = Mapper.getUsersList(resultSet)
        statement.close()

        return userList
    }

    fun getStat(): String {
        var result = ""
        Mapper.usersToSortedString(getUsers()).forEach { result += it + "\n" }
        return result
    }

    private fun updateReputation(id: Long, reputation: Int) {
        val statement = connection.prepareStatement("UPDATE $USER_TABLE SET reputation=? WHERE id=?")
        statement.setInt(1, reputation)
        statement.setLong(2, id)
        statement.executeUpdate()
        statement.close()
    }

    fun increaseReputation(user: TelegramUser?) {
        val id = user?.id ?: 0
        val reputation = getReputation(id)+1

        updateReputation(id, reputation)
    }

    fun decreaseReputation(user: TelegramUser?) {
        val id = user?.id ?: 0
        val reputation = getReputation(id)-1

        updateReputation(id, reputation)
    }

    fun getReputation(id: Long): Int {
        val statement = connection.prepareStatement("SELECT reputation FROM $USER_TABLE WHERE id=?")
        statement.setLong(1, id)
        val resultSet = statement.executeQuery()
        val reputation = resultSet.getInt(1)
        println(reputation)
        statement.close()

        return reputation
    }

    fun getUserName(id: Long): String {
        val statement = connection.prepareStatement("SELECT name FROM $USER_TABLE WHERE id=?")
        statement.setLong(1, id)
        val resultSet = statement.executeQuery()
        val name = resultSet.getString(1)
        statement.close()

        return name
    }

    fun isUserInDB(user: TelegramUser?): Boolean {
        val id = user?.id ?: 0
        val statement = connection.prepareStatement("SELECT name FROM $USER_TABLE WHERE id=?")
        statement.setLong(1, id)
        val resultSet = statement.executeQuery()
        val name = resultSet.getString(1)
        statement.close()

        return name != null
    }

}