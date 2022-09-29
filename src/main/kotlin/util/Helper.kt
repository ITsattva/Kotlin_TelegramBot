package com.github.kotlintelegrambot.echo.util

import com.github.kotlintelegrambot.entities.Message

class Helper {
    companion object {
        fun isCheating(users: Pair<Long?, Long?>, message: Message) =
            users.first == users.second && (message.text.equals("+") || message.text.equals("-"))

        fun isBot(users: Pair<Long?, Long?>, message: Message) =
            users.second == 5680894336 && (message.text.equals("+") || message.text.equals("-"))
    }
}