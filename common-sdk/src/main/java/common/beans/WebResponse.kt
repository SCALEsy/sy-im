package common.beans

import common.exceptions.ImException

data class WebResponse<T>(val code: Int = 200, val msg: String = "ok", val count: Long? = null, val data: T?) {

    companion object {


        fun ok(data: Any? = null): WebResponse<Any> {
            return WebResponse(data = data)
        }

        fun ok(data: Any? = null, count: Long): WebResponse<Any> {
            return WebResponse(data = data, count = count)
        }

        fun error(code: Int, msg: String, data: Any? = null): WebResponse<Any> {
            return WebResponse(code, msg, null, data)
        }

        fun error(e: ImException): WebResponse<Any> {
            return WebResponse(e.code, e.msg, data = null)
        }
    }
}