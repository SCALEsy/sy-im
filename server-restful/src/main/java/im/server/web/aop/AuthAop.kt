package im.server.web.aop

import common.beans.Role
import common.exceptions.ImException
import common.server.jwt.JWTUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


@Aspect
@Component
open class AuthAop {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    //@Pointcut("execution(* im.server.web.controllers..*(..)) && !within(im.server.web.controllers.UserController)")
    //@Pointcut("within(im.server.web.controllers.*) && !within(im.server.web.controllers.UserController)")
    @Pointcut("within(im.server.web.controllers.*)")
    open fun point_cut() {
    }

    /*@Before("point_cut()")
    open fun around() {
        logger.info("aop")

    }*/

    @Around("point_cut()&& @annotation(im.server.web.aop.Auth)")
    open fun around(joinPoint: ProceedingJoinPoint): Any {
        val req = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
        val token = req.request.getHeader("TOKEN") ?: throw ImException.InvalidToken
        val methed = joinPoint.signature as MethodSignature
        val auth = methed.method.getAnnotation(Auth::class.java)
        if (auth.role == Role.User) {
            if (!JWTUtils.verify(token)) {
                throw ImException.InvalidToken
            }
        }
        if (auth.role == Role.Admin) {
            val user = JWTUtils.user(token)
            if (user.role !== Role.Admin) {
                throw ImException.InvalidToken
            }
        }

        return joinPoint.proceed()
    }
}