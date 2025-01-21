package im.server.logic.caches.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import common.beans.MsgOffset
import common.server.MsgOffsetRepository
import im.server.logic.caches.MsgOffsetCache
import im.server.logic.distributed.MQSender
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class RedisMsgOffsetCacheImpl : MsgOffsetCache {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var template: RedisTemplate<String, String>

    @Autowired
    private lateinit var repository: MsgOffsetRepository

    @Autowired
    @Qualifier(value = "msg-reached")
    private lateinit var mqSender: MQSender

    /* @Value("\${im.server.name}")
     private lateinit var name: String*/

    private val cached_second = 300
    private val mapper = ObjectMapper().registerKotlinModule()

    private val prefix = "reached:"

    private val splitter = "-"

    override fun load_single(user_id: Int, dialog_id: String): MsgOffset {
        val key = prefix + user_id + splitter + dialog_id
        val value = template.opsForValue().get(key)
        val res = if (value == null) {
            val bean = repository.load_single(user_id, dialog_id)
            val text = mapper.writeValueAsString(bean)
            template.opsForValue().set(key, text, 300, TimeUnit.SECONDS)
            bean ?: MsgOffset(0, user_id, dialog_id, 0, null)
        } else {
            mapper.readValue(value, MsgOffset::class.java)
        }
        return res
    }

    override fun save(user_id: Int, dialog_id: String, msg_id: Long) {
        val bean = MsgOffset(null, user_id, dialog_id, msg_id, Date())
        mqSender.send(bean)

    }
}