package im.server.save.task

import common.beans.Msg
import common.beans.MsgOffset
import org.apache.commons.lang3.concurrent.BasicThreadFactory

class SaveTaskDispatcher(private val size: Int) {

    private val factory = BasicThreadFactory.Builder().namingPattern("save-exe-%d").build()

    private lateinit var msgTask: MsgTask
    private lateinit var offsetTask: OffsetTask
    private lateinit var executors: Array<SaveExecutor>


    fun setMsgTask(msgTask: MsgTask) {
        this.msgTask = msgTask
    }

    fun setOffsetTask(offsetTask: OffsetTask) {
        this.offsetTask = offsetTask
    }


    fun start() {
        if (!this::msgTask.isInitialized || !this::offsetTask.isInitialized) {
            throw RuntimeException("task is not init")
        }
        executors = (0 until size).map {
            SaveExecutor(msgTask, offsetTask)
        }.toTypedArray()
        executors.forEach { e ->
            factory.newThread(e).start()
        }
    }

    fun dispatch(msg: Any) {
        val index = if (msg is Msg) {
            msg.id.mod(size)
        } else {
            val x = msg as MsgOffset
            val hash = "${x.user_id}-${x.dialog_id}".hashCode().mod(size)
            hash
        }
        val executor = executors[index]
        executor.put(msg)
    }
}