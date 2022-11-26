package com.github.kotlintelegrambot.echo.db

import com.github.kotlintelegrambot.echo.entity.Question
import com.github.kotlintelegrambot.echo.util.Mapper
import java.sql.Connection
import java.sql.DriverManager
import kotlin.random.Random

object QuestionDB {
    private lateinit var connection: Connection
    private lateinit var questions: List<Question>
    private const val DB_PATH = "database/questions.db"
    private val questionTopics = listOf("java", "sql", "spring_eng")
    var previousQuestions: MutableList<Int> = mutableListOf()
    var answerNumber = 0
    var questionIndex = 0
    var currentQuestionTopic = 0

    fun init() {
        println("QuestionDB init")
        connection = DriverManager.getConnection("jdbc:sqlite:$DB_PATH")
        questions = receiveQuestions()
    }

    fun getDB(): QuestionDB {
        return this
    }

    fun getRandomQuestion(): Question {
        var index: Int
        do {
            index = Random.nextInt(questions.size)
        } while (previousQuestions.contains(index))

        questionIndex = index
        previousQuestions.add(index)
        val question = shakeAnswers(questions[index])
        answerNumber = getRightAnswerNumber(question)

        return question
    }

    private fun getRightAnswerNumber(question: Question): Int {
        with(question) {
            val rightAnswer = questions[questionIndex].answer
            val options = listOf(answer, option1, option2, option3)

            for((index, option) in options.withIndex()){
                if(option == rightAnswer){
                    answerNumber = (index+1)
                }
            }

            return answerNumber
        }
    }

    fun getRightAnswer() = answerNumber

    private fun receiveQuestions(): List<Question> {
        currentQuestionTopic = Random.nextInt(2)
        val questionTopic = questionTopics[currentQuestionTopic]
        val statement = connection.prepareStatement("SELECT * FROM $questionTopic")
        val resultSet = statement.executeQuery()
        val questionsList = Mapper.getQuestionsList(resultSet)
        statement.close()

        return questionsList
    }

    private fun shakeAnswers(question: Question): Question{
        with(question) {
            var shaker = listOf(answer, option3, option2, option1)
            shaker = shaker.shuffled()
            return Question(id, description, shaker[0], shaker[1], shaker[2], shaker[3])
        }
    }

    fun resetPreviousQuestions(){
        previousQuestions.clear()
    }

}