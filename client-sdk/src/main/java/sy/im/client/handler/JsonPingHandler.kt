package sy.im.client.handler

import common.beans.ImState
import common.beans.Msg
import common.beans.MsgType
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import sy.im.client.ClientConfig
import sy.im.client.msg.MsgSender
import java.util.concurrent.TimeUnit

class JsonPingHandler(
    val config: ClientConfig,
    val sender: MsgSender

) : ChannelInboundHandlerAdapter() {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private var id = 0L

    //private val heartBeatfuture = Future

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is Msg) {
            if (msg.msg_type == MsgType.Pong) {
                this.id = msg.id + 1
                logger.info("read pong {}", msg.id)
                /*ctx.executor().schedule(
                    Runnable {
                        sender.send_ping(id)
                    },
                    config.max_write_time_second,
                    TimeUnit.SECONDS
                )*/

            } else {
                ctx.fireChannelRead(msg)
            }
        } else {
            ctx.fireChannelRead(msg)
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        config.retries = 0
        ctx.executor().scheduleAtFixedRate({
            sender.send_ping()
        }, 5, 10, TimeUnit.SECONDS)

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
        config.state = ImState.Lost
        /*ctx.channel().eventLoop().schedule({
            config.connect()
        }, config.config.reconnect_sleep_second, TimeUnit.SECONDS)*/
        ctx.fireChannelInactive()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.channel().close()
        logger.error("ping error:{}", ExceptionUtils.getStackFrames(cause))
        ctx.fireExceptionCaught(cause)
    }

    override fun channelWritabilityChanged(ctx: ChannelHandlerContext) {
        super.channelWritabilityChanged(ctx)
        logger.info("channel write ability change {}", ctx.channel().isWritable)
        //sender.wakeup()
    }

}