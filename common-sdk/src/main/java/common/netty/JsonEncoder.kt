package common.netty

import com.fasterxml.jackson.databind.ObjectMapper
import common.beans.Msg
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import org.slf4j.LoggerFactory

@Sharable
class JsonEncoder(val mapper: ObjectMapper) : MessageToMessageEncoder<Msg>() {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun encode(ctx: ChannelHandlerContext, msg: Msg, list: MutableList<Any>) {
        val bytes = mapper.writeValueAsBytes(msg)
        list.add(bytes)
        /*val text = String(bytes)
        logger.info("send:{}", text)*/
    }
}