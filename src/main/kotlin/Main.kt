package com.github.kotlintelegrambot.echo

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.pollAnswer
import com.github.kotlintelegrambot.echo.db.QuestionDB
import com.github.kotlintelegrambot.echo.db.UsersDB
import com.github.kotlintelegrambot.echo.util.*
import com.github.kotlintelegrambot.echo.util.PollHandler.Companion.handleAnswer
import com.github.kotlintelegrambot.echo.util.PollHandler.Companion.isPositive
import com.github.kotlintelegrambot.echo.util.PollHandler.Companion.user
import com.github.kotlintelegrambot.echo.util.TimeManager.Companion.userNeedWait
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.extensions.filters.Filter
import java.io.FileInputStream
import java.util.*

fun main() {
    val properties = Properties()
    properties.load(FileInputStream("bot.properties"))
    LanguageHandler.setLanguage("strong")

    val db = UsersDB.init()
    QuestionDB.init()

    println("Init completed")
    val bot = bot {
        token = properties.getProperty("token")

        dispatch {
            println("dispatch")
            command("start") {
                println("start")
                val name = message.from?.firstName ?: "Пользователь"
                val messageText = LanguageHandler.getText("start").format(name)
                bot.sendMessage(ChatId.fromId(message.chat.id), text = messageText)
            }
            command("help") {
                val messageText = LanguageHandler.getText("help")
                bot.sendMessage(ChatId.fromId(message.chat.id), text = messageText)
            }
            command("myrep") {
                val id = message.from?.id ?: 0
                val name = db.getUserName(id)
                val reputation = db.getReputation(id)
                val messageText = LanguageHandler.getText("my_rep").format(name, reputation)

                bot.sendMessage(ChatId.fromId(message.chat.id), text = messageText)
            }
            command("stat") {
                bot.sendMessage(ChatId.fromId(message.chat.id), text = db.getStat())
            }
            command("duel") {
                if (DuelHandler.isDuel) {
                    bot.sendDuelExistsMessage(message)
                } else {
                    val chat = message.chat.id
                    val userNameFrom = message.from?.firstName
                    val userNameTo = message.replyToMessage?.from?.firstName
                    val lastMessage = message.replyToMessage?.messageId
                    println("userNameTo: $userNameTo")
//                    when (userNameTo) {
//                        null -> {
//                            println("userNameTo: $userNameTo")
//                            println("userNameTo != null")
//                            val messageText = LanguageHandler.getText("duel_question").format(userNameTo, userNameFrom)
//                            DuelHandler.prepareDuel(message)//todo refactor to BotUtils
//
//                            bot.sendMessage(ChatId.fromId(chat), text = messageText, replyToMessageId = lastMessage)
//                        }
//                        userNameFrom -> {
//                            println("userNameFrom == userNameTo")
//                            val messageText = LanguageHandler.getText("duel_another_option")
//                            val cheaterMessageId = message.messageId
//
//                            bot.sendMessage(ChatId.fromId(chat), text = messageText, replyToMessageId = cheaterMessageId)
//                        }
//                        else -> {
//                            println("duel_wrong_option")
//                            val messageText = LanguageHandler.getText("duel_wrong_option")
//                            val cheaterMessageId = message.messageId
//
//                            bot.sendMessage(ChatId.fromId(chat), text = messageText, replyToMessageId = cheaterMessageId)
//                        }
//                    }
                    if (userNameTo == null) {
                        println("duel_wrong_option")
                        val messageText = LanguageHandler.getText("duel_wrong_option")
                        val cheaterMessageId = message.messageId

                        bot.sendMessage(ChatId.fromId(chat), text = messageText, replyToMessageId = cheaterMessageId)
                    } else if (userNameFrom == userNameTo) {//todo make option to duel with bot
                        println("userNameFrom == userNameTo")
                        val messageText = LanguageHandler.getText("duel_another_option")
                        val cheaterMessageId = message.messageId

                        bot.sendMessage(ChatId.fromId(chat), text = messageText, replyToMessageId = cheaterMessageId)
                    } else {
                        val messageText = LanguageHandler.getText("duel_question").format(userNameTo, userNameFrom)
                        DuelHandler.prepareDuel(message)//todo refactor to BotUtils

                        bot.sendMessage(ChatId.fromId(chat), text = messageText, replyToMessageId = lastMessage)
                    }
                }
            }
            command("setpenalty") {
                if (UserUtils.isAdmin(message.from)) {
                    val text = message.text ?: ""
                    val time = text.split(" ")[1].toInt()
                    val messageText = LanguageHandler.getText("set_penalty").format(time)

                    TimeManager.timeToWait = time
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = messageText)
                } else {
                    bot.sendOnlyAdminMessage(message)
                }
            }
            command("setlanguage") {
                if (UserUtils.isAdmin(message.from)) {
                    val text = message.text ?: ""
                    val language = text.split(" ")[1]
                    val messageText = LanguageHandler.getText("set_language").format(language)

                    when (language) {
                        "strong", "light" -> {
                            LanguageHandler.setLanguage(language)
                            bot.sendMessage(ChatId.fromId(message.chat.id), text = messageText)
                        }
                        else -> {
                            bot.sendWrongFormatMessage(message)
                        }
                    }
                } else {
                    bot.sendOnlyAdminMessage(message)
                }
            }
            command("questionscount") {
                if (UserUtils.isAdmin(message.from)) {
                    val questionsCount = message.text?.split(" ")?.get(1)?.toInt() ?: 1
                    DuelHandler.questionsCount = questionsCount

                    bot.sendQuestionsCountChanged(message)
                } else {
                    bot.sendOnlyAdminMessage(message)
                }
            }
            command("mute") {
                if (UserUtils.isAdmin(message.from)) {
                    val chat = message.chat.id
                    val user = message.replyToMessage?.from?.firstName
                    val messageQuestion = LanguageHandler.getText("mute_question").format(user)
                    val messageAnswer1 = LanguageHandler.getText("mute_answer1")
                    val messageAnswer2 = LanguageHandler.getText("mute_answer2")

                    bot.sendPoll(
                        ChatId.fromId(chat), question = messageQuestion,
                        options = (listOf(messageAnswer1, messageAnswer2)), isAnonymous = false
                    )

                    PollHandler.handlePoll(message, PollHandler.Type.RESTRICTION)
                } else {
                    bot.sendOnlyAdminMessage(message)
                }
            }
            command("ban") {
                if (UserUtils.isAdmin(message.from)) {
                    val chat = message.chat.id
                    val user = message.replyToMessage?.from?.firstName
                    val messageQuestion = LanguageHandler.getText("ban_question").format(user)
                    val messageAnswer1 = LanguageHandler.getText("ban_answer1")
                    val messageAnswer2 = LanguageHandler.getText("ban_answer2")

                    bot.sendPoll(
                        ChatId.fromId(chat), question = messageQuestion,
                        options = (listOf(messageAnswer1, messageAnswer2)), isAnonymous = false
                    )

                    PollHandler.handlePoll(message, PollHandler.Type.BAN)
                } else {
                    bot.sendOnlyAdminMessage(message)
                }
            }
            command("unmute") {
                val messageText = LanguageHandler.getText("unmute")

                bot.sendMessage(
                    ChatId.fromId(message.chat.id),
                    text = messageText
                )
                val chat = message.chat.id
                val who = message.replyToMessage?.from?.id ?: 0

                bot.restrictChatMember(ChatId.fromId(chat), who, ChatPermissions(canSendMessages = true))
            }

            message(Filter.Reply) {
                val text = message.text
                val users = UserUtils.getUserPair(message)
                val user = message.replyToMessage?.from
                val reputation = db.getReputation(user?.id ?: 0)
                val cheatingMessage = LanguageHandler.getText("cheating")
                val botIncreasingMessage = LanguageHandler.getText("bot_increasing")
                val botDecreasingMessage = LanguageHandler.getText("bot_decreasing")

                if (!db.isUserInDB(user)) {
                    db.addUser(user)
                }

                if (Helper.isCheating(users, message)) {
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = cheatingMessage)
                } else if (Helper.isBot(users, message)) {
                    when (message.text) {
                        "+" -> bot.sendMessage(ChatId.fromId(message.chat.id), text = botIncreasingMessage)
                        "-" -> bot.sendMessage(ChatId.fromId(message.chat.id), text = botDecreasingMessage)
                    }
                } else {
                    if (userNeedWait(users, text)) {
                        bot.sendCalmDownMessage(message)
                    } else {
                        when (text) {
                            "+" -> {
                                db.increaseReputation(user)
                                TimeManager.addPenalty(users)
                                bot.sendPositiveMessage(message, user)
                                bot.promote(message, reputation)
                            }
                            "-" -> {
                                db.decreaseReputation(user)
                                TimeManager.addPenalty(users)
                                bot.sendNegativeMessage(message, user)
                                bot.promote(message, reputation)
                            }
                        }
                    }
                }
            }
            message(Filter.Text) {
                if (message.from?.id == DuelHandler.participants?.second) {
                    if (DuelHandler.isWaitingForConfirmation) { //todo in better way
                        val answer = message.text?.lowercase()
                        if (DuelHandler.acceptWordsList.contains(answer)) {
                            bot.startDuel(message)
                            bot.sendDuelRules(message)
                            bot.sendQuestion(message)
                            DuelHandler.startDuel()
                        } else if (DuelHandler.rejectionWordsList.contains(answer)) {
                            bot.rejectDuel(message)
                            DuelHandler.rejectDuel()
                        } else {
                            if (DuelHandler.isNeedToAskAgain()) {
                                bot.askAboutDuelOneMoreTime(message)
                            } else {
                                bot.rejectDuel(message)
                            }
                        }
                    }
                }

                if (message.text?.contains("php") == true) {
                    val phpMessage = LanguageHandler.getText("php")
                    bot.sendMessage(
                        ChatId.fromId(message.chat.id),
                        text = phpMessage
                    )
                } else if (message.text?.contains("python") == true) {
                    val pythonMessage = LanguageHandler.getText("python")
                    bot.sendMessage(
                        ChatId.fromId(message.chat.id),
                        text = pythonMessage
                    )
                }
            }
            message(Filter.All) {
                if (message.newChatMembers != null) {
                    val user = message.newChatMembers!![0].firstName
                    val helloMessage = LanguageHandler.getText("hello").format(user)

                    bot.sendMessage(ChatId.fromId(message.chat.id), text = helloMessage)
                }
            }

            pollAnswer {
                handleAnswer(pollAnswer.optionIds[0])
                if (isPositive()) {
                    when (PollHandler.type) {
                        PollHandler.Type.RESTRICTION -> bot.muteUser(PollHandler.user)
                        PollHandler.Type.BAN -> bot.banUser(PollHandler.user)
                        PollHandler.Type.DUEL -> {
                            if (DuelHandler.isDuel) { //todo in better way
                                println("isDuel")
                                val isRoundEnded = DuelHandler.acceptAnswer(message)// todo quiz doesn't end
                                println("isRoundEnded: $isRoundEnded")
                                if (isRoundEnded) {
                                    if (DuelHandler.questionsCount >= 0) {
                                        println("questionsCount ${DuelHandler.questionsCount}")
                                        bot.sendQuestion(message)
                                    } else {
                                        val winnerId = DuelHandler.resolveWinner()
                                        DuelHandler.stopDuel()
                                        bot.sendWinnerMessage(message, winnerId)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    println("Prepare for polling")
    bot.startPolling()
}

