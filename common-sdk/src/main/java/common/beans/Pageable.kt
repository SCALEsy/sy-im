package common.beans

import common.exceptions.ImException
import java.io.Serializable

data class PageParam(
        var page: Int = 1,
        var size: Int = 40,
        var msg_id: Long? = null
) : Serializable


data class MaxOffsetParam(
        val user_id: Int,
        val dialog_id: String,
        val msg_id: Long,
) : Serializable

data class CmdResponse(
        val code: Int = 200,
        val msg: String = "ok",
        val count: Int? = null,
        val data: Any? = null
) {
    companion object {

        fun ok(data: Any? = null, count: Int? = null): CmdResponse {
            return CmdResponse(data = data, count = count)
        }

        fun error(e: ImException): CmdResponse {
            return CmdResponse(e.code, e.msg)
        }

    }
}