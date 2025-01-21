package im.server.web.controllers

import common.beans.Role
import common.beans.WebResponse
import common.exceptions.ImException
import im.server.web.aop.Auth
import im.server.web.services.UserService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["user"])
open class UserController {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userService: UserService

    @PostMapping("/add", headers = ["TOKEN"])
    @Auth(Role.Admin)
    open fun add(name: String): WebResponse<Any> {
        return try {
            userService.add(name)
            WebResponse.ok()
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
            WebResponse.error(500, "add user fail")
        }
    }

    @GetMapping("/login")
    open fun login(user_id: Int): WebResponse<Any> {
        return try {
            WebResponse.ok(userService.login(user_id))
        } catch (e: ImException) {
            WebResponse.error(e)
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
            WebResponse.error(500, "add user fail")
        }
    }

}