package im.server.logic.caches

data class UserLoginInfo(
    val server_id: String, val channel_id: String,
    val online: Boolean = false,
    val current: Boolean = false,
)

interface UserCache {
    fun save(user_id: Int, channel_id: String, server_id: String)
    fun find(user_id: Int, except_id: String? = null): List<String>
    //fun send_except(user_id: List<Int>, msg: Any)

    fun info(user_id: Int, server_id: String): UserLoginInfo


    fun remove(user_id: Int)

    fun refresh_time(user_id: Int)
    /*fun online(user_id: Int): Boolean

    fun current_user(user_id: Int): Boolean*/
}