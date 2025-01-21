package common.beans

import common.protobuf.MsgBuilder
import java.io.Serializable


data class Msg(

    var id: Long = 0,
    val from_id: Int,
    var dest_id: Int? = null,
    var dialog_id: String? = null,
    val client_id: Long? = null,
    val msg_type: MsgType = MsgType.Chat,
    var state: AckState? = null,
    var body: Any? = null,
    var time: Long? = null,
    var cmd: String? = null,
    var chat_type: ChatType? = ChatType.Text,
) : Serializable, Sharding {

    constructor() : this(from_id = 0)

    fun toProtoMsg(): MsgBuilder.ProtoMsg {
        val builder = MsgBuilder.ProtoMsg.newBuilder().setId(this.id)//buidler 新建cmd就是1
        this.from_id.also {
            builder.setFromId(it)
        }
        this.dest_id?.also {
            builder.setDestId(it)
        }
        this.dialog_id?.also {
            builder.setDialogId(it)
        }
        this.client_id?.also {
            builder.setClientId(it)
        }
        /*this.cmd?.also {
            builder.setCmd(CmdType.forNumber(it.num))
        }*/
        builder.setMsgType(MsgBuilder.MsgType.forNumber(this.msg_type.num))
        this.state?.also {
            builder.setState(MsgBuilder.AckState.forNumber(it.num))
        }
        this.body?.also {
            //存疑
            builder.setBody(it.toString())
        }
        this.cmd?.also {
            builder.setCmd(it)
        }
        this.time?.also {
            builder.setTime(it)
        }
        this.chat_type?.also {
            builder.setChatType(MsgBuilder.ChatType.forNumber(it.num))
        }


        return builder.build();
    }


    companion object {
        fun ping(id: Long, user_id: Int): Msg {
            return Msg(id, from_id = user_id, msg_type = MsgType.Ping)
        }

        fun pong(id: Long): Msg {
            return Msg(id, -1, msg_type = MsgType.Pong)
        }

        fun chat(seq: Long, from_id: Int, dest_id: Int, dialog_id: String, body: Any? = null, time: Long? = null): Msg {
            return Msg(
                id = seq,
                client_id = seq,
                from_id = from_id,
                dest_id = dest_id,
                dialog_id = dialog_id,
                msg_type = MsgType.Chat,
                body = body,
                state = AckState.ClientSend,
                time = time
            )
        }

        fun group(index: Long, from_id: Int, group_id: String, body: Any? = null): Msg {
            return Msg(
                id = index,
                from_id = from_id,
                dialog_id = group_id,
                msg_type = MsgType.GroupChat,
                body = body,
                client_id = index
            )
        }

        fun ack(
            id: Long,
            state: AckState,
            from_id: Int,
            dest_id: Int? = null,
            dialog_id: String? = null,
            client_id: Long? = null,
            body: Any? = null
        ): Msg {
            return Msg(
                id,
                state = state,
                msg_type = MsgType.ChatAck,
                from_id = from_id,
                dest_id = dest_id,
                dialog_id = dialog_id,
                client_id = client_id,
                body = body,
            )
        }


        fun cmd(
            index: Long,
            from_id: Int,
            cmd: String,
            dest_id: Int? = null,
            dialog_id: String? = null,
            body: Any? = null
        ): Msg {
            return Msg(
                id = index,
                client_id = index,
                from_id = from_id,
                cmd = cmd,
                dest_id = dest_id,
                dialog_id = dialog_id,
                body = body,
                msg_type = MsgType.CMD
            )
        }

        /*fun boardcast(body: String?): Msg {
            return Msg(msg_type = MsgType.Broadcast, body = body)
        }*/
    }

    override fun tableValue(): Int {
        return this.dialog_id?.hashCode() ?: this.from_id
    }

    override fun DBValue(): Int {
        return this.dialog_id?.hashCode() ?: this.from_id
    }
}