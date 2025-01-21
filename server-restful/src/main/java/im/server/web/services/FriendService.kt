package im.server.web.services

import common.beans.Friends
import common.server.FriendsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils
import kotlin.math.max
import kotlin.math.min

@Service
class FriendService {
    @Autowired
    private lateinit var friendsRepository: FriendsRepository

    fun add(from: Int, dest: Int) {
        val min = min(from, dest)
        val max = max(from, dest)
        val dialog = DigestUtils.md5DigestAsHex("$min-$max".toByteArray())

        val friend1 = Friends(id = null, from, dest, dialog, 1)
        val friend2 = Friends(id = null, dest, from, dialog, 1)

        friendsRepository.save(friend1)
        friendsRepository.save(friend2)
    }

    fun load(id: Int): List<Friends> {
        val list = friendsRepository.load(id)
        return list
    }
}