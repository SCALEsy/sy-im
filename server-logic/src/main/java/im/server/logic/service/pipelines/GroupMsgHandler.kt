package im.server.logic.service.pipelines

import common.beans.AckState
import common.beans.Msg
import common.server.actions.Action
import common.server.actions.GateWayCMD
import im.server.logic.caches.DialogCache
import im.server.logic.caches.MsgOffsetCache
import im.server.logic.caches.MsgSaver
import im.server.logic.caches.UserCache
import im.server.logic.distributed.MQSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class GroupMsgHandler {

    @Autowired
    private lateinit var userCache: UserCache

    @Autowired
    private lateinit var dialogCache: DialogCache

    @Autowired
    private lateinit var msgOffsetCache: MsgOffsetCache

    @Autowired
    @Qualifier(value = "gateway")
    private lateinit var mqSender: MQSender

    @Autowired
    private lateinit var msgSaver: MsgSaver

    fun checkAndSend(msg: Msg, server_id: String) {

        msgOffsetCache.save(msg.from_id!!, msg.dialog_id!!, msg.id)

        val users = dialogCache.find(msg.dialog_id!!)
        users.forEach { user ->
            val info = userCache.info(user, server_id)
            if (info.online) {
                msg.dest_id = user
                msg.state = AckState.ServerSend
                val act = Action(GateWayCMD.Send, msg, listOf(info.channel_id))
                mqSender.send(info.server_id, act)
                //
            } else {
                //msgSaver.save(msg)
            }
        }


    }
}