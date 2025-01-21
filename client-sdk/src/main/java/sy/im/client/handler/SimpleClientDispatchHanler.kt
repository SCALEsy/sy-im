package sy.im.client.handler

import common.beans.AckState
import common.beans.Msg
import common.beans.MsgType
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import sy.im.client.msg.MsgReceiver
import sy.im.client.msg.MsgSender

class SimpleClientDispatchHanler(
    private val sender: MsgSender,
    private val receiver: MsgReceiver
) :
    SimpleChannelInboundHandler<Msg>() {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun channelActive(ctx: ChannelHandlerContext) {
        /*ctx.executor().scheduleAtFixedRate({
            sender.send_ping()
        }, 5, 30, TimeUnit.SECONDS)*/
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Msg) {
        //logger.info("msg id {} {}", msg.id, msg.msg_type)
        if (msg.msg_type == MsgType.Pong) {
            sender.hanlder_pong(msg.id)
            return
        }
        if (msg.msg_type != MsgType.ChatAck) {
            sendClientAck(ctx, msg)
        }
        receiver.add(msg)
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt is IdleStateEvent) {
            if (evt.state() == IdleState.WRITER_IDLE) {
                // heart beat
                sender.send_ping_now()
            }
        } else {
            super.userEventTriggered(ctx, evt)
        }

    }

    private fun sendClientAck(ctx: ChannelHandlerContext, msg: Msg) {
        val ack = Msg.ack(msg.id, AckState.ClientReceive, msg.from_id, msg.dest_id, msg.dialog_id)
        ctx.writeAndFlush(ack)
        //logger.info("ack send client receive:{}", msg.id)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        //close connect
        logger.info("inactive close")
        ctx.channel().close()

    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        super.exceptionCaught(ctx, cause)
        logger.error(ExceptionUtils.getStackTrace(cause))
    }

    override fun channelWritabilityChanged(ctx: ChannelHandlerContext) {
        super.channelWritabilityChanged(ctx)
        logger.info("channel write ability change {}", ctx.channel().isWritable)
        sender.wakeup()
    }
}