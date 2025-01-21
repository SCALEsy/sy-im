package common.server.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import common.beans.Role
import common.beans.User
import common.exceptions.ImException
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import java.util.*


object JWTUtils {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val salt = "sy-im.fgbnbb"

    /**
     * 生成JWT token
     */

    fun generateToken(user_id: Int, name: String, role: Role = Role.User): String {
        //预设一个token过期时间
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1) //过期时间为1小时
        val token = JWT.create()
                .withHeader(HashMap()) //Header
                .withClaim("user_id", user_id) //PayLoad
                .withClaim("name", name)
                .withClaim("role", role.name)
                .withExpiresAt(calendar.getTime()) //过期时间
                .sign(Algorithm.HMAC256(salt)) //签名用的密钥secret
        return token
    }

    fun verify(token: String): Boolean {
        //创建解析对象，使用的算法和secret要和创建token时保持一致
        try {
            val jwtVerifier: JWTVerifier = JWT.require(Algorithm.HMAC256(salt)).build()
            val decodedJWT: DecodedJWT = jwtVerifier.verify(token)
            val user_id = decodedJWT.getClaim("user_id")
            val name = decodedJWT.getClaim("name")
            val time = decodedJWT.expiresAt
            return time.after(Date())
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
            return false
        }
    }

    fun user(token: String): User {
        try {
            val jwtVerifier: JWTVerifier = JWT.require(Algorithm.HMAC256(salt)).build()
            val decodedJWT: DecodedJWT = jwtVerifier.verify(token)
            val user_id = decodedJWT.getClaim("user_id").asInt()
            val name = decodedJWT.getClaim("name").asString()
            val role = Role.valueOf(decodedJWT.getClaim("role").asString())
            val user = User(user_id, name, role)
            return user
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
            throw ImException.InvalidToken
        }
    }


}