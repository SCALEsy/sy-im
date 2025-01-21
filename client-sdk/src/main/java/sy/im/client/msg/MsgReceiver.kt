package sy.im.client.msg

import common.beans.*
import common.exceptions.ExceptionData
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import sy.im.client.ClientConfig
import java.util.concurrent.PriorityBlockingQueue

class MsgReceiver(
    val config: ClientConfig,
    val saver: MsgSaver,
    val userDataCache: UserDataCache
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val queue = PriorityBlockingQueue<Msg>(8, Comparator { a, b -> (a.id - b.id).toInt() })

    @Volatile
    private var stop_tag: Boolean = false
    fun add(msg: Msg) {
        queue.add(msg)
    }

    fun start() {
        val thread = Thread(Runnable {
            while (!stop_tag) {
                try {
                    val msg = queue.take()
                    handler_msg(msg)
                } catch (e: Exception) {
                    logger.error(ExceptionUtils.getStackTrace(e))
                }
            }

        })
        thread.name = "msg-receive"
        thread.start()
    }

    fun stop() {
        stop_tag = true
    }

    private fun handler_msg(msg: Msg) {
        when (msg.msg_type) {
            MsgType.Chat -> {
                val e = System.currentTimeMillis()
                val x = msg.time?.let { e - it } ?: -1
                logger.info("chat msg  id:{} from :{}->body:{} {}ms", msg.id, msg.from_id, msg.body, x)
            }
            MsgType.ChatAck -> {
                handle_ack(msg)
            }
            MsgType.CMD -> {
                handle_cmd(msg)
            }
            MsgType.GroupChat -> {
                logger.info(
                    "group msg  id:{} from :{}->body:{}",
                    msg.id,
                    msg.dialog_id + "-" + msg.from_id,
                    msg.body
                )
            }
            else -> {

            }
        }
    }

    private fun handle_cmd(msg: Msg) {

        saver.removeCmd(msg.client_id!!)
        val res = msg.body as CmdResponse
        if (res.code != 200) {
            logger.error("cmd error {}->{}", res.code, res.msg)
            return
        }

        if (msg.cmd!! == "greet") {
            config.state = ImState.Logined
        }
        if (msg.cmd!! == "unread") {

            logger.info("load unread: {}", res.count)

        }
    }

    private fun handle_ack(msg: Msg) {
        when (msg.state) {
            AckState.ServerReceive -> {
                saver.remove(msg.client_id!!)
            }
            AckState.ClientRead, AckState.ServerRead -> {
                logger.info("user read from {} id:{}", msg.from_id, msg.id)
            }
            AckState.Error -> {
                msg.client_id?.also {
                    saver.remove(it)
                    saver.removeCmd(it)
                }

                val exception = msg.body!! as ExceptionData
                logger.error("msg {} error code {}->{}", msg.client_id, exception.code, exception.msg)
            }
            else -> {}
        }
    }


}