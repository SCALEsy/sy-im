package sy.im.client.msg

import com.google.common.util.concurrent.RateLimiter
import common.beans.MaxOffsetParam
import common.beans.Msg
import common.beans.MsgType
import common.beans.PageParam
import common.exceptions.ImException
import common.snowflake.SnowFlakeSeq
import io.netty.channel.Channel
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import sy.im.client.ClientConfig
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

@Suppress("UnstableApiUsage")
class MsgSender(
        val config: ClientConfig,
        val msgSaver: MsgSaver,
        val userDataCache: UserDataCache,
        val schedulers: ScheduledThreadPoolExecutor,
        val idGenerator: SnowFlakeSeq = SnowFlakeSeq()
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private lateinit var channel: Channel
    private val user_id = config.user_id

    private val limiter = RateLimiter.create(250.0)

    private val lock = ReentrantLock()
    private val await = lock.newCondition()

    //private val queue = LinkedBlockingQueue<Msg>()

    fun setChannel(channel: Channel) {
        this.channel = channel
        wakeup()
    }

    fun wakeup() {
        try {
            lock.lock()
            await.signal()
            logger.info("signal")
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
        } finally {
            lock.unlock()
        }
    }

    fun start() {
        /*val thread = Thread(Runnable {
            while (true) {
                try {
                    val msg = queue.take()
                    send(msg, 3000)
                } catch (e: Exception) {
                    logger.error(ExceptionUtils.getStackTrace(e))
                    userDataCache.fail_sum.incrementAndGet()
                }
            }
        })
        thread.name = "sender-thread"
        thread.start()*/
    }

    fun send_msg(msg: Msg, time_out: Long = 3000) {
        msg.time = System.currentTimeMillis()
        //queue.offer(msg)
        send(msg, time_out)
    }

    /*fun send_aync(msg: Msg) {
        msg.time = System.currentTimeMillis()
        queue.offer(msg)
    }*/


    /*核心发送方法*/
    fun send(msg: Msg, time_out: Long) {
        val acq = limiter.acquire()
        var e = System.currentTimeMillis()
        if ((msg.time!! + time_out) < e) {
            throw ImException.SendTimeOut
        }
        lock.lock()
        try {

            if (!channel.isWritable) {
                //userDataCache.fail_sum.incrementAndGet()
                logger.info("await")
                await.awaitNanos(time_out)
            }
            e = System.currentTimeMillis()
            if ((msg.time!! + time_out) < e) {
                throw ImException.SendTimeOut
            }
            val future = channel.writeAndFlush(msg)
            future.addListener { f ->
                if (f.isSuccess) {
                    //logger.info("user acq:{}", acq)
                    userDataCache.sum.incrementAndGet()
                    if (msg.msg_type == MsgType.Ping) {
                        logger.info("send ping {}", msg.id)
                    }
                    //todo
                    msgSaver.save(msg)
                    schedulers.schedule({
                        if (msgSaver.containsKey(msg.id)) {
                            msgSaver.remove(msg.id)
                            throw RuntimeException("send msg timeout error id:${msg.id} body:${msg.body}")
                        }
                    }, 3, TimeUnit.SECONDS)
                } else {
                    logger.error("send msg fail :{}", ExceptionUtils.getStackTrace(future.cause()))
                }
            }
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
        } finally {
            lock.unlock()
        }

    }


    fun hanlder_pong(id: Long) {
        userDataCache.ping_id = id
        logger.info("pong :{}", id)
    }

    fun send_ping_now() {
        val id = userDataCache.ping_id
        val msg = Msg.ping(id, user_id)
        if (channel.isActive && channel.isWritable) {
            channel.writeAndFlush(msg)
        }
    }

    fun send_ping() {
        val id = userDataCache.ping_id
        val msg = Msg.ping(id, user_id)
        send_msg(msg)
    }


    fun send_chat(text: String, dest_id: Int) {
        val id = idGenerator.nextId()
        val dialog_id = userDataCache.dialog_chat(dest_id)
        if (dialog_id == null) {
            logger.error("no chat dialog id")
            return
        }
        val msg = Msg.chat(id, user_id, dest_id, dialog_id!!, text, System.currentTimeMillis())
        send_msg(msg)
    }

    fun send_error() {
        val id = 3333L
        val msg = Msg.chat(id, user_id, 2, "error", "error")
        send_msg(msg)
    }

    fun send_group(text: String, name: String) {
        val id = idGenerator.nextId()
        val dialog_id = userDataCache.dialog_group(name)
        if (dialog_id == null) {
            logger.error("no group dialog id")
            return
        }
        val msg = Msg.group(id, user_id, dialog_id, text)
        send_msg(msg)
    }


    fun refresh_offset(dialog_id: String, msg_id: Long) {
        val offset = MaxOffsetParam(user_id, dialog_id, msg_id)
        val id = idGenerator.nextId()
        val greet = Msg.cmd(id, config.user_id, "max-offset", body = offset)
        send_cmd(greet)
    }


    fun send_cmd(msg: Msg) {
        send_msg(msg)
        msgSaver.saveCmd(msg)
        schedulers.schedule({
            if (msgSaver.containsCmd(msg.client_id!!)) {
                msgSaver.removeCmd(msg.client_id!!)
                logger.error("cmd wait response error:{}", msg.cmd)
            }
        }, 10, TimeUnit.SECONDS)
    }

    fun send_greet() {
        val id = idGenerator.nextId()
        val greet = Msg.cmd(id, config.user_id, "greet", body = "token")
        send_cmd(greet)
    }

    /*fun send_load(dialog_id: String) {
        *//*val id = idGenerator.nextId()
        val pageParam = PageParam(1, 40)
        val msg = Msg.cmd(id, user_id, "unread", dialog_id = dialog_id, body = pageParam)
        send_cmd(msg)*//*
        val list = userDataCache.loadUnread(dialog_id)
        logger.info("{} last id {}", list.size, list.last().id)
        userDataCache.max_id = list.last().id
    }*/
}