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

@Service
class AckHandler {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var saver: MsgSaver

    @Autowired
    private lateinit var msgOffsetCache: MsgOffsetCache

    @Autowired
    private lateinit var userCache: UserCache

    @Autowired
    @Qualifier("gateway")
    private lateinit var mqSender: MQSender

    fun handleAck(ack: Msg, server_id: String) {
        when (ack.state) {
            /*AckState.ServerReceive -> {
                logger.info("echo ack:{} type:server send", msg.id)
            }*/
            AckState.ClientReceive -> {//这个包有可能客户端发出了，服务端没收到
                saver.set_state(ack.id, AckState.ClientReceive)
                /*saver.get(ack.id)?.time?.also {
                    val now = System.currentTimeMillis()
                    logger.info("receive time:{}ms", now - it)
                }*/
                ack.dest_id?.also {
                    msgOffsetCache.save(it, ack.dialog_id!!, ack.id)
                }

            }
            AckState.ClientRead -> {
                saver.set_state(ack.id, AckState.ServerRead)
                ack.from_id?.let {
                    val channels = userCache.find(it)
                    val action = Action(GateWayCMD.Ack, ack, channels)
                    mqSender.send(server_id, action)
                }

            }
            else -> {

            }
        }


    }
}