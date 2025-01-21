package im.server.web.controllers

import common.beans.WebResponse
import im.server.web.aop.Auth
import im.server.web.services.ZookeeperListenerService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["gateway"], headers = ["TOKEN"])
open class GatewayController {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var zookeeperListenerService: ZookeeperListenerService

    @Auth
    @GetMapping("/all")
    open fun all(): WebResponse<Any> {
        try {
            val list = zookeeperListenerService.loadAll()
            return WebResponse.ok(list)
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
            return WebResponse.error(500, "load all gateway fail")
        }
    }

    @Auth
    @GetMapping("/balance")
    open fun balance(user_id: Int): WebResponse<Any> {
        try {
            val list = zookeeperListenerService.loadBalance(user_id)
            return WebResponse.ok(list)
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
            return WebResponse.error(500, "load all gateway fail")
        }
    }

}