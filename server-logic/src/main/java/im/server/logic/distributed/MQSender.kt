package im.server.logic.distributed

interface MQSender {

    /*fun send(server_id: String, msg: Msg)
    fun send(server_id: String, action: Action)*/

    fun send(obj: Any)

    fun send(server_id: String, obj: Any)
}

/*
class DefaultMQSender(val queue: Queue<Msg>) : MQSender {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    override fun send(server_id: String, msg: Msg) {
        logger.info("send msg to mq:${msg.id}")
    }
}*/
