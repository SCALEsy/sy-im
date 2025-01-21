package sy.im.client.msg

import com.fasterxml.jackson.core.type.TypeReference
import common.beans.*
import common.exceptions.ImException
import org.mapdb.Serializer
import org.slf4j.LoggerFactory
import sy.im.client.utils.WebClient
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

class UserDataCache(val user_id: Int, val uri: String, val path: String) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val friends = MapdbSaver("friends", "${path}/${user_id}").build(Serializer.INTEGER, Serializer.JAVA)
    private val groups = MapdbSaver("groups", "${path}/${user_id}").build(Serializer.STRING, Serializer.JAVA)
    private val id_cache = MapdbSaver("id_cache", "${path}/${user_id}").build(Serializer.STRING, Serializer.LONG)
    private val user = MapdbSaver("user", "${path}/${user_id}").build(Serializer.STRING, Serializer.JAVA)

    private val gateways = mutableListOf<GateWayInfo>()
    private lateinit var token: String

    private var inited = false


    val sum = AtomicInteger(0)
    val fail_sum = AtomicInteger()
    internal var ping_id = 0L

    fun inited(): Boolean {
        return inited
    }

    fun init() {
        login()
        loadFriends()
        loadGroups()
        loadGateway()
        inited = true
    }


    fun refresh_Id(dialog_id: String, id: Long) {
        if (id_cache.containsKey(dialog_id)) {
            val v = id_cache[dialog_id]
            id_cache[dialog_id] = max(id, v!!)
        } else {
            id_cache[dialog_id] = id
        }
    }

    private fun login() {
        if (user.containsKey("token")) {
            token = user["token"].toString()
            return
        }
        val res = WebClient.get<WebResponse<String>>(
            "$uri/user/login?user_id=$user_id", object : TypeReference<WebResponse<String>>() {})
        if (res.code == 200) {
            user["token"] = res.data!!
            token = res.data!!
        } else {
            throw ImException.LoginError
        }
    }

    private fun loadFriends() {
        val res = WebClient.get<WebResponse<List<Friends>>>(
            "$uri/friend/mine?id=$user_id", object : TypeReference<WebResponse<List<Friends>>>() {}, token
        )
        if (res.code == 200) {
            res.data?.forEach { f ->
                friends[f.dest_id] = f

            }
        }
    }

    private fun loadGroups() {
        val res = WebClient.get<WebResponse<List<GroupInfo>>>(
            "$uri/group/mine?id=$user_id", object : TypeReference<WebResponse<List<GroupInfo>>>() {}, token
        )
        if (res.code == 200) {
            res.data?.forEach { f ->
                groups[f.dialog_id] = f
            }
        }

    }

    private fun loadGateway() {
        val res = WebClient.get<WebResponse<GateWayInfo>>(
            "$uri/gateway/balance?user_id=$user_id", object : TypeReference<WebResponse<GateWayInfo>>() {}, token
        )
        if (res.code == 200) {
            res.data?.also { f ->
                gateways.add(f)
            }
        }
    }

    fun loadUnread(dialog_id: String): List<Msg> {
        //todo check
        var page = 1
        val list = mutableListOf<Msg>()
        var count = 0L
        val size = 500
        val id = id_cache[dialog_id] ?: 0L
        do {
            val res = WebClient.get<WebResponse<List<Msg>>>(
                "$uri/msg/unread?dialog_id=${dialog_id}&page=$page&size=${size}&id=${id}",
                object : TypeReference<WebResponse<List<Msg>>>() {},
                token
            )
            if (res.code == 200) {
                res.data?.also { f ->
                    list.addAll(f)
                }
                count = res.count ?: 0
                page++
                val p = count / size
                if (page > p) {
                    break
                }
            } else {
                logger.error("load unread fail")
            }
        } while (true)
        return list
    }

    fun dialog_chat(dest_id: Int): String? {
        val ob = friends[dest_id]
        return ob?.let {
            val x = it as Friends
            x.dialog_id
        }
    }

    fun dialog_group(name: String): String? {
        return groups.filter { (k, v) ->
            (v as GroupInfo).name == name
        }.keys.firstOrNull()
    }

    fun gateway(): GateWayInfo? {
        return gateways.firstOrNull()
    }
}