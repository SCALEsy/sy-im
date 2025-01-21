package common.server

interface LoginService {
    fun check(token: String): Boolean

    fun login(user_id: Int, channel_id: String, server_id: String): Boolean

    fun login_out(user_id: Int): Boolean

    fun refresh(user_id: Int)
}