package im.server.logic.caches

import common.beans.MsgOffset


interface MsgOffsetCache {

    fun load_single(user_id: Int, dialog_id: String): MsgOffset

    fun save(user_id: Int, dialog_id: String, msg_id: Long)
}