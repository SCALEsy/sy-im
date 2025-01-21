package im.server.logic.caches.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import common.beans.AckState
import common.beans.Msg
import im.server.logic.caches.MsgSaver
import im.server.logic.distributed.MQSender
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisSaver : MsgSaver {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var template: RedisTemplate<String, String>


    @Autowired
    @Qualifier(value = "msg-save")
    private lateinit var sender: MQSender

    private val cache_second: Long = 10
    private val sendMQ: Boolean = true

    private val mapper = ObjectMapper().registerKotlinModule()

    private val prefix = "msg:"

    private val prefix_client_id = "client_id:"

    override fun save(msg: Msg, state: AckState?) {
        val key = prefix + msg.id
        state?.also {
            msg.state = it
        }
        val value = mapper.writeValueAsString(msg)
        template.opsForValue().set(key, value, cache_second, TimeUnit.SECONDS)
        sendMsgToMQ(msg)

        //存client id
        val client_id_key = prefix_client_id + msg.from_id!!
        val client_id = msg.client_id!!
        template.opsForValue().set(client_id_key, client_id.toString())
    }

    override fun get(id: Long): Msg? {
        val key = prefix + id
        val value = template.opsForValue().get(key)
        val msg = value?.let {
            mapper.readValue(it, Msg::class.java)
        }
        return msg
    }

    override fun ack_sended(from_user: Int, client_id: Long): Boolean {
        val key = prefix_client_id + from_user
        val value = template.opsForValue().get(key) ?: return false
        val id = value.toLong()
        return client_id <= id
    }

    override fun set_state(id: Long, state: AckState) {
        val key = prefix + id
        val value = template.opsForValue().get(key)
        value?.also {
            val msg = mapper.readValue(it, Msg::class.java)
            msg.state = state
            val v = mapper.writeValueAsString(msg)
            template.opsForValue().set(key, v, cache_second, TimeUnit.SECONDS)
            sendMsgToMQ(msg)
            logger.debug("redis save msg: {},state {}", id, state)
        }

    }

    override fun load_by_dest_user(dest_id: Int): List<Msg> {
        TODO("Not yet implemented")
    }

    /*override fun save_offline(msg: Msg) {
        rabbitTemplate.convertAndSend(exchange, server_name, msg)
    }*/

    override fun state(): String {
        return "ok"
    }


    fun sendMsgToMQ(msg: Msg) {
        if (sendMQ) {
            sender.send(msg)
        }
    }
}