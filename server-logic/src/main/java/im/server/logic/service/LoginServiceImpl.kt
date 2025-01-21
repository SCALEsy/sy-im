package im.server.logic.service

import common.server.LoginService
import im.server.logic.caches.UserCache
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.dubbo.config.annotation.DubboService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.ThreadPoolExecutor

//@MotanService()
@DubboService
open class LoginServiceImpl : LoginService {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userCache: UserCache

    @Autowired
    private lateinit var executor: ThreadPoolExecutor
    override fun check(token: String): Boolean {
        return true
    }

    override fun login(user_id: Int, channel_id: String, server_id: String): Boolean {
        userCache.save(user_id, channel_id, server_id)
        return true
    }

    override fun login_out(user_id: Int): Boolean {
        userCache.remove(user_id)
        return true
    }

    override fun refresh(user_id: Int) {
        try {
            executor.submit {
                userCache.refresh_time(user_id)
            }
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
        }
    }


}