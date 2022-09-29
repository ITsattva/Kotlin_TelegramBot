package com.github.kotlintelegrambot.echo.db

import com.github.kotlintelegrambot.echo.entity.User
import com.github.kotlintelegrambot.echo.util.Mapper
import com.github.kotlintelegrambot.echo.util.UserUtils
import com.github.kotlintelegrambot.entities.User as TelegramUser
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path

object Database {
    private val dbFile = File("database/db")
    private var dbMapIdReputation = mutableMapOf<Long, Int>()
    private var dbMapIdName = mutableMapOf<Long, String>()

    private fun createDB() {
        if (!dbFile.exists()) {
            val directoryDB = File("database")
            if (!directoryDB.exists()) Files.createDirectory(Path.of("database"))

            Files.createFile(Path.of("database/db"))
        }
    }

    fun addUser(tgUser: TelegramUser?) {
        val fullName = UserUtils.getFullName(tgUser)
        val user = User(fullName, tgUser?.id?: 0)
        dbMapIdReputation[user.id] = user.reputation
        dbMapIdName[user.id] = user.name
        val output = FileOutputStream(dbFile, true)
        val userInString = Mapper.userToString(user)
        println(userInString)
        output.write(userInString.toByteArray())
    }

    fun init(): Database {
        createDB()
        dbMapIdReputation = getUsers().associate { it.id to it.reputation } as MutableMap<Long, Int>
        dbMapIdName = getUsers().associate { it.id to it.name } as MutableMap<Long, String>
        return this
    }

    private fun getUsers(): List<User> {
        with(Mapper) {
            val raw = getRawData(dbFile)
            return parseUsers(raw)
        }
    }

    fun getStat(): String {
        var result = ""
        Mapper.usersToSortedString(getUsers()).forEach { result += it + "\n" }
        return result
    }

    private fun updateReputation(id: Long, reputation: Int?) {
        val regex = Regex("${id}=-*\\d")
        val oldData = Mapper.getRawData(dbFile)
        val newData = regex.replace(oldData, "$id=$reputation")
        updateDB(newData)
    }

    fun increaseReputation(user: TelegramUser?) {
        val userId = user?.id ?: 0

        dbMapIdReputation[userId] = dbMapIdReputation[userId]!! + 1
        val reputation = dbMapIdReputation[userId]

        updateReputation(userId, reputation)
    }

    fun decreaseReputation(user: TelegramUser?) {
        val userId = user?.id ?: 0

        dbMapIdReputation[userId] = dbMapIdReputation[userId]!! - 1
        val reputation = dbMapIdReputation[userId]

        updateReputation(userId, reputation)
    }

    fun getReputation(id: Long) = dbMapIdReputation[id]
    fun getUserName(id: Long) = dbMapIdName[id]

    private fun updateDB(newData: String) {
        val output = FileOutputStream(dbFile, false)
        output.write(newData.toByteArray())
        output.flush()
    }

    fun isUserInDB(user: TelegramUser?): Boolean {
        val id = user?.id ?: 0

        return dbMapIdReputation.containsKey(id)
    }

}