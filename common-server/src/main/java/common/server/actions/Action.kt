package common.server.actions

import common.beans.Msg
import java.io.Serializable

enum class GateWayCMD() : Serializable {
    Ack, Send, SaveChannel
}

data class Action(
    val cmd: GateWayCMD,
    val msg: Msg,
    val channl_ids: List<String>
) : Serializable {
}

enum class RpcCode() : Serializable {
    OK, Fail, Error
}

data class RpcResponse(
    val code: RpcCode = RpcCode.OK,
    val msg: Msg? = null
) : Serializable {

    companion object {
        fun ack(ack: Msg?): RpcResponse {
            return RpcResponse(msg = ack)
        }


    }

}