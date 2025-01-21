package common.handler

import org.slf4j.LoggerFactory
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent

class HeartbeatTrigger : ChannelInboundHandlerAdapter() {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt is IdleStateEvent) {
            if (evt.state() == IdleState.READER_IDLE) {
                logger.info("idle atfer max,disconnect")
                ctx.channel().disconnect()
            }
        } else {
            ctx.fireUserEventTriggered(evt)
        }
    }
}