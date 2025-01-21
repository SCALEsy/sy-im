package common.client.beans

data class WebResponse<T>(
    val code: Int = 200, val msg: String = "ok", val count: Int? = null,
    val data: T?
) {

    companion object {


        fun <T> ok(data: T? = null): WebResponse<T> {
            return WebResponse(data = data)
        }

        fun <T> ok(data: T? = null, count: Int): WebResponse<T> {
            return WebResponse(data = data, count = count)
        }

        fun <T> error(code: Int, msg: String, data: T? = null): WebResponse<T> {
            return WebResponse(code, msg, null, data)
        }
    }
}