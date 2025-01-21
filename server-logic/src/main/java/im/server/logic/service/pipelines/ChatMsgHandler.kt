package im.server.logic.service.pipelines

import common.beans.AckState
import common.beans.Msg
import common.server.actions.Action
import common.server.actions.GateWayCMD
import im.server.logic.caches.MsgOffsetCache
import im.server.logic.caches.MsgSaver
import im.server.logic.caches.UserCache
import im.server.logic.distributed.MQSender
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledThreadPoolExecutor

@Service
class ChatMsgHandler {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var cache: UserCache

    @Autowired
    private lateinit var msg_saver: MsgSaver

    @Autowired
    private lateinit var msgOffsetCache: MsgOffsetCache

    @Autowired
    private lateinit var scheduler: ScheduledThreadPoolExecutor

    @Autowired
    @Qualifier(value = "gateway")
    private lateinit var mqSender: MQSender


    //@Value("")
    private val user_receive_time_second: Long = 3

    fun checkAndSend(msg: Msg, server_id: String) {
        //val dest_id = msg.dest_id ?: throw ImException.NoDestIdException
        msgOffsetCache.save(msg.from_id!!, msg.dialog_id!!, msg.id)
        val info = cache.info(msg.dest_id!!, server_id)
        if (info.online) {
            msg.state = AckState.ServerSend
            msg_saver.save(msg)
            val deploy = System.currentTimeMillis() + user_receive_time_second * 1000
            //executor.schedule(Runnable { check_msg_timeout(msg.id, System.currentTimeMillis()) }, Date(deploy))
            val mq = Action(GateWayCMD.Send, msg, listOf(info.channel_id))
            mqSender.send(info.server_id, mq)

        } else {
            //msg_saver.save(msg, AckState.ServerReceive)
            //保存离线消息
            logger.error("dest user is not online:{}", msg.dest_id)

        }

    }


    private fun check_msg_timeout(id: Long, time: Long) {
        val now = System.currentTimeMillis()
        val msg = msg_saver.get(id) ?: return
        if (msg.state == AckState.ClientReceive || msg.state == AckState.ClientRead) {
            return
        }
        val x = now - time
        logger.error("wait for user receive time out:{} {}ms", id, x)
    }


}