package im.gateway.sdk.caches

import java.util.concurrent.atomic.AtomicLong

class ServerData {
    val receive_count = AtomicLong()
    val ack_fail = AtomicLong()
    val mq_receive = AtomicLong()
    val mq_fail = AtomicLong()
    val online_users = AtomicLong()
}