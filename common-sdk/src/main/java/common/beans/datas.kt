package common.beans

import java.io.Serializable
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "user")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null,
    val name: String,
    @Transient
    val role: Role = Role.User
)

@Entity
@Table(name = "friends")
data class Friends(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null,
    val from_id: Int,
    val dest_id: Int,
    val dialog_id: String,
    val status: Int
) : Serializable


@Table(name = "msg_offset")
@Entity
data class MsgOffset(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int?,
    val user_id: Int,
    val dialog_id: String,
    val msg_id: Long,
    val time: Date?
) : Sharding {
    override fun tableValue(): Int {
        return this.user_id
    }

    override fun DBValue(): Int {
        return this.user_id
    }

}

@Entity
@Table(name = "group_user")
data class GroupUser(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int? = null,
    val dialog_id: String,
    val user_id: Int,
    val time: Date?
)

@Entity
@Table(name = "group_info")
data class GroupInfo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val dialog_id: String,
    val name: String,
    val owner: Int,
    val time: Date? = null
) : Serializable

data class GateWayInfo(val name: String, val ip: String, val port: Int)