package common.server


import common.beans.AckState
import common.beans.ChatType
import common.beans.Msg
import common.beans.MsgType
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Table(name = "msg")
@Entity
data class MsgBean(
    @Id
    var id: Long = 0,
    val from_id: Int? = null,
    var dest_id: Int? = null,
    var dialog_id: String? = null,
    val client_id: Long? = null,
    @Enumerated(EnumType.STRING) val msg_type: MsgType = MsgType.Chat,
    @Enumerated(EnumType.STRING) var state: AckState? = null,

    //@Transient
    var body: String? = null,
    @Temporal(TemporalType.TIMESTAMP)
    var time: Date? = null,
    var cmd: String? = null,
    @Enumerated(EnumType.STRING) var chat_type: ChatType? = ChatType.Text,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other !is MsgBean) {
            return false
        }
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }

}


fun Msg.toBean(): MsgBean {

    val bean = MsgBean(
        this.id, this.from_id, this.dest_id,
        this.dialog_id, this.client_id, this.msg_type,
        this.state, this.body.toString(),
        this.time?.let { Date(it) },
        this.cmd, this.chat_type
    )
    return bean

}