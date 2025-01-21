package im.server.logic.service.pipelines

import common.beans.AckState
import common.beans.CmdResponse
import common.beans.Msg
import common.exceptions.ImException
import common.server.MsgOffsetRepository
import common.server.MsgRepository
import common.server.actions.Action
import common.server.actions.GateWayCMD
import im.server.logic.caches.MsgOffsetCache
import im.server.logic.caches.UserCache
import im.server.logic.distributed.MQSender
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Service
class CmdMsgHandler {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val workers = mutableMapOf<String, CommandExecutor>()

    //private val mapper = ObjectMapper().registerKotlinModule()

    @Autowired
    private lateinit var cache: UserCache

    @Autowired
    private lateinit var msgOffsetCache: MsgOffsetCache

    @Autowired
    private lateinit var msgRepository: MsgRepository

    @Autowired
    @Qualifier("gateway")
    private lateinit var gateway: MQSender

    @PostConstruct
    fun init() {
        this.register("greet", GreetCMDExecutor(cache))
        this.register("unread", LoadUnreadExecutor(cache, msgOffsetCache, msgRepository))
        this.register("max-offset", RefreshOffsetExecutor(msgOffsetCache))
    }

    fun register(key: String, worker: CommandExecutor) {
        this.workers.put(key, worker)
    }

    fun handCmdMsg(msg: Msg, channel_id: String, server_id: String) {
        try {
            val exe = workers[msg.cmd!!] ?: throw ImException.InvalidCmdParam
            val action = exe.execute(msg, channel_id, server_id)
            gateway.send(server_id, action)
        } catch (e: ImException) {
            logger.error("cmd exe error from {} code {} ->{}", msg.id, e.code, e.msg)
            val body = CmdResponse.error(e)
            msg.state = AckState.Error
            msg.body = body
            val action = Action(GateWayCMD.Send, msg, listOf(channel_id))
            gateway.send(server_id, action)
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
            val body = CmdResponse.error(ImException.CmdExeError)
            msg.state = AckState.Error
            msg.body = body
            val action = Action(GateWayCMD.Send, msg, listOf(channel_id))
            gateway.send(server_id, action)
        }
    }
}