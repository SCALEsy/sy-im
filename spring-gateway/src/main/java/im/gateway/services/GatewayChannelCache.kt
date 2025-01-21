package im.gateway.services

import common.server.LoginService
import im.gateway.sdk.caches.ChannelCache
import io.netty.channel.Channel
import io.netty.channel.ChannelFutureListener
import io.netty.util.AttributeKey
import org.apache.dubbo.config.annotation.DubboReference
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class GatewayChannelCache() : ChannelCache {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    //@MotanReferer(basicReferer = "logic-rpc")
    @DubboReference
    private lateinit var loginService: LoginService

    @Value("\${im.server.name}")
    private lateinit var server_id: String

    private val user_id_attr = AttributeKey.valueOf<Int>("user_id")
    //private val prefix = "channel:"

    private val remover = ChannelFutureListener { future ->

        val channel = future.channel() ?: return@ChannelFutureListener
        val id = channel.id().asLongText()
        channels.remove(id)

        if (channel.hasAttr(user_id_attr)) {
            val user_id = channel.attr(user_id_attr).get()
            loginService.login_out(user_id)
        }
    }

    private val channels = ConcurrentHashMap<String, Channel>()


    override fun save(channel: Channel, user_id: Int?) {

        user_id?.also {
            channel.attr(user_id_attr).set(it)
        }
        channels[channel.id().asLongText()] = channel
        channel.closeFuture().addListener(remover)
        //loginService.login(user_id, channel.id().asLongText(), server_id)
    }

    /*override fun find(user_id: Int, except_id: ChannelId?): List<Channel> {
        val key = prefix + user_id
        val value = template.opsForValue().get(key) ?: return emptyList()
        val channel_id = value.split(":", limit = 2).get(1)
        val channel = channels.get(channel_id)
        return listOf(channel).filterNotNull()
    }*/

    override fun find(channel_id: String): Channel? {
        val channel = channels.get(channel_id)
        return channel
    }

    /*override fun info(user_id: Int): UserLoginInfo {
        val key = prefix + user_id
        val value = template.opsForValue().get(key) ?: return UserLoginInfo("", "")
        val text = value.split(":", limit = 2)
        return UserLoginInfo(text[0], text[1], true, text[0] == server_id)
    }*/

}