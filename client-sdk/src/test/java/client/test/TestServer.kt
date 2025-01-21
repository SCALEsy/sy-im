package client.test

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import common.handler.HeartbeatTrigger
import common.netty.CustomNioServerSocketChannel
import common.netty.JsonDecoder
import common.netty.JsonEncoder
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder
import io.netty.handler.timeout.IdleStateHandler
import org.apache.commons.lang3.concurrent.BasicThreadFactory
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

object TestServer {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val bootstrap = ServerBootstrap()
    private val parent = NioEventLoopGroup()
    private val child = NioEventLoopGroup(3, BasicThreadFactory.Builder().namingPattern("nio-%d").build())

    private val port = 9090
    private val mapper = ObjectMapper()

    init {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        mapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        )
    }

    fun start() {
        try {
            bootstrap.channel(CustomNioServerSocketChannel::class.java).group(parent, child)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(channel: SocketChannel) {
                        channel.pipeline()
                            .addLast(
                                "idle",
                                IdleStateHandler(30, 0, 0, TimeUnit.SECONDS)
                            )
                            .addLast("heartbeat", HeartbeatTrigger())

                            .addLast("prepender", LengthFieldPrepender(4))
                            .addLast("encode", ByteArrayEncoder())
                            .addLast("json-encoder", JsonEncoder(mapper))

                            .addLast("frame-decode", LengthFieldBasedFrameDecoder(4096, 0, 4, 0, 4))
                            .addLast("decode", ByteArrayDecoder())
                            .addLast("json-decoder", JsonDecoder(mapper))

                            //.addLast("ping", PigggHandler())
                            //.addLast(JsonObjectDecoder())
                            .addLast("handler", TestServerHandler())
                    }
                })
            logger.info("im server:{} bind:{}", "server", port)
            val future = bootstrap.bind(port).sync()
            future.addListener { f ->
                if (f.isSuccess) {
                    logger.info("start server")
                }
            }
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
            parent.shutdownGracefully()
            child.shutdownGracefully()
        }
    }

    @JvmStatic
    fun main(vararg args: String) {
        start()
    }
}