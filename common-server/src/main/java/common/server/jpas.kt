package common.server


import common.beans.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Repository
open interface UserRepository : JpaRepository<User, Int>

@Repository
open interface FriendsRepository : JpaRepository<Friends, Int> {
    @Query(nativeQuery = true, value = "select * from friends where from_id=?1 and status =1")
    open fun load(id: Int): List<Friends>

    /*@Query(nativeQuery = true, value = "select * from friends where from_id=?1 and dest_id=?2")
    open fun load_by_pair(from_id: Int, dest_id: Int): Friends?*/


}


@Repository
open interface MsgRepository : JpaRepository<MsgBean, Long> {
    @Query(
            nativeQuery = true,
            value = "select * from msg where id>?2 and `dialog_id`=?1 and msg_type in ('GroupChat','Chat') and state in ('ServerReceive','ServerSend')"
    )
    open fun unread(dialog_id: String, msg_id: Long, page: Pageable): Page<MsgBean>

    /*@Query(
        nativeQuery = true,
        value = "INSERT INTO msg(`id`, `from_id`, `dest_id`, `dialog_id`, `client_id`, `cmd`, `msg_type`, `chat_type`, `state`, `body`,`time`) " +
                "VALUES (#{#bean.id}, #{#bean.from_id}, #{#bean.dest_id}, #{#bean.dialog_id}," +
                " #{#bean.client_id}, #{#bean.cmd}, #{#bean.msg_type.name()}," +
                "#{#bean.chat_type?.name()}, #{#bean.state?.name()}, #{#bean.body}, #{#bean.time})"
    )
    @Modifying
    @Transactional
    open fun insert0(@Param("bean") bean: MsgBean)*/


    @Query(
            nativeQuery = true,
            value = "INSERT INTO msg(`id`, `from_id`, `dest_id`, `dialog_id`, `client_id`, `cmd`, `msg_type`, `chat_type`, `state`, `body`,`time`) " +
                    "VALUES (?1,?2,?3,?4,?5, ?6,?7,?8,?9,?10, ?11)"
    )
    @Modifying
    @Transactional
    open fun insert0(
            id: Long,
            from_id: Int,
            dest_id: Int?,
            dialog_id: String?,
            client_id: Long?,
            cmd: String?,
            msg_type: String,
            chat_type: String?,
            state: String?,
            body: String?,
            time: Date?
    )


    @Query(
            nativeQuery = true,
            value = "UPDATE msg SET  `state` = ?2, `time` = ?3 WHERE `id` = ?1"
    )
    @Modifying
    @Transactional
    open fun update_state(id: Long, state: String?, time: Date)
}

fun MsgRepository.insert(bean: MsgBean) {

    insert0(
            bean.id,
            bean.from_id!!,
            bean.dest_id,
            bean.dialog_id,
            bean.client_id,
            bean.cmd,
            bean.msg_type.name,
            bean.chat_type?.name,
            bean.state?.name,
            bean.body,
            bean.time
    )

}

@Repository
interface GroupUserRepository : JpaRepository<GroupUser, Int> {
    @Query(nativeQuery = true, value = "select * from `group_user` where dialog_id=?1")
    fun load_group_users(dialog_id: String): List<GroupUser>

    /* @Query(nativeQuery = true, value = "select * from `group_user` where user_id=?1")
     fun load_all(user_id: Int): List<GroupUser>*/
}

@Repository
interface GroupInfoRepository : JpaRepository<GroupInfo, Int> {

    @Query(
            nativeQuery = true,
            value = "select group_info.* from group_info left join group_user  on group_info.dialog_id=group_user.dialog_id where group_user.user_id=?1"
    )
    fun load_by_user(user_id: Int): List<GroupInfo>

    /*@Query(
        nativeQuery = true,
        value = "select * from group_info  where dialog_id in (?1)"
    )
    fun load_by_dialogs(dialog_id: List<String>): List<GroupInfo>*/
}

@Repository
interface MsgOffsetRepository : JpaRepository<MsgOffset, Int> {
    @Query(nativeQuery = true, value = "select * from `msg_offset` where user_id=?1 and dialog_id=?2")
    fun load_single(user_id: Int, dialog_id: String): MsgOffset?

    @Modifying
    @Query(nativeQuery = true, value = "update msg_offset set msg_id=?1 where user_id=?2 and dialog_id=?3 ")
    @Transactional
    fun replaceMsgId(msg_id: Long, user_id: Int, dialog_id: String)
}

