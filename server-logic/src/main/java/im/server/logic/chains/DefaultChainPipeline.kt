package im.server.logic.chains

import common.beans.Msg
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory

class DefaultChainPipeline(private val runnable: MsgRunnable) : MsgChainPipeline {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val end = MsgFilterChainOut(runnable)
    private val head = MsgFilterChain(Head(), end)

    private var point = head

    @Synchronized
    override fun register(filter: Filter): MsgChainPipeline {
        val chain = MsgFilterChain(filter)
        chain.next = point.next
        point.next = chain
        point = point.next!!
        return this
    }


    override fun run(msg: Msg, server_id: String, channel_id: String) {
        try {
            val wrapper = MsgWrapper(msg, server_id, channel_id)
            head.doFilter(wrapper)
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
        }
    }


}