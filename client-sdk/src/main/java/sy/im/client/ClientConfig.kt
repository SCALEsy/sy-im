package sy.im.client

import common.beans.ImState
import io.netty.channel.Channel


data class ClientConfig(
    var user_id: Int,
    var reconnect_max_count: Int = 3,
    var reconnect_sleep_second: Long = 3L,
    var ack_timeout: Long = 3L,
    var idle_write_time: Long = 30L,//要比服务端的小
    var web_uri: String = "http://192.168.31.90:8090",
    val save_path: String = "./save",
    var direct_ip: String? = null,
    var direct_port: Int? = null,
    internal var channel: Channel? = null,
    internal var state: ImState = ImState.Lost,
    internal var retries: Int = 0
)