package common.server

/*
@Entity
@Table(name = "user")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null, val name: String
)

@Entity
@Table(name = "friends")
data class Friends(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null,
    val from_id: Int,
    val dest_id: Int,
    val dialog_id: String,
    val status: Int
)

@Entity
@Table(name = "groups")
data class Group(
    @Id val id: Int,
    val dialog_id: String,
    val user_id: Int,
    val time: Date?
) :
    Comparable<Group> {
    override fun compareTo(other: Group): Int {
        return this.id - other.id
    }

}

@Table(name = "msg_reached_info")
@Entity
data class MsgReachedInfo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int?,
    val user_id: Int,
    val dialog_id: String,
    val msg_id: Long,
    val time: Date?
)

@Table(name = "msg")
@Entity
data class MyMsg(
    @Id val id: Long = 0,
    val from_id: Int? = null,
    val dest_id: Int? = null,
    val dialog_id: String? = null,
    val client_id: Long? = null,
    val cmd: String? = null,
    @Enumerated(EnumType.STRING) val msg_type: MsgType = MsgType.Chat,
    @Enumerated(EnumType.STRING) val chat_type: ChatType = ChatType.Text,
    @Enumerated(EnumType.STRING) var state: AckState? = null,
    var body: String? = null,
    var time: Date? = null

) {
    fun toMsg(): Msg {
        val bean = Msg(
            this.id,
            this.from_id,
            this.dest_id,
            this.dialog_id,
            this.client_id,
            this.msg_type,
            this.state,
            this.body,
            this.time?.time,
            this.cmd,
            this.chat_type
        )
        return bean
    }
}

fun Msg.toBean(): MyMsg {
    val bean = MyMsg(this.id,
        this.from_id,
        this.dest_id,
        this.dialog_id,
        this.client_id,
        this.cmd,
        this.msg_type,
        this.chat_type,
        this.state,
        this.body,
        this.time?.let { Date(it) })
    return bean
}*/
