package sy.im.client

import org.apache.commons.lang3.exception.ExceptionUtils
import java.util.*


object Client1 {
    @JvmStatic
    fun main(args: Array<String>) {

        val cfg = ClientConfig(1)
        //val cfg = ClientConfig(1, direct_ip = "192.168.31.234", direct_port = 9090)
        val imClient = ImIClient(cfg)
        //client.connect()
        val thread = Thread(Runnable {
            imClient.connect()
        })
        thread.start()
        val scanner = Scanner(System.`in`)
        while (scanner.hasNextLine()) {
            try {
                val str = scanner.nextLine()
                if (str == null || str.isEmpty()) {
                    continue
                }
                if (str == "state") {
                    imClient.show_state()
                    continue
                }

                /* if (str == "load") {
                     imClient.load_unread()
                     continue
                 }*/
                if (str == "stop") {
                    imClient.stop()
                    break
                }
                if (str == "much") {
                    send_much(imClient, 2)
                    continue
                }
                if (str == "error") {
                    imClient.test_error()
                    continue
                }

                if (str.startsWith("load")) {
                    val arr = str.split("-", limit = 2)
                    val group_id = arr[1]
                    imClient.load_unread(group_id)
                    continue
                }
                if (str.startsWith("group")) {
                    val arr = str.split("-", limit = 3)
                    val group_id = arr[1]
                    val body = arr[2]
                    imClient.send_group(body, group_id)
                    continue
                }
                val arr = str.split("-", limit = 2)
                val id = arr[0].toInt()
                val body = arr[1]
                imClient.send_msg(body, id)
            } catch (e: Exception) {
                println(ExceptionUtils.getStackTrace(e))
            }
        }
    }
}


object Client2 {
    @JvmStatic
    fun main(args: Array<String>) {
        val cfg = ClientConfig(2)
        //val cfg = ClientConfig(2, direct_ip = "192.168.31.234", direct_port = 9090)
        val imClient = ImIClient(cfg)
        //client.connect()
        val thread = Thread(Runnable {
            imClient.connect()
        })
        thread.start()
        val scanner = Scanner(System.`in`)
        while (scanner.hasNextLine()) {
            try {
                val str = scanner.nextLine()
                if (str == null || str.isEmpty()) {
                    continue
                }
                if (str == "state") {
                    imClient.show_state()
                    continue
                }


                if (str == "stop") {
                    imClient.stop()
                    break
                }
                if (str == "much") {
                    send_much(imClient, 4)
                    continue
                }
                if (str.startsWith("load")) {
                    val arr = str.split("-", limit = 2)
                    val group_id = arr[1]
                    imClient.load_unread(group_id)
                    continue
                }
                if (str.startsWith("group")) {
                    val arr = str.split("-", limit = 3)
                    val group_id = arr[1]
                    val body = arr[2]
                    imClient.send_group(body, group_id)
                    continue
                }
                val arr = str.split("-", limit = 2)
                val id = arr[0].toInt()
                val body = arr[1]
                imClient.send_msg(body, id)
            } catch (e: Exception) {
                println(ExceptionUtils.getStackTrace(e))
            }
        }
    }
}

object Client3 {
    @JvmStatic
    fun main(args: Array<String>) {
        val cfg = ClientConfig(3)
        val imClient = ImIClient(cfg)
        //client.connect()
        val thread = Thread(Runnable {
            imClient.connect()
        })
        thread.start()
        val scanner = Scanner(System.`in`)
        while (scanner.hasNextLine()) {
            try {
                val str = scanner.nextLine()
                if (str == null || str.isEmpty()) {
                    continue
                }
                if (str == "state") {
                    imClient.show_state()
                    continue
                }

                if (str == "stop") {
                    imClient.stop()
                    break
                }
                if (str == "much") {
                    send_much(imClient, 4)
                    continue
                }
                if (str.startsWith("load")) {
                    val arr = str.split("-", limit = 2)
                    val group_id = arr[1]
                    imClient.load_unread(group_id)
                    continue
                }
                if (str.startsWith("group")) {
                    val arr = str.split("-", limit = 3)
                    val group_id = arr[1]
                    val body = arr[2]
                    imClient.send_group(body, group_id)
                    continue
                }
                val arr = str.split("-", limit = 2)
                val id = arr[0].toInt()
                val body = arr[1]
                imClient.send_msg(body, id)
            } catch (e: Exception) {
                println(ExceptionUtils.getStackTrace(e))
            }
        }
    }
}

object Client4 {
    @JvmStatic
    fun main(args: Array<String>) {
        val cfg = ClientConfig(4)
        val imClient = ImIClient(cfg)
        //client.connect()
        val thread = Thread(Runnable {
            imClient.connect()
        })
        thread.start()
        val scanner = Scanner(System.`in`)
        while (scanner.hasNextLine()) {
            try {
                val str = scanner.nextLine()
                if (str == null || str.isEmpty()) {
                    continue
                }
                if (str == "state") {
                    imClient.show_state()
                    continue
                }


                if (str == "stop") {
                    imClient.stop()
                    break
                }
                if (str == "much") {
                    send_much(imClient, 1)
                    continue
                }
                if (str.startsWith("load")) {
                    val arr = str.split("-", limit = 2)
                    val group_id = arr[1]
                    imClient.load_unread(group_id)
                    continue
                }
                if (str.startsWith("group")) {
                    val arr = str.split("-", limit = 3)
                    val group_id = arr[1]
                    val body = arr[2]
                    imClient.send_group(body, group_id)
                    continue
                }
                val arr = str.split("-", limit = 2)
                val id = arr[0].toInt()
                val body = arr[1]
                imClient.send_msg(body, id)
            } catch (e: Exception) {
                println(ExceptionUtils.getStackTrace(e))
            }
        }
    }
}

fun send_much(client: ImIClient, user_id: Int) {

    for (i in 1..10000) {
        val msg = "msg_$i"
        client.send_msg(msg, user_id)
        //Thread.sleep(10)
    }

}