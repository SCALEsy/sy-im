package im.server.logic.utils

import common.beans.Msg
import common.beans.MsgType
import kotlin.math.max
import kotlin.math.min

object LogicUtils {


    fun gen_dialog_id(msg: Msg): String {
        if (msg.msg_type == MsgType.Chat) {
            val min = min(msg.from_id!!, msg.dest_id!!)
            val max = max(msg.from_id!!, msg.dest_id!!)
            return "$min-$max"
        }
        if (msg.msg_type == MsgType.GroupChat) {
            return msg.dialog_id!!
        }
        return "-"
    }
}