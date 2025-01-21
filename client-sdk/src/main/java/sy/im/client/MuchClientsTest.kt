package sy.im.client

import com.fasterxml.jackson.core.type.TypeReference
import common.beans.WebResponse
import sy.im.client.utils.WebClient
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

object MuchClientsTest {
    val uri = "http://192.168.31.90:8090"

    @JvmStatic
    fun main(args: Array<String>) {

        /*(7 until 1000).forEach { user ->
            add_user("test-${user}")
            add_friend(user)
        }*/
        val uc = 1
        val countdown = CountDownLatch(uc)
        var index = AtomicInteger(0)
        val clients = (10 until uc + 10).map { id ->
            val client = ImIClient(id)
            id to client
        }
        val s = System.currentTimeMillis()
        clients.forEach { (id, client) ->
            val thread = Thread(Runnable {

                client.connect()
                //Thread.sleep(3000)
                var loops = 0
                while (!client.ok()) {
                    loops++
                    Thread.sleep(100)
                    if (loops > 300) {
                        client.stop()
                        return@Runnable
                    }
                }
                println("ok ${id}")


                (0 until 50).forEach { i ->
                    val s = System.currentTimeMillis()

                    (0 until 1000).forEach {
                        val out = index.incrementAndGet()
                        client.send_msg("i amd ${out}", 1)
                    }

                    val e = System.currentTimeMillis()
                    println("$id send once $i ${e - s}ms")
                    Thread.sleep(1000)
                }
                //client.stop()
                countdown.countDown()
                println("finish $id")
            })
            thread.start()
        }
        countdown.await()
        val e = System.currentTimeMillis()

        val scanner = Scanner(System.`in`)
        while (scanner.hasNext()) {
            scanner.nextLine()
            val sends = clients.map { (id, client) ->
                client.sum()
            }.sum()
            val fails = clients.map { (id, client) ->
                client.fail_sum()
            }.sum()
            println("all: ${index.get()} send ${sends} fail:${fails} cost:${e - s}ms")
        }
    }


    private fun add_user(user_id: String) {
        val res = WebClient.post<WebResponse<Any>>("$uri/user/add?name=$user_id",
            object : TypeReference<WebResponse<Any>>() {})
        if (res.code == 200) {
            println("add user ok")
        }
    }

    private fun add_friend(user_id: Int) {
        val res = WebClient.post<WebResponse<Any>>("$uri/friend/add?dest=1&from=$user_id",
            object : TypeReference<WebResponse<Any>>() {})
        if (res.code == 200) {
            println("add friend ok")
        }
    }
}