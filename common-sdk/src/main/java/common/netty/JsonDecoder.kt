package common.netty

import com.fasterxml.jackson.databind.ObjectMapper
import common.beans.Msg
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import org.slf4j.LoggerFactory

@Sharable
class JsonDecoder(val mapper: ObjectMapper) : MessageToMessageDecoder<ByteArray>() {
    private val logger = LoggerFactory.getLogger(this.javaClass)


    override fun decode(ctx: ChannelHandlerContext, bytes: ByteArray, list: MutableList<Any>) {
        /*val str = String(bytes)
        logger.info("receive:{}", str)*/

        /*val tree = mapper.readTree(bytes)
        val id = tree.get("id").asLong()
        val from_id = tree.get("from_id")?.asInt()
        val dest_id = tree.get("dest_id")?.asInt()
        val dialog_id = tree.get("dialog_id")?.asText()
        val client_id = tree.get("client_id")?.asLong()
        val msg_type = MsgType.valueOf(tree.get("msg_type").asText())
        var state = tree.get("state")?.asText()?.let { AckState.valueOf(it) }
        var time = tree.get("time")?.asLong()
        var cmd = tree.get("cmd")?.asText()
        var chat_type = tree.get("chat_type")?.asText()?.let { ChatType.valueOf(it) }

        var subclass = tree.get("subclass")?.asText()
        var text = tree.get("body")

        val body = if (subclass != null && text != null) {
            val cls = Class.forName(subclass)
            mapper.treeToValue(text, cls)
        } else {
            text?.asText()
        }
        val msg = Msg(id, from_id, dest_id, dialog_id, client_id, msg_type, state, body, time, cmd, chat_type, subclass)*/

        val msg = mapper.readValue(bytes, Msg::class.java)
        list.add(msg)
    }
}