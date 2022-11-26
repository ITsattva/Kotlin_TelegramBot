package com.github.kotlintelegrambot.echo.util

import com.github.kotlintelegrambot.echo.db.QuestionDB
import com.github.kotlintelegrambot.entities.Message

class DuelHandler {
    companion object {
        var questionsCount = 2
        var questionsCountPrevious = 0
        var querryCount = 0
        var participants: Pair<Long, Long>? = null
        var isWaitingForConfirmation = false
        var isDuel = false
        var acceptWordsList = listOf("ок", "да", "согласен", "принимаю", "погнали", "yes", "da") //todo language helper support
        var rejectionWordsList = listOf("не", "нет", "отказываюсь", "не принимаю", "отъебись", "no", "ne", "net") //todo language helper support
        private var firstFighterAnswer: Pair<String?, Long> = null to 0
        private var firstFighterAttempts = 0
        private var firstRightAnswers = 0
        private var firstIsWinner = false
        private var secondFighterAnswer: Pair<String?, Long> = null to 0
        private var secondFighterAttempts = 0
        private var secondRightAnswers = 0
        private var secondIsWinner = false


        fun prepareDuel(message: Message){
            isWaitingForConfirmation = true
            participants = UserUtils.getUserPair(message)
        }

        fun prepareDuelWithBot(message: Message){
            isWaitingForConfirmation = true//todo make this in future
            participants = UserUtils.getUserPair(message)
        }

        fun isNeedToAskAgain(): Boolean{
            if(querryCount < 1) {
                querryCount++
                return true
            } else {
                querryCount = 0
                return false
            }
        }

        fun startDuel(){
            isWaitingForConfirmation = false
            isDuel = true
            questionsCountPrevious = questionsCount
        }

        fun rejectDuel(){
            isWaitingForConfirmation = false
        }

        fun stopDuel(){
            isDuel = false
            questionsCount = questionsCountPrevious
            firstRightAnswers = 0
            secondRightAnswers = 0
            QuestionDB.resetPreviousQuestions()
        }

        fun acceptAnswer(message: Message): Boolean{
            println("acceptAnswer")
            val participant = message.from?.id
            val answer = message.text
            val answerTime = System.currentTimeMillis()
            when(participant) {
                participants?.first -> {
                    if(firstFighterAttempts < 1 && !firstIsWinner){
                        firstFighterAnswer = answer to answerTime
                        firstFighterAttempts++
                    }
                }
                participants?.second -> {
                    if(secondFighterAttempts < 1 && !secondIsWinner){
                        secondFighterAnswer = answer to answerTime
                        secondFighterAttempts++
                    }
                }
            }
            return calculateAnswers()
        }

        private fun calculateAnswers(): Boolean {
            println("calculateAnswers")
            if(firstFighterAnswer.first?.toInt() == QuestionDB.getRightAnswer()){
                println("firstFighterAnswer")
                firstIsWinner = true
            }
            if(secondFighterAnswer.first?.toInt() == QuestionDB.getRightAnswer()){
                println("secondFighterAnswer")
                secondIsWinner = true
            }
            println("calculateAnswers2")
            if(firstFighterAnswer.first != null && secondFighterAnswer.first != null) {
                println("both answered")
                calculateAnswersCount()
                questionsCount--
                return true
            } else {
                return false
            }
        }

        private fun calculateAnswersCount(){
            println("calculateAnswersCount")
            if(firstIsWinner && secondIsWinner){
                if (firstFighterAnswer.second > secondFighterAnswer.second) secondRightAnswers++ else firstRightAnswers++
            } else if(firstIsWinner) {
                firstRightAnswers++
            } else if(secondIsWinner) {
                secondRightAnswers++
            }
            resetIteration()
        }

        private fun resetIteration(){
            println("resetIteration")
            firstFighterAttempts = 0
            secondFighterAttempts = 0
            firstFighterAnswer = null to 0
            secondFighterAnswer = null to 0
            firstIsWinner = false
            secondIsWinner = false
        }

        fun resolveWinner(): Long {
            val winner = if(secondRightAnswers > firstRightAnswers){
                participants?.second?: 0
            } else {
                participants?.first?: 0
            }

            return winner
        }
    }
}