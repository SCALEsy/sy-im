package common.netty

import common.beans.Msg
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory

class MsgOutConverter : ChannelOutboundHandlerAdapter() {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise?) {
        try {
            when (msg) {
                is Msg -> {
                    val proto = msg.toProtoMsg()
                    ctx.write(proto, promise)
                }

                else -> {
                    ctx.write(msg, promise)
                }
            }
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
        }
    }
}