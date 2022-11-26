package db

import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager

class TestQuestionDB {

    companion object {
        private const val FILE = "questions.db"
        private const val USER_TABLE = "sql"
        private const val DIRECTORY = "database"

        var connection: Connection = DriverManager.getConnection("jdbc:sqlite:${DIRECTORY}/${FILE}")
    }

    @Test
    fun showAll() {
        val statement = connection.prepareStatement("SELECT * FROM ${USER_TABLE}")
        val result= statement.executeQuery()

        while(result.next()) {
            val description = result.getString(2)
            val option1 = result.getString(3)
            val option2 = result.getString(4)
            val option3 = result.getString(5)
            val option4 = result.getString(6)
            println("Description: $description")
            println("answer: $option1")
            println("option1: $option2")
            println("option2: $option3")
            println("option3: $option4")
            println()
        }
        statement.close()

    }
}