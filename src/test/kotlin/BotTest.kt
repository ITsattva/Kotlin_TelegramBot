import com.github.kotlintelegrambot.echo.db.Database
import com.github.kotlintelegrambot.echo.util.UserUtils
import com.github.kotlintelegrambot.entities.User as TelegramUser
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.lang.reflect.Method
import com.github.kotlintelegrambot.echo.entity.User
import com.github.kotlintelegrambot.echo.util.TimeManager
import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.Message
import java.io.FileWriter

class BotTest {
    companion object {
        var db: Database = Database
        val fakeUser1 = TelegramUser(firstName = "test1", id = 1241, isBot = false)
        val fakeUser2 = TelegramUser(firstName = "test2", id = 2324, isBot = false)
        val fakeUser3 = TelegramUser(firstName = "test3", id = 3547, isBot = true)
        val fakeUser7 = TelegramUser(firstName = "test7", id = 5314, isBot = true)
        val fakeUser8 = TelegramUser(firstName = "test8", id = 6691, isBot = true)

        @BeforeClass
        @JvmStatic
        fun init() {
            println("init()")
            db.directory = "resources"
            db.file = "testDB"

            addUsersToDB()
        }

        @AfterClass
        @JvmStatic
        fun clean() {
            println("clean()")

            clearDB()
        }


        private fun addUsersToDB(){
            with(db){

                addUser(fakeUser1)
                addUser(fakeUser2)
                addUser(fakeUser3)
            }
        }

        private fun clearDB(){
            with(db){
                val outputStream = FileWriter("$directory/$file")
                outputStream.write("")
            }
        }

        fun showAll(){
            val getUsersMethod: Method = db.javaClass.getDeclaredMethod("getUsers")
            getUsersMethod.trySetAccessible()
            val users = getUsersMethod.invoke(db) as List<User>
            println(users)
        }

    }


    @Test
    fun addUserTest() {
        println("addUserTest()")
        val fakeUser4 = TelegramUser(firstName = "test4", id = 4437, isBot = false)
        val fakeUser5 = TelegramUser(firstName = "test5", id = 4128, isBot = false)
        val fakeUser6 = TelegramUser(firstName = "test6", id = 4539, isBot = false)
        db.addUser(fakeUser4)
        db.addUser(fakeUser5)
        db.addUser(fakeUser6)
        db.addUser(fakeUser7)
        db.addUser(fakeUser8)

        val getUsersMethod: Method = db.javaClass.getDeclaredMethod("getUsers")
        getUsersMethod.trySetAccessible()
        val users = getUsersMethod.invoke(db) as List<User>
        println(users)

        Assert.assertTrue(users.size == 8)
    }

    @Test
    fun increaseReputationTest() {
        println("increaseReputationTest()")
        showAll()
        db.increaseReputation(fakeUser2)
        showAll()
        db.increaseReputation(fakeUser2)
        showAll()


        Assert.assertTrue(db.getReputation(2324) == 2)
    }

    @Test
    fun decreaseReputationTest() {
        println("decreaseReputationTest()")
        db.decreaseReputation(fakeUser1)
        db.decreaseReputation(fakeUser1)

        showAll()

        Assert.assertTrue(db.getReputation(1241) == -2)
    }

    @Test
    fun showAllTest() { //todo
        println("showAllTest()")
        val stat = db.getStat()

        Assert.assertTrue(true)
    }

    @Test
    fun showMyReputationTest() { //todo
        println("showMyReputationTest()")
        val rep = db.getReputation(fakeUser3.id)
        println("reputation: $rep")

        showAll()
        Assert.assertTrue(rep == 0)
    }

    @Test
    fun passTimePenaltyTest() { //todo
        println("passTimePenaltyTest()")
        val replyMessage = Message(chat = Chat(1L, type = "TestChat"), date = 2020, messageId = 46, from = fakeUser8)
        val message = Message(chat = Chat(1L, type = "TestChat"), date = 2020, messageId = 47, from = fakeUser7, replyToMessage = replyMessage)

        val users = UserUtils.getUserPair(message)

        db.increaseReputation(fakeUser8)
        TimeManager.addPenalty(users)

        val penalty = TimeManager.getPenalty(users)
        Assert.assertTrue(penalty < 60)
    }


}

