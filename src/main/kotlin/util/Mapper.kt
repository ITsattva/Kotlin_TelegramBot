package com.github.kotlintelegrambot.echo.util

import com.github.kotlintelegrambot.echo.entity.User
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Scanner

class Mapper {
    companion object{
        fun parseUsers(rawData: String): List<User> {
            val users = mutableListOf<User>()
            val rawUsers = getRawUsers(rawData)

            for(rawUser in rawUsers) {
                if(rawUser.contains("=")){
                    val id = rawUser.split("=")[0].toLong()
                    val reputation = rawUser.split("=")[1].toInt()
                    users.add(User(id, reputation))
                }
            }

            return users
        }

        private fun getRawUsers(rawData: String): List<String> {
            val scanner = Scanner(rawData)

            var rawUsers = ""
            while(scanner.hasNext()){
                rawUsers += scanner.nextLine()
            }

            return rawUsers.split(";")
        }

        fun getRawData(file: File) : String {
            val input = BufferedReader(InputStreamReader(file.inputStream()))
            var rawData = ""

            while(input.ready()) {
                rawData += input.readLine()
            }

            return rawData
        }

        fun userToString(user: User) = "${user.id}=${user.reputation};"

        fun usersToHashMap(users: List<User>) = users.associate { Pair(it.id, it.reputation) }
    }
}