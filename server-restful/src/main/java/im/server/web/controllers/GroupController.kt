package im.server.web.controllers

import common.beans.Role
import common.beans.WebResponse
import im.server.web.aop.Auth
import im.server.web.services.GroupService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["group"], headers = ["TOKEN"])
open class GroupController {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var groupService: GroupService

    @PostMapping("/add")
    @Auth(Role.Admin)
    open fun add(user: Int, name: String, @RequestBody users: List<Int>): WebResponse<Any> {
        return try {
            groupService.add(user, name, users)
            WebResponse.ok()
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
            WebResponse.error(500, "add friends fail")
        }
    }

    @GetMapping("mine")
    @Auth
    open fun mine(id: Int): WebResponse<Any> {
        return try {
            WebResponse.ok(groupService.load(id))
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
            WebResponse.error(500, "load friends fail")
        }

    }
}