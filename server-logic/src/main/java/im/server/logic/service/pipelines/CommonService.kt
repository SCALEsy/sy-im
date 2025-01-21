package im.server.logic.service.pipelines

import common.beans.AckState
import common.beans.Msg
import common.beans.MsgType
import common.exceptions.ImException
import im.server.logic.caches.MsgIdGenerator
import im.server.logic.caches.MsgSaver
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommonService {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var msg_saver: MsgSaver

    @Autowired
    private lateinit var idGenerator: MsgIdGenerator

    private val cmd_prefix = "u_"

    fun checkMsgAndGenAck1(msg: Msg): Msg? {
        if (msg.from_id == null || msg.from_id!! <= 0) {
            throw ImException.InvalidClient
        }

        if (msg.msg_type == MsgType.ChatAck) {
            return null
        }

        if (msg_saver.ack_sended(msg.from_id!!, msg.client_id!!)) {
            //send exception
            //logger.error("invalid client id {} from {}", msg.client_id, msg.from_id)
            //throw ImException.InvalidClientId
            //must todo
        }
        when (msg.msg_type) {
            MsgType.Chat -> {
                if (msg.dest_id == null) {
                    throw ImException.NoDestIdException
                }
                if (StringUtils.isEmpty(msg.dialog_id)) {
                    throw ImException.InvalidDialogId
                }
            }

            MsgType.CMD -> {
                if (StringUtils.isEmpty(msg.cmd)) {
                    throw ImException.InvalidCmdParam
                }
            }
            MsgType.GroupChat -> {
                if (StringUtils.isEmpty(msg.dialog_id)) {
                    throw ImException.InvalidDialogId
                }
            }
            else -> {

            }
        }
        //check friendship

        val key = msg.dialog_id ?: (cmd_prefix + msg.from_id!!)
        msg.id = idGenerator.generator(key)
        msg.time = msg.time ?: System.currentTimeMillis()
        msg_saver.save(msg, AckState.ServerReceive)
        //回执ack
        val ack = Msg.ack(msg.id, AckState.ServerReceive, msg.from_id, msg.dest_id, msg.dialog_id, msg.client_id)
        return ack
    }

}