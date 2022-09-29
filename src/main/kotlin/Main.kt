package com.github.kotlintelegrambot.echo

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.echo.db.Database
import com.github.kotlintelegrambot.echo.entity.User
import com.github.kotlintelegrambot.echo.util.*
import com.github.kotlintelegrambot.echo.util.TimePenaltyManager.Companion.userNeedWait
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.extensions.filters.Filter
import java.io.FileInputStream
import java.util.*

fun main() {
    val properties = Properties()
    properties.load(FileInputStream("bot.properties"))
    val db = Database.init()

    val bot = bot {
        token = properties.getProperty("token")

        dispatch {
            command("start") {
                bot.sendMessage(
                    ChatId.fromId(message.chat.id),
                    text = "Привет, ${message.from?.firstName ?: "Пользователь"}!\nЭто бот, " +
                            "при помощи которого можно поднять/опустить показатель репутации" +
                            ", для того чтоб это сделать, ответь на сообщение " +
                            "при помощи + или -\n" +
                            "используй /myrep чтоб посмотреть свой показатель"
                )
            }
            command("help") {
                bot.sendMessage(
                    ChatId.fromId(message.chat.id),
                    text = "/stat - выводит таблицу\n/myrep - покажет твою репутацию"
                )
            }
            command("myrep") {
                message.from?.id?.let {
                    bot.sendMessage(
                        ChatId.fromId(message.chat.id),
                        text = "${db.getUserName(it)}, твоя репутация: ${db.getReputation(it)}"
                    )
                }
            }
            command("stat") {
                bot.sendMessage(
                    ChatId.fromId(message.chat.id),
                    text = db.getStat()
                )
            }
            command("setpenalty") {
                bot.sendMessage(
                    ChatId.fromId(message.chat.id),
                    text = db.getStat()
                )
            }

            message(Filter.Reply) {
                val text = message.text
                val users = UserUtils.getUserPair(message)
                val user = message.replyToMessage?.from

                if (!db.isUserInDB(user)) {
                    db.addUser(user)
                }

                if (Helper.isCheating(users, message)) {
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = "It's not fair! You can't do that!")
                } else {
                    if (userNeedWait(users, text)) {
                        bot.sendCalmDownMessage(message)
                    } else {
                        when (text) {
                            "+" -> {
                                db.increaseReputation(user)
                                TimePenaltyManager.addPenalty(users)
                                bot.sendPositiveMessage(message, user)
                            }
                            "-" -> {
                                db.decreaseReputation(user)
                                TimePenaltyManager.addPenalty(users)
                                bot.sendNegativeMessage(message, user)
                            }
                        }
                    }
                }
            }

            message(Filter.Text) {
                if (message.text?.contains("php") == true) {
                    bot.sendMessage(
                        ChatId.fromId(message.chat.id),
                        text = "PHP top!"
                    )
                }
            }

            message(Filter.All) {
                if (message.newChatMembers != null) {
                    bot.sendMessage(
                        ChatId.fromId(message.chat.id),
                        text = "Привет, ${message.newChatMembers!![0].firstName}!\nВ нашем чате работает бот, " +
                                "при помощи которого можно поднять/опустить показатель репутации" +
                                ", для того чтоб это сделать, ответь на сообщение " +
                                "при помощи + или -"
                    )
                }
            }
        }
    }

    bot.startPolling()
}

