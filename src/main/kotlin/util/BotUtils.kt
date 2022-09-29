package com.github.kotlintelegrambot.echo.util

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.echo.db.Database
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.User as TelegramUser

fun Bot.sendPositiveMessage(message: Message, userRaw: TelegramUser?) {
    val userId = userRaw?.id?: 0
    val userName = UserUtils.getFullName(userRaw)
    val reputation = Database.getReputation(userId)

    sendMessage(
        ChatId.fromId(message.chat.id),
        text = "$userName reputation has been increased! :) \nreputation now is: $reputation"
    )
}

fun Bot.sendNegativeMessage(message: Message, userRaw: TelegramUser?) {
    val userId = userRaw?.id?: 0
    val userName = UserUtils.getFullName(userRaw)
    val reputation = Database.getReputation(userId)

    sendMessage(
        ChatId.fromId(message.chat.id),
        text = "$userName reputation has been decreased! :/ \nreputation now is: $reputation"
    )
}

fun Bot.sendCalmDownMessage(message: Message) {
    val userPair = UserUtils.getUserPair(message)
    val time = TimePenaltyManager.getPenalty(userPair.first, userPair.second)

    sendMessage(
        ChatId.fromId(message.chat.id),
        text = "Calm down! You need to wait ${TimePenaltyManager.TIME_TO_WAIT - time} sec!"
    )
}