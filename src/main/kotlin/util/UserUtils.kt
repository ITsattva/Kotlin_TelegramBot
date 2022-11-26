package com.github.kotlintelegrambot.echo.util

import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.User as TelegramUser


class UserUtils {
    companion object {
        val titles = mapOf(-10 to "Сволочь",
            -5 to "Неприятный",
            0 to "Хто я?",
            5 to "Начинающий",
            10 to "Продвинутый",
            15 to "Свой Чувак",
            20 to "Про",
            25 to "GODLIKE")
        fun isAdmin(user: TelegramUser?): Boolean {
            val admins = listOf(769557804L, 159503570L)
            val userId = user?.id

            return admins.contains(userId)
        }
        fun getFullName(tgUser: TelegramUser?): String {
            val userFirstName = tgUser?.firstName?.replace("=", "")?: ""
            val userLastName = tgUser?.lastName?.replace("=", "")
            return if (userLastName == null) userFirstName else "$userFirstName $userLastName"
        }

        fun getUserPair(message: Message): Pair<Long, Long> {
            val userFrom = message.from?.id?: 0
            val userTo = message.replyToMessage?.from?.id?: 0

            return userFrom to userTo
        }

        fun setTitle(message: Message){

        }

    }
}