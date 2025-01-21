package im.server.web.services

import common.beans.GroupInfo
import common.beans.GroupUser
import common.server.GroupInfoRepository
import common.server.GroupUserRepository
import im.server.web.services.transactionals.TransactionalService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils

@Service
open class GroupService {
    @Autowired
    private lateinit var groupUserRepository: GroupUserRepository

    @Autowired
    private lateinit var groupInfoRepository: GroupInfoRepository

    @Autowired
    private lateinit var transactionalService: TransactionalService


    open fun add(user_id: Int, name: String, users: List<Int>) {
        val all = listOf(user_id).union(users)
        val dialogId = DigestUtils.md5DigestAsHex("$user_id-${System.currentTimeMillis()}".toByteArray())
        val groupUsers = all.map { id ->

            GroupUser(null, dialogId, id, null)
        }
        val info = GroupInfo(id = null, dialogId, name, user_id)
        transactionalService.addGroup(info, groupUsers)
    }

    fun load(user_id: Int): List<GroupInfo> {
        /*val groups = groupUserRepository.load_all(user_id).map { x -> x.dialog_id }
        return groupInfoRepository.load_by_dialogs(groups)*/
        return groupInfoRepository.load_by_user(user_id)
    }
}