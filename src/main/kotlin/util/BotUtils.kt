package com.github.kotlintelegrambot.echo.util

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.echo.db.Database
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Message
import java.util.*
import com.github.kotlintelegrambot.entities.User as TelegramUser

fun Bot.sendPositiveMessage(message: Message, userRaw: TelegramUser?) {
    val userId = userRaw?.id?: 0
    val userName = UserUtils.getFullName(userRaw)
    val reputation = Database.getReputation(userId)
    val messageText = LanguageHandler.getText("reputation_increase").format(userName, reputation)

    sendMessage(
        ChatId.fromId(message.chat.id),
        text = messageText
    )
}

fun Bot.sendNegativeMessage(message: Message, userRaw: TelegramUser?) {
    val userId = userRaw?.id?: 0
    val userName = UserUtils.getFullName(userRaw)
    val reputation = Database.getReputation(userId)
    val messageText = LanguageHandler.getText("reputation_decrease").format(userName, reputation)

    sendMessage(
        ChatId.fromId(message.chat.id),
        text = messageText
    )
}

fun Bot.sendCalmDownMessage(message: Message) {
    val userPair = UserUtils.getUserPair(message)
    val time = TimeManager.getPenalty(userPair)
    val timeLeft = TimeManager.timeToWait - time
    val messageText = LanguageHandler.getText("calm_down").format(timeLeft)

    sendMessage(
        ChatId.fromId(message.chat.id),
        text = messageText
    )
}

fun Bot.sendOnlyAdminMessage(message: Message) {
    val messageText = LanguageHandler.getText("only_admin")

    sendMessage(ChatId.fromId(message.chat.id), text = messageText)
}

fun Bot.sendWrongFormatMessage(message: Message) {
    val messageText = LanguageHandler.getText("wrong_format")

    sendMessage(ChatId.fromId(message.chat.id), text = messageText)
}

fun Bot.muteUser(message: Message?) {
    val userId = message?.replyToMessage?.from?.id?: 0
    val chatId = message?.chat?.id?: 0
    val penalty = Date().time / 1000 + 60
    val messageText = LanguageHandler.getText("mute")


    restrictChatMember(ChatId.fromId(chatId), userId, ChatPermissions(canSendMessages = false), penalty)
    sendMessage(ChatId.fromId(chatId), messageText)
}

fun Bot.banUser(message: Message?) {
    val userId = message?.replyToMessage?.from?.id?: 0
    val chatId = message?.chat?.id?: 0
    val penalty = Date().time / 1000 + 60
    val messageText = LanguageHandler.getText("ban")

    kickChatMember(ChatId.fromId(chatId), userId, penalty)
    sendMessage(ChatId.fromId(chatId), messageText)
}
