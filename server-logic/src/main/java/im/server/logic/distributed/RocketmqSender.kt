package im.server.logic.distributed

import im.server.logic.configs.RocketmqClient

class RocketmqSender(
    private val exchanhe: String,
    private val server_id: String,
    private val rocketmqSender: RocketmqClient
) : MQSender {

    override fun send(obj: Any) {
        rocketmqSender.send(exchanhe, server_id, obj)
    }

    override fun send(server_id: String, obj: Any) {
        rocketmqSender.send(exchanhe, server_id, obj)
    }
}