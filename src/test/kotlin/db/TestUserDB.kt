package db

import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager

class TestUserDB {

    companion object {
        private const val FILE = "users.db"
        private const val USER_TABLE = "users"
        private const val DIRECTORY = "database"

        var connection: Connection = DriverManager.getConnection("jdbc:sqlite:${DIRECTORY}/${FILE}")
    }

    @Test
    fun showAll() {
        println("showAll()")
        val statement = connection.prepareStatement("SELECT * FROM ${USER_TABLE}")
        println(statement.toString())
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            println("id: ${resultSet.getInt(1)} name: ${resultSet.getString(2)} rep: ${resultSet.getInt(3)}")
        }
        statement.close()
    }

    @Test
    fun clearAll() {
        println("clearAll()")
        val statement = connection.prepareStatement("DELETE FROM ${USER_TABLE}")
        statement.executeUpdate()
//
//        while (resultSet.next()) {
//            println("id: ${resultSet.getInt(1)} name: ${resultSet.getString(2)} rep: ${resultSet.getInt(3)}")
//        }
        statement.close()
    }

}