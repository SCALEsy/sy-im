package client.test

import common.beans.AckState
import common.beans.Msg
import common.beans.MsgType
import common.netty.checkAndSend
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory


class PigggHandler : SimpleChannelInboundHandler<Msg>() {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Msg) {
        //logger.info("id {} type:{} body:{}", msg.id, msg.msg_type, msg.body)
        if (msg.msg_type == MsgType.Ping) {
            //logger.info("writable {}", ctx.channel().isWritable)
            val pong = Msg.pong(msg.id + 1)
            this.checkAndSend(ctx, pong)
            return
        } else {
            ctx.fireChannelRead(msg)
        }
    }


    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        //super.exceptionCaught(ctx, cause)
        logger.error(ExceptionUtils.getStackTrace(cause))
    }
}

class TestServerHandler : SimpleChannelInboundHandler<Msg>() {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Msg) {
        logger.info("id {} {}", msg.id, msg.msg_type)
        if (msg.msg_type == MsgType.Chat) {
            val ack = Msg.ack(msg.id, AckState.ServerReceive, 1)
            ctx.writeAndFlush(ack)
            //return
        }
        Thread.sleep(100)
    }
}

class TestClientHandler : SimpleChannelInboundHandler<Msg>() {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Msg) {
        logger.info("id {} type:{} ", msg.id, msg.msg_type)
    }
}