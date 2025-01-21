package im.gateway.services

import common.server.actions.Action
import common.server.actions.GateWayCMD
import im.gateway.sdk.caches.ChannelCache
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
//@RabbitListener(queues = ["\${im.server.name}"])
open class RabbitMqConsumer {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var channelCache: ChannelCache

    //@RabbitHandler
    fun recive(action: Action) {
        logger.info(action.toString())
        //ImServer.send(msg)
        when (action.cmd) {
            GateWayCMD.Ack -> {

            }
            GateWayCMD.Send -> {
                action.channl_ids.forEach { channel_id ->
                    val channel = channelCache.find(channel_id) ?: return@forEach
                    channel.eventLoop().execute {
                        channel.writeAndFlush(action.msg)
                    }
                }
            }
            GateWayCMD.SaveChannel -> {
                action.channl_ids.forEach { channel_id ->
                    val channel = channelCache.find(channel_id) ?: return@forEach
                    channelCache.save(channel, action.msg!!.from_id!!)
                    channel.eventLoop().execute {
                        channel.writeAndFlush(action.msg)
                    }
                }

            }
        }
    }
}