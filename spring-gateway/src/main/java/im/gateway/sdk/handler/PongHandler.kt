package im.gateway.sdk.handler

import common.beans.Msg
import common.beans.MsgType
import im.gateway.sdk.utils.checkAndSend
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.slf4j.LoggerFactory


class PongHandler() : SimpleChannelInboundHandler<Msg>() {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Msg) {
        logger.info("id {} type:{} body:{}", msg.id, msg.msg_type, msg.body)
        if (msg.msg_type == MsgType.Ping) {
            val pong = Msg.pong(msg.id + 1)
            this.checkAndSend(ctx, pong)
            return
        } else {
            ctx.fireChannelRead(msg)
        }
    }

}