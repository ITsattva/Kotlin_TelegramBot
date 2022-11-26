package com.github.kotlintelegrambot.echo.util

import com.github.kotlintelegrambot.entities.Message

class PollHandler {
    enum class Type {RESTRICTION, BAN, DUEL}

    companion object {
        var type: Type = Type.RESTRICTION
        var voteCounter = -2
        var user: Message? = null

        fun handlePoll(message: Message, pollType: Type){
            voteCounter = -2
            user = message
            type = pollType
        }

        fun handleAnswer(answer: Int = 0) {
            voteCounter += answer
        }

        fun isPositive() = voteCounter >= 0

    }
}