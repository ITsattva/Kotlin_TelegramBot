package com.github.kotlintelegrambot.echo.entity

data class Question(val id: Int,
                    val description: String,
                    val answer: String = "",
                    val option1: String = "",
                    val option2: String = "",
                    val option3: String = "") {

    override fun toString(): String {
        return super.toString()
    }
}

