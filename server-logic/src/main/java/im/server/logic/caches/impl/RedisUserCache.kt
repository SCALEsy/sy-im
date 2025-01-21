package im.server.logic.caches.impl

import im.server.logic.caches.UserCache
import im.server.logic.caches.UserLoginInfo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Component
class RedisUserCache : UserCache {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var template: RedisTemplate<String, String>
    private val last_refresh = ConcurrentHashMap<String, Long>()

    private val prefix = "channel:"
    private val mark = ":"
    private val valid_second = 95L
    override fun save(user_id: Int, channel_id: String, server_id: String) {

        val key = prefix + user_id
        val value = server_id + mark + channel_id
        template.opsForValue().set(key, value, valid_second, TimeUnit.SECONDS)
    }

    override fun find(user_id: Int, except_id: String?): List<String> {
        val key = prefix + user_id
        val value = template.opsForValue().get(key) ?: return emptyList()
        val channel_id = value.split(mark, limit = 2)[1]
        return listOf(channel_id).filterNotNull()
    }


    override fun info(user_id: Int, server_id: String): UserLoginInfo {
        val key = prefix + user_id
        val value = template.opsForValue().get(key) ?: return UserLoginInfo("", "")
        val text = value.split(":", limit = 2)
        return UserLoginInfo(text[0], text[1], true, text[0] == server_id)
    }

    override fun remove(user_id: Int) {
        val key = prefix + user_id
        //val value = server_id + mark + channel_id
        template.delete(key)
    }

    override fun refresh_time(user_id: Int) {
        val key = prefix + user_id
        //val value = server_id + mark + channel_id
        template.expire(key, valid_second, TimeUnit.SECONDS)
    }

}