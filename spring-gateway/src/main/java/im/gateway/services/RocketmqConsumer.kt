package im.gateway.services

import common.beans.Msg
import common.server.actions.Action
import common.server.actions.GateWayCMD
import im.gateway.sdk.ImServer
import im.gateway.sdk.caches.ChannelCache
import io.netty.channel.Channel
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener
import org.apache.rocketmq.spring.core.RocketMQListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@RocketMQMessageListener(
    consumerGroup = "\${im.server.name}",
    selectorExpression = "\${im.server.name}",
    topic = "\${im.server.msg.exchange}"
)
@ConditionalOnProperty(name = ["im.server.mq.type"], havingValue = "rocketmq")
open class RocketmqConsumer : RocketMQListener<Action> {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var channelCache: ChannelCache


    @Autowired
    private lateinit var server: ImServer


    override fun onMessage(action: Action) {

        try {
            when (action.cmd) {
                GateWayCMD.Ack -> {

                }
                GateWayCMD.Send -> {
                    action.channl_ids.forEach { channel_id ->
                        val channel = channelCache.find(channel_id) ?: return@forEach

                        send(channel, action.msg)

                    }
                }
                GateWayCMD.SaveChannel -> {
                    action.channl_ids.forEach { channel_id ->
                        val channel = channelCache.find(channel_id) ?: return@forEach
                        channelCache.save(channel, action.msg.from_id!!)
                        send(channel, action.msg)

                    }

                }
            }
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
        } finally {
            server.serverData().mq_receive.incrementAndGet()
        }
    }

    private fun send(channel: Channel, msg: Msg) {
        if (channel.isActive && channel.isWritable) {
            channel.writeAndFlush(msg)
        } else {
            logger.error("mq send fail")
            server.serverData().mq_fail.incrementAndGet()
        }
    }
}