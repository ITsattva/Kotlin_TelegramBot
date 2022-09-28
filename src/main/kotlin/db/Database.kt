package com.github.kotlintelegrambot.echo.db

import com.github.kotlintelegrambot.echo.entity.User
import com.github.kotlintelegrambot.echo.util.Mapper
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.file.Files
import java.nio.file.Path

object Database {
    private val dbFile = File("database/db")
    private var dbMap = mutableMapOf<Long, Int>()

    private fun createDB() {
        if (!dbFile.exists()) {
            println("db doesn't exist")
            val directoryDB = File("database")
            if (!directoryDB.exists()) Files.createDirectory(Path.of("database"))

            Files.createFile(Path.of("database/db"))
            println("db has been created!")
        }
    }

    fun addUser(user: User) {
        dbMap[user.id] = user.reputation
        val output = FileOutputStream(dbFile, true)
        val userInString = Mapper.userToString(user)
        output.write(userInString.toByteArray())
    }

    fun init(): Database {
        createDB()
        dbMap = getUsers() as MutableMap<Long, Int>
        return this
    }

    private fun getUsers(): Map<Long, Int> {
        with(Mapper) {
            val raw = getRawData(dbFile)
            val users = parseUsers(raw)
            return usersToHashMap(users)
        }
    }

    fun updateReputation(id: Long, reputation: Int) {
        val regex = Regex("${id}=\\d")
        val oldData = Mapper.getRawData(dbFile)
        val newData = regex.replace(oldData, "$id=$reputation")
        updateDB(newData)
    }

    fun increaseReputation(id: Long) {
        dbMap[id] = dbMap[id]!! + 1

        val regex = Regex("${id}=\\d")
        val oldData = Mapper.getRawData(dbFile)
        val newData = regex.replace(oldData, "$id=${dbMap[id]}")
        updateDB(newData)
    }

    fun decreaseReputation(id: Long) {
        dbMap[id] = dbMap[id]!! - 1

        val regex = Regex("${id}=\\d")
        val oldData = Mapper.getRawData(dbFile)
        val newData = regex.replace(oldData, "$id=${dbMap[id]}")
        updateDB(newData)
    }

    fun getReputation(id: Long) = dbMap[id]

    private fun updateDB(newData: String) {
        val output = FileOutputStream(dbFile, false)
        output.write(newData.toByteArray())
        output.flush()
    }

    fun isUserInDB(id: Long): Boolean = dbMap.containsKey(id)

}