package im.server.logic.service.pipelines

import common.beans.*
import common.exceptions.ImException
import common.server.MsgOffsetRepository
import common.server.MsgRepository
import common.server.actions.Action
import common.server.actions.GateWayCMD
import im.server.logic.caches.MsgOffsetCache
import im.server.logic.caches.UserCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

interface CommandExecutor {
    fun execute(cmd: Msg, channel_id: String, server_id: String): Action
}

class GreetCMDExecutor(val userCache: UserCache) : CommandExecutor {
    //private val mapper = ObjectMapper().registerKotlinModule()
    override fun execute(cmd: Msg, channel_id: String, server_id: String): Action {
        //userCache.save()
        userCache.save(cmd.from_id!!, channel_id, server_id)
        cmd.state = AckState.ServerSend
        cmd.body = CmdResponse.ok()
        val action = Action(GateWayCMD.SaveChannel, cmd, listOf(channel_id))
        return action
    }
}

class RefreshOffsetExecutor(val msgOffsetCache: MsgOffsetCache) : CommandExecutor {

    override fun execute(cmd: Msg, channel_id: String, server_id: String): Action {
        val offset = cmd.body!! as MaxOffsetParam
        msgOffsetCache.save(offset.user_id, offset.dialog_id, offset.msg_id)
        cmd.body = CmdResponse.ok()
        val action = Action(GateWayCMD.Send, cmd, listOf(channel_id))
        return action
    }

}

class LoadUnreadExecutor(
        val userCache: UserCache,
        val msgOffsetCache: MsgOffsetCache,
        val msgRepository: MsgRepository
) : CommandExecutor {
    //private val mapper = ObjectMapper().registerKotlinModule()
    override fun execute(cmd: Msg, channel_id: String, server_id: String): Action {

        val pageable = cmd.body!! as PageParam
        val start = if (pageable.msg_id !== null && pageable.msg_id!! > 0) {
            pageable.msg_id!!
        } else {
            msgOffsetCache.load_single(cmd.from_id!!, cmd.dialog_id!!)?.msg_id ?: throw ImException.InvalidCmdParam
        }
        val page = PageRequest.of(pageable.page - 1, pageable.size)
        val msg = msgRepository.unread(cmd.dialog_id!!, start, page)
        val data = CmdResponse.ok(msg.content, msg.totalElements.toInt())
        cmd.body = data
        cmd.state = AckState.ServerSend
        val action = Action(GateWayCMD.Send, cmd, listOf(channel_id))
        return action
    }

}