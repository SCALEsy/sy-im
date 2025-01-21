package im.server.web.services.transactionals

import common.beans.GroupInfo
import common.beans.GroupUser
import common.server.GroupInfoRepository
import common.server.GroupUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
open class TransactionalService {
    @Autowired
    private lateinit var groupUserRepository: GroupUserRepository

    @Autowired
    private lateinit var groupInfoRepository: GroupInfoRepository

    @Transactional(rollbackFor = [Exception::class])
    open fun addGroup(info: GroupInfo, users: List<GroupUser>) {
        groupInfoRepository.save(info)
        groupUserRepository.saveAll(users)
    }
}