package com.github.kotlintelegrambot.echo.util

import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.User as TelegramUser


class UserUtils {
    companion object {
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

        fun getUserPair(message: Message): Pair<Long?, Long?> {
            val userFrom = message.from?.id
            val userTo = message.replyToMessage?.from?.id

            return userFrom to userTo
        }

    }
}