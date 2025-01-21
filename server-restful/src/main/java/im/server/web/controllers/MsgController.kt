package im.server.web.controllers

import common.beans.WebResponse
import im.server.web.aop.Auth
import im.server.web.services.MsgService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["msg"], headers = ["TOKEN"])
open class MsgController {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var msgService: MsgService

    @GetMapping("/unread")
    @Auth
    open fun add(dialog_id: String, page: Int, size: Int, id: Long? = null): WebResponse<Any> {
        try {
            return msgService.unread(dialog_id, page, size, id)
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
            return WebResponse.error(500, "add user fail")
        }
    }

}