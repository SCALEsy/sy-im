package common.netty

import common.beans.transfer
import common.protobuf.MsgBuilder.ProtoMsg
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.handler.codec.MessageToMessageEncoder

class ProtoToJavaDecoder : MessageToMessageDecoder<ProtoMsg>() {
    override fun decode(ctx: ChannelHandlerContext, proto_msg: ProtoMsg, out: MutableList<Any>) {
        val msg = proto_msg.transfer()
        out.add(msg)
    }
}