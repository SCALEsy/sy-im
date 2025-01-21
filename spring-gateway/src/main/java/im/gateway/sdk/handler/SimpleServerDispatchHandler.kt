package im.gateway.sdk.handler

import common.beans.AckState
import common.beans.Msg
import common.beans.MsgType
import common.exceptions.ImException
import common.server.LoginService
import common.server.MsgRpcService
import common.server.actions.RpcCode
import im.gateway.sdk.caches.ChannelCache
import im.gateway.sdk.caches.ServerData

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleStateEvent
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory


class SimpleServerDispatchHandler(
    private val channelCache: ChannelCache,
    val loginService: LoginService,
    private val msgRpcService: MsgRpcService,
    val name: String,
    val serverData: ServerData
) : SimpleChannelInboundHandler<Msg>() {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun channelActive(ctx: ChannelHandlerContext) {
        super.channelActive(ctx)
        channelCache.save(ctx.channel())
        serverData.online_users.incrementAndGet()
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        ctx.close()
        serverData.online_users.decrementAndGet()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        //super.exceptionCaught(ctx, cause)
        logger.error(ExceptionUtils.getStackTrace(cause))
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt is IdleStateEvent) {
            ctx.fireChannelInactive()
        } else {
            ctx.fireUserEventTriggered(evt)
        }
    }

    fun send_msg(ctx: ChannelHandlerContext, msg: Msg) {
        if (ctx.channel().isActive && ctx.channel().isWritable) {
            ctx.writeAndFlush(msg)
        } else {
            logger.error("lost msg:{}, {}", msg.id, msg.msg_type)
            serverData.ack_fail.incrementAndGet()
        }
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Msg) {
        try {
            logger.info("get id {}->{}", msg.id, msg.msg_type)
            if (msg.msg_type == MsgType.Ping) {
                val pong = Msg.pong(msg.id + 1)
                send_msg(ctx, pong)
                loginService.refresh(msg.from_id)
                //logger.info("ping id {}", msg.id)
                return
            }

            serverData.receive_count.incrementAndGet()
            //Thread.sleep(10)
            msg.time = msg.time ?: System.currentTimeMillis()
            val s = System.currentTimeMillis()
            val res = msgRpcService.handleMsg(msg, name, ctx.channel().id().asLongText())
            val e = System.currentTimeMillis()
            //logger.info("id {} rpc cost {}:ms", msg.id, e - s)
            if (res.code == RpcCode.OK) {
                res.msg?.also {
                    send_msg(ctx, it)
                }
            }
        } catch (e: ImException) {
            logger.error("ack client_id {} error code:{}->{}", msg.client_id, e.code, e.msg)
            val body = e.toData()
            val ack = Msg.ack(msg.id, AckState.Error, msg.from_id, msg.dest_id, msg.dialog_id, msg.client_id, body)
            send_msg(ctx, ack)
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
            val body = ImException.UnknownError.toData()
            val ack = Msg.ack(msg.id, AckState.Error, msg.from_id, msg.dest_id, msg.dialog_id, msg.client_id, body)
            send_msg(ctx, ack)
        }
    }


}