package im.server.logic.service.pipelines

import common.beans.Msg
import common.beans.MsgType
import im.server.logic.chains.MsgRunnable
import im.server.logic.chains.MsgWrapper
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class PipelineMsgRunnable : MsgRunnable {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var ackMsgHandler: AckHandler

    @Autowired
    private lateinit var chatMsgHandler: ChatMsgHandler

    @Autowired
    private lateinit var cmdMsgHandler: CmdMsgHandler

    @Autowired
    private lateinit var groupMsgHandler: GroupMsgHandler


    private fun handleMsgAsync(msg: Msg, server_id: String, channel_id: String) {
        try {
            when (msg.msg_type) {
                MsgType.ChatAck -> {
                    ackMsgHandler.handleAck(msg, server_id)
                }
                MsgType.Chat -> {
                    chatMsgHandler.checkAndSend(msg, server_id)
                }
                MsgType.CMD -> {
                    cmdMsgHandler.handCmdMsg(msg, channel_id, server_id)
                }
                MsgType.GroupChat -> {
                    groupMsgHandler.checkAndSend(msg, server_id)
                }
                else -> {
                    logger.error("msg error")
                }
            }
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
        } finally {
            //finish_size.incrementAndGet()
        }
    }

    override fun run(wrapper: MsgWrapper) {
        handleMsgAsync(wrapper.msg, wrapper.server_id, wrapper.channel_id)
    }
}