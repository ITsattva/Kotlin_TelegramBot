package com.github.kotlintelegrambot.echo.util

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.echo.db.UsersDB
import com.github.kotlintelegrambot.echo.db.QuestionDB
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Message
import java.util.*
import com.github.kotlintelegrambot.entities.User as TelegramUser

fun Bot.sendPositiveMessage(message: Message, userRaw: TelegramUser?) {
    val userId = userRaw?.id?: 0
    val userName = UserUtils.getFullName(userRaw)
    val reputation = UsersDB.getReputation(userId)
    val messageText = LanguageHandler.getText("reputation_increase").format(userName, reputation)

    sendMessage(
        ChatId.fromId(message.chat.id),
        text = messageText
    )
}

fun Bot.sendNegativeMessage(message: Message, userRaw: TelegramUser?) {
    val userId = userRaw?.id?: 0
    val userName = UserUtils.getFullName(userRaw)
    val reputation = UsersDB.getReputation(userId)
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

fun Bot.promote(message: Message, reputation: Int){
    if (reputation % 5 == 0) {
        val title = UserUtils.titles[reputation] ?: "nobody"
        val chatId = ChatId.fromId(message.chat.id)
        val user = message.replyToMessage?.from?.id ?: 0
        val userName = message.replyToMessage?.from?.firstName
        val messageText = LanguageHandler.getText("promotion").format(userName, title)

        promoteChatMember(chatId, user, false, false, false, false, false, true, false, false, false)
        setChatAdministratorCustomTitle(chatId, user, title)

        sendMessage(chatId, messageText)
    }
}

fun Bot.startDuel(message: Message?) {
    val chatId = message?.chat?.id?: 0
    val messageText = LanguageHandler.getText("duel_start")

    sendMessage(ChatId.fromId(chatId), messageText)
}

fun Bot.rejectDuel(message: Message?) {
    val chatId = message?.chat?.id?: 0
    val messageText = LanguageHandler.getText("duel_reject")

    sendMessage(ChatId.fromId(chatId), messageText)
}

fun Bot.askAboutDuelOneMoreTime(message: Message?) {
    val chatId = message?.chat?.id?: 0
    val messageText = LanguageHandler.getText("duel_ask_again")

    sendMessage(ChatId.fromId(chatId), messageText)
}

fun Bot.sendQuestion(message: Message?) {
    val chatId = message?.chat?.id?: 0
    val db = QuestionDB.getDB()
    val question = db.getRandomQuestion()

    sendPoll(
        ChatId.fromId(chatId), question = question.description,
        options = (listOf(question.option1, question.option2, question.option3, question.answer)), isAnonymous = false
    )
}

fun Bot.sendQuestionsCountChanged(message: Message?) {
    val chatId = message?.chat?.id?: 0
    val message = LanguageHandler.getText("question_count_changed")

    sendMessage(ChatId.fromId(chatId), message)
}

fun Bot.sendDuelRules(message: Message?) {
    val chatId = message?.chat?.id?: 0
    val messageId = message?.messageId
    val messageText = LanguageHandler.getText("duel_rules")

    sendMessage(ChatId.fromId(chatId), messageText, replyToMessageId = messageId)
}

fun Bot.sendDuelExistsMessage(message: Message?) {
    val chatId = message?.chat?.id?: 0
    val messageText = LanguageHandler.getText("duel_currently_exists")

    sendMessage(ChatId.fromId(chatId), messageText)
}
fun Bot.sendWinnerMessage(message: Message?, winner: Long) {
    val chatId = message?.chat?.id?: 0
    val winnerName = getChatMember(ChatId.fromId(chatId), winner).first?.body()?.result?.user?.firstName
    val messageText = LanguageHandler.getText("duel_winner").format(winnerName)

    sendMessage(ChatId.fromId(chatId), messageText)
}