package sy.im.client.handler

import common.beans.ImState
import common.beans.Msg
import common.protobuf.MsgBuilder
import common.protobuf.MsgBuilder.ProtoMsg
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import sy.im.client.interfaces.IClient
import java.util.concurrent.TimeUnit

class PingHandler(val imAbstractClient: IClient, val user: Int) : ChannelInboundHandlerAdapter() {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private var id = 0L


    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is ProtoMsg) {
            if (msg.msgType == MsgBuilder.MsgType.Pong) {
                this.id = msg.id + 1
                ctx.executor().schedule(
                    Runnable {
                        ping(ctx.channel())
                    },
                    imAbstractClient.config.idle_write_time,
                    TimeUnit.SECONDS
                )

            } else {
                ctx.fireChannelRead(msg)
            }
        } else {
            ctx.fireChannelRead(msg)
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        imAbstractClient.setRetry(0)
        ping(ctx.channel())
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt is IdleStateEvent) {
            if (evt.state() == IdleState.WRITER_IDLE) {
                //ping(ctx.channel())
            }
        } else {
            super.userEventTriggered(ctx, evt)
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        logger.info("lost connect")
        imAbstractClient.set_state(ImState.Lost)
        ctx.channel().eventLoop().schedule({
            imAbstractClient.connect()
        }, imAbstractClient.config.reconnect_sleep_second, TimeUnit.SECONDS)
        ctx.fireChannelInactive()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.channel().close()
        logger.error("ping error:{}", ExceptionUtils.getStackFrames(cause))
        ctx.fireExceptionCaught(cause)
    }

    fun ping(channel: Channel) {

        channel.eventLoop().execute {
            //logger.info("ping {}", id)
            val msg = Msg.ping(id, user)
            channel.writeAndFlush(msg)
        }
    }
}