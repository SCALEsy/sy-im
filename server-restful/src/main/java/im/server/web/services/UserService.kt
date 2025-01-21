package im.server.web.services

import common.beans.User
import common.exceptions.ImException
import common.server.UserRepository
import common.server.jwt.JWTUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Service
class UserService {
    @Autowired
    private lateinit var userRepository: UserRepository

    fun add(name: String) {
        val user = User(name = name)
        userRepository.save(user)
    }

    fun login(userId: Int): String {
        val option = userRepository.findById(userId)
        if (!option.isPresent) {
            throw ImException.LoginError
        }
        val user = option.get()
        return JWTUtils.generateToken(user.id!!, user.name)
    }
}