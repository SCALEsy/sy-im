package common.beans

import common.protobuf.MsgBuilder.ProtoMsg

enum class Role {
    Admin, User
}

enum class ImState {
    Bind, Lost, Logining, Logined, LoginOut, Connected
}

enum class ChatType(val num: Int) {
    Text(1), Photo(2), Audio(3);

    companion object {
        fun find(num: Int): ChatType {
            return ChatType.values().filter { x -> x.num == num }.first()
        }
    }
}

enum class MsgType(val num: Int) {
    Ping(1), Pong(2), Chat(3), ChatAck(4), CMD(5), Broadcast(6), GroupChat(7), GroupAck(8);

    companion object {
        fun find(num: Int): MsgType {
            return MsgType.values().filter { x -> x.num == num }.first()
        }
    }
}

enum class AckState(val num: Int) {
    ClientSend(1), ServerReceive(2), ServerSend(3), ClientReceive(4), ClientRead(5), ServerRead(6), Error(7);

    companion object {
        fun find(num: Int): AckState? {
            return AckState.values().filter { x -> x.num == num }.firstOrNull()
        }
    }
}

fun ProtoMsg.transfer(): Msg {
    val msg_type = MsgType.find(this.msgType.number)
    val state = AckState.find(this.state.number)
    /*val cmd = if (msg_type.equals(MsgType.CMD)) {
        CMD.find(this.cmd.number)
    } else {
        null
    }*/
    val chatType = ChatType.find(this.chatType.number)
    val msg = Msg(
            this.id,
            this.fromId,
            this.destId,
            this.dialogId,
            this.clientId,
            msg_type,
            state,
            this.body,
            this.time,
            this.cmd,
            chatType
    )
    return msg
}