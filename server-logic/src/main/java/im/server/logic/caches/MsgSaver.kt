package im.server.logic.caches

import common.beans.AckState
import common.beans.Msg

interface MsgSaver {
    fun save(chat: Msg, state: AckState? = null)
    fun get(id: Long): Msg?
    fun ack_sended(from_user: Int, client_id: Long): Boolean
    fun set_state(id: Long, state: AckState)
    fun load_by_dest_user(dest_id: Int): List<Msg>

    //fun save_offline(msg: Msg)
    fun state(): String
}

/*object DefaultMsgSaver : MsgSaver {
    val buff = ConcurrentHashMap<Long, Msg>()
    val indexs = ConcurrentHashMap<Long, Long>()
    override fun save(chat: Msg, state: AckState?) {
        if (state != null) {
            chat.state = state
        }
        chat.time = System.currentTimeMillis()
        buff[chat.id] = chat
        indexs[chat.client_id!!] = chat.id
    }

    override fun ack_sended(from_user: Int, client_id: Long): Boolean {
        return indexs[client_id] != null
    }


    override fun get(id: Long): Msg? {
        return buff[id]
    }

    override fun set_state(id: Long, state: AckState) {
        buff[id]?.state = state
    }


    override fun load_by_dest_user(dest_id: Int): List<Msg> {
        return buff.values.filter { x -> x.dest_id == dest_id }
    }


    override fun state(): String {
        return "size:" + this.buff.size
    }
}*/
