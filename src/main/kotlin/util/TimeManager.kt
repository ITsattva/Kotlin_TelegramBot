package com.github.kotlintelegrambot.echo.util

class TimeManager {

    companion object {
        var timeToWait: Int = 60
        var penaltyManager: HashMap<Long, HashMap<Long, Long>?> = HashMap()

        fun addPenalty(users: Pair<Long?, Long?>) {
            val userFrom = users.first
            val userTo = users.second
            val temp = Pair(userTo ?: 0, System.currentTimeMillis() / 1000)
            val userMap = penaltyManager[userFrom] ?: hashMapOf(temp)

            userMap[userTo ?: 0] = System.currentTimeMillis() / 1000
            penaltyManager[userFrom ?: 0] = userMap
        }

        fun getPenalty(users: Pair<Long?, Long?>): Long {
            val userFrom = users.first
            val userTo = users.second
            val userMap = penaltyManager[userFrom]
            val now = System.currentTimeMillis() / 1000
            val was = userMap?.get(userTo) ?: (now - 61)

            return now - was
        }

        fun userNeedWait(users: Pair<Long?, Long?>, text: String?): Boolean {
            if (text == "+" || text == "-"){
                val time = getPenalty(users)
                return time < timeToWait
            }

            return false
        }
    }
}