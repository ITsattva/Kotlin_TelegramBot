package db

import com.github.kotlintelegrambot.echo.db.Database
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class DataBaseTest {

    companion object{
        var db: Database = Database

        @BeforeClass @JvmStatic
        fun init(){
            println("init()")
            db.directory = "resources"
            db.file = "testDB"
        }

        @AfterClass @JvmStatic
        fun clean(){
            println("clean()")

            Files.delete(Path.of("${db.directory}/${db.file}"))
            Files.deleteIfExists(Path.of("${db.directory}/${db.file}"))
        }
    }


    @Test
    fun createDataBaseTest(){
        db.directory = "resources"
        db.file = "testDB"
        db.init()
        var fileDB: File
        with(db){
            fileDB = File("$directory/$file")
        }

        Assert.assertTrue(fileDB.exists())
    }
}