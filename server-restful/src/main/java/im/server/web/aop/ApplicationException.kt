package im.server.web.aop

import common.beans.WebResponse
import common.exceptions.ImException
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
class ApplicationException {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(value = [Exception::class])
    @ResponseBody
    fun all(e: Exception): WebResponse<Any> {
        logger.error(ExceptionUtils.getStackTrace(e))
        return WebResponse.error(ImException.UnknownError)
    }

    @ExceptionHandler(value = [ImException::class])
    @ResponseBody
    fun im(e: ImException): WebResponse<Any> {
        logger.error(ExceptionUtils.getStackTrace(e))
        return WebResponse.error(e)
    }
}