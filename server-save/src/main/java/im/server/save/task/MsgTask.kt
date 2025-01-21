package im.server.save.task

import common.beans.Msg
import common.beans.MsgOffset

fun interface MsgTask {
    fun save(list: List<Msg>)

}

fun interface OffsetTask {
    fun save_offset(list: List<MsgOffset>)
}