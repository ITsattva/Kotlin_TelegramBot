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
                    val name = rawUser.split("=")[0]
                    val id = rawUser.split("=")[1].toLong()
                    val reputation = rawUser.split("=")[2].toInt()
                    users.add(User(name, id, reputation))
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

        fun userToString(user: User) = "${user.name}=${user.id}=${user.reputation};"

        fun usersToSortedString(users: List<User>) = users.sortedByDescending { it.reputation }.map { "${it.name}: ${it.reputation}\n" }
    }
}