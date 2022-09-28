package com.github.kotlintelegrambot.echo

import com.github.kotlintelegrambot.*
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.echo.db.Database
import com.github.kotlintelegrambot.echo.entity.User
import com.github.kotlintelegrambot.echo.util.Mapper
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.extensions.filters.Filter
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalTime
import java.util.Properties

fun main() {
    val properties = Properties()
    properties.load(FileInputStream("bot.properties"))

    val db = Database.init()
    var timePenalty: HashMap<Long?, Long> = HashMap()

    val bot = bot {
        token = properties.getProperty("token")

        dispatch {
            message(Filter.Reply) {

                val userFrom = message.from?.id
                val userId = message.replyToMessage?.from?.id

                if(userFrom == userId) {
                    bot.sendMessage(
                        ChatId.fromId(message.chat.id),
                        text = "It's not fair! You can't do that!"
                    )
                } else {
                    val userName = message.replyToMessage?.from?.firstName
                    var reputation: Int? = 0

                    userId?.let {
                        if (!db.isUserInDB(it)) {
                            db.addUser(User(it))
                        }
                    }

                    val now = System.currentTimeMillis()/1000
                    val was = timePenalty[userFrom]?:121
                    val time = now - was


                    if (message.text.equals("+")) {
                        if(time < 60) {
                            bot.sendMessage(
                                ChatId.fromId(message.chat.id),
                                text = "Calm down! You need to wait ${60 - time} sec!"
                            )
                        } else {
                            userId?.let {
                                db.increaseReputation(userId)
                                reputation = db.getReputation(userId)
                            }

                            timePenalty[userFrom] = System.currentTimeMillis()/1000
                            bot.sendMessage(
                                ChatId.fromId(message.chat.id),
                                text = "$userName reputation has been increased! :) \nreputation now is: $reputation"
                            )
                        }
                    } else if (message.text.equals("-")) {
                        if(time < 60) {
                            bot.sendMessage(
                                ChatId.fromId(message.chat.id),
                                text = "Calm down! You need to wait ${60 - time} sec!"
                            )
                        } else {
                            userId?.let {
                                db.decreaseReputation(userId)
                                reputation = db.getReputation(userId)
                            }

                            timePenalty[userFrom] = System.currentTimeMillis() / 1000
                            bot.sendMessage(
                                ChatId.fromId(message.chat.id),
                                text = "$userName reputation has been decreased! :/ \nreputation now is: $reputation"
                            )
                        }
                    }
                }
            }
        }
    }


    bot.startPolling()
}