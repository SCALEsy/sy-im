package im.server.save.task

import common.beans.Msg
import common.beans.MsgOffset
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock


class SaveExecutor(
    val msgTask: MsgTask,
    val offsetTask: OffsetTask,
    val max_wait_time: Long = 5000L,
    private val max_batch_size: Int = 5000
) :
    Runnable {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val queue = ConcurrentLinkedQueue<Any>()

    private val count = AtomicLong()

    private val lock = ReentrantLock()
    private val wait = lock.newCondition()

    private var wait_time = 100L
    override fun run() {
        while (true) {
            val list = poll_much()
            if (list.isNotEmpty()) {
                //do work
                wait_time = 100
                dowork(list)
            } else {
                await0(wait_time)
                wait_time *= 2
            }
        }
    }

    fun put(msg: Any) {
        queue.offer(msg)
        val c = count.incrementAndGet()
        if (c.mod(max_batch_size) == 0) {
            //wake up
            wakeup()
        }
        if (wait_time > (max_wait_time / 2)) {
            wakeup()
        }
    }

    fun dowork(list: List<Any>) {
        try {
            val msgs = list.filter { x -> x is Msg }.map { x -> x as Msg }
            val offsets = list.filter { x -> x is MsgOffset }.map { x -> x as MsgOffset }
            val merge_msgs = msgs.groupBy { msg -> msg.id }.values.map { l ->
                l.reduce { a, b ->
                    val sa = a.state?.num ?: 0
                    val sb = b.state?.num ?: 0
                    return@reduce if (sa > sb) {
                        a
                    } else {
                        b
                    }
                }
            }
            val merge_offsets = offsets.groupBy { o -> "${o.user_id}-${o.dialog_id}" }.values.map { l ->
                l.reduce { a, b ->
                    if (a.msg_id >= b.msg_id) {
                        a
                    } else {
                        b
                    }
                }
            }

            msgTask.save(merge_msgs)
            offsetTask.save_offset(merge_offsets)
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
        }
    }

    private fun poll_much(): List<Any> {
        val list = mutableListOf<Any>()
        var count = 0
        do {
            val msg = queue.poll()
            if (msg != null) {
                list.add(msg)
                count++
            }
        } while (msg != null && count <= max_batch_size)
        return list
    }

    private fun await0(time: Long) {
        lock.lock()
        try {
            wait.await(time, TimeUnit.NANOSECONDS)
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
        } finally {
            lock.unlock()
        }
    }

    private fun wakeup() {
        lock.lock()
        try {
            wait.signal()
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
        } finally {
            lock.unlock()
        }
    }
}