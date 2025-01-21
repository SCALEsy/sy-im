package sy.im.client.msg

import common.beans.Msg
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

interface MsgSaver {

    fun save(msg: Msg)

    fun remove(id: Long)

    fun containsKey(id: Long): Boolean

    fun saveCmd(msg: Msg)

    fun containsCmd(id: Long): Boolean

    fun removeCmd(id: Long)
}

class DefaultMsgSaver : MsgSaver {

    private val max = 100
    private val count = AtomicInteger(0)
    private val queue = PriorityBlockingQueue<Msg>(64, Comparator { a, b -> return@Comparator (a.id - b.id).toInt() })
    private val map = ConcurrentHashMap<Long, Msg>()

    private val cmds = ConcurrentHashMap<Long, Msg>()
    override fun save(msg: Msg) {
        map[msg.id] = msg
        queue.offer(msg)
        val c = count.addAndGet(1)
        if (c > max) {
            val last = queue.poll()
            map.remove(last.id)
            count.decrementAndGet()
        }
    }

    override fun remove(id: Long) {
        val msg = map.remove(id)
        queue.remove(msg)
        count.decrementAndGet()
    }


    override fun containsKey(id: Long): Boolean {
        return map.containsKey(id)
    }

    override fun saveCmd(msg: Msg) {
        cmds[msg.id] = msg
    }

    override fun containsCmd(id: Long): Boolean {
        return cmds.containsKey(id)
    }

    override fun removeCmd(id: Long) {
        cmds.remove(id)
    }


}