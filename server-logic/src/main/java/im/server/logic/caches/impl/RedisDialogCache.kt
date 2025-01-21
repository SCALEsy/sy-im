package im.server.logic.caches.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import common.server.GroupUserRepository
import im.server.logic.caches.DialogCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisDialogCache : DialogCache {
    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Autowired
    private lateinit var groupUserRepository: GroupUserRepository


    private val prefix = "dialog:"
    private val mapper = ObjectMapper().registerKotlinModule()
    private val cache_minutes = 30L


    override fun find(dialog_id: String): List<Int> {
        val key = prefix + dialog_id
        val value = redisTemplate.opsForValue().get(key)
        if (value == null) {
            val list = groupUserRepository.load_group_users(dialog_id).map { g -> g.user_id }
            val text = mapper.writeValueAsString(list)
            redisTemplate.opsForValue().set(key, text, cache_minutes, TimeUnit.MINUTES)
            return list
        } else {
            val list = mapper.readValue<List<Int>>(value, object : TypeReference<List<Int>>() {})

            return list
        }

    }
}