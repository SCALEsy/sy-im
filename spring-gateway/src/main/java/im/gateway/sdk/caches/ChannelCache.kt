package im.gateway.sdk.caches

import io.netty.channel.Channel


interface ChannelCache {
    fun save(channel: Channel, user_id: Int? = null)
    /* fun find(user_id: Int, except_id: ChannelId? = null): List<Channel>
     //fun send_except(user_id: List<Int>, msg: Any)

     fun info(user_id: Int): UserLoginInfo*/


    fun find(channel_id: String): Channel?


    /*fun online(user_id: Int): Boolean

    fun current_user(user_id: Int): Boolean*/
}

/*
object DefaultChannelCache : ChannelCache {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val groups = DefaultChannelGroup(GlobalEventExecutor.INSTANCE)
    private val user_id_attr = AttributeKey.valueOf<Int>("user_id")
    private val user_map = ConcurrentHashMap<Int, MutableList<ChannelId>>()
    override fun save(channel: Channel, user_id: Int) {
        groups.add(channel)

        channel.attr(user_id_attr).set(user_id)
        if (user_map.containsKey(user_id)) {
            val list = user_map[user_id]!!
            list.add(channel.id())
        } else {
            val list = mutableListOf<ChannelId>(channel.id())
            user_map.put(user_id, list)
        }


        channel.closeFuture().addListener {
            val id = channel.attr(user_id_attr).get()
            if (user_map.containsKey(id)) {
                val list = user_map.get(id)!!
                list.remove(channel.id())
                if (list.size == 0) {
                    user_map.remove(id)
                }
            }
        }
    }

    override fun find(user_id: Int, except_id: ChannelId?): List<Channel> {
        if (!user_map.containsKey(user_id)) {
            return emptyList()
        }
        val ids = user_map.get(user_id) ?: emptyList<ChannelId>()
        val list = if (except_id == null) {
            ids.map { id -> groups.find(id) }
        } else {
            ids.filter { x -> x != except_id }.map { id -> groups.find(id) }
        }
        return list
    }

    override fun find(channel_id: String): List<Channel> {
        TODO("Not yet implemented")
    }

    override fun info(user_id: Int): UserLoginInfo {
        TODO("Not yet implemented")
    }


    */
/*override fun send_except(user_id: List<Int>, msg: Any) {
        val f = groups.writeAndFlush(msg, ChannelMatcher { c ->
            val id = c.attr(user_id_attr).get()
            !user_id.contains(id)
        })
        f.addListener { it ->
            if (!it.isSuccess) {
                logger.error(ExceptionUtils.getStackTrace(it.cause()))
            }
        }
    }*//*


   */
/* override fun online(user_id: Int): Boolean {
        return user_map.containsKey(user_id)
    }

    override fun current_user(user_id: Int): Boolean {
        return user_map.containsKey(user_id)
    }*//*


    fun active_channel_size(): Int {
        return groups.size
    }
}*/
