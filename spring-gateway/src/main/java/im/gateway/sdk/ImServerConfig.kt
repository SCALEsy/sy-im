package im.gateway.sdk

data class ImServerConfig(
    var center: Long = 0,
    var machine: Long = 0,
    var name: String = "",
    var port: Int = 8090,
    var max_read_time_second: Long = 35L,
    var user_receive_time_second: Long = 3L
)