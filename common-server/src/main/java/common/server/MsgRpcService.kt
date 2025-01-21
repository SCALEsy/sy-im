package common.server

import common.beans.Msg
import common.server.actions.RpcResponse

interface MsgRpcService {

    //fun genAck(msg: Msg, gateway: String, channel_id: String): RpcResponse

    fun handleMsg(msg: Msg, server_id: String, channel_id: String): RpcResponse
}