package com.github.kotlintelegrambot.echo.util

import com.github.kotlintelegrambot.echo.entity.Question
import com.github.kotlintelegrambot.echo.entity.User
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.sql.ResultSet
import java.util.Scanner

class Mapper {
    companion object{
        fun getUsersList(set: ResultSet): List<User> {
            val users = mutableListOf<User>()

            while(set.next()) {
                val id = set.getLong(1)
                val name = set.getString(2)
                val reputation = set.getInt(3)
                users.add(User(id, name, reputation))
            }

            return users
        }

        fun usersToSortedString(users: List<User>) = users.sortedByDescending { it.reputation }.map { "${it.name}: ${it.reputation}\n" }
        fun getQuestionsList(resultSet: ResultSet): List<Question> {
            val questions = mutableListOf<Question>()

            while(resultSet.next()) {
                val id = resultSet.getInt(1)
                val description = resultSet.getString(2)
                val answer = resultSet.getString(3)
                val option1 = resultSet.getString(4)
                val option2 = resultSet.getString(5)
                val option3 = resultSet.getString(6)
                questions.add(Question(id, description, answer, option1, option2, option3))
            }

            return questions
        }

        fun questionToString(question: Question): String {
            with(question) {
                return "$description\n1.$answer\n2.$option1\n3.$option2\n4.$option3"
            }
        }
    }
}