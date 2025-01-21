package im.gateway.sdk.utils

import common.exceptions.ImException
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class OutUtils {
}

fun <T> SimpleChannelInboundHandler<T>.checkAndSend(ctx: ChannelHandlerContext, msg: T) {
    if (ctx.channel().isActive && ctx.channel().isWritable) {
        ctx.writeAndFlush(msg)
    } else {
        ctx.fireExceptionCaught(ImException.SendMsgError)
        //logger.error("lost msg:{}, {}", msg.id, msg.msg_type)
    }
}