package im.gateway.sdk

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import common.beans.ImState
import common.handler.HeartbeatTrigger
import common.netty.CustomNioServerSocketChannel
import common.netty.JsonDecoder
import common.netty.JsonEncoder
import common.server.LoginService
import common.server.MsgRpcService
import im.gateway.sdk.caches.ChannelCache
import im.gateway.sdk.caches.ServerData
import im.gateway.sdk.handler.SimpleServerDispatchHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder
import io.netty.handler.timeout.IdleStateHandler
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class ImServer(
    val config: ImServerConfig,
    val channelCache: ChannelCache,
    val loginService: LoginService,
    val msgRpcService: MsgRpcService,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val bootstrap = ServerBootstrap()
    private val parent = NioEventLoopGroup()
    private val child = NioEventLoopGroup()
    private var state = ImState.Lost

    private val global = GlobalChannelTrafficShapingHandler(
        child,
        100 * 1024 * 1024,
        100 * 1024 * 1024,
        1 * 1024 * 1024,
        1 * 1024 * 1024
    )
    private val serverData = ServerData()


    private val mapper = ObjectMapper()

    init {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        //mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        //mapper.configure(Se)

        mapper.activateDefaultTyping(
            //BasicPolymorphicTypeValidator.builder().allowIfBaseType(Any::class.java).build(),
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
                            .addLast(global)
                            .addLast(
                                "idle",
                                IdleStateHandler(config.max_read_time_second, 0, 0, TimeUnit.SECONDS)
                            )
                            .addLast("heartbeat", HeartbeatTrigger())
                            .addLast("prepender", LengthFieldPrepender(4))
                            .addLast("encode", ByteArrayEncoder())
                            .addLast("json-encoder", JsonEncoder(mapper))

                            .addLast("frame-decode", LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4))
                            .addLast("decode", ByteArrayDecoder())
                            .addLast("json-decoder", JsonDecoder(mapper))

                            //.addLast("pong", PongHandler())
                            .addLast(
                                "handler", SimpleServerDispatchHandler(
                                    channelCache,
                                    loginService,
                                    msgRpcService,
                                    config.name,
                                    serverData
                                )
                            )
                    }
                })
            logger.info("im server:{} bind:{}", config.name, config.port)
            val future = bootstrap.bind(config.port).sync()
            future.addListener { f ->
                if (f.isSuccess) {
                    state = ImState.Bind
                    //mqHandler.start()
                }
            }
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
            parent.shutdownGracefully()
            child.shutdownGracefully()
        }
    }

    fun serverData(): ServerData {
        return serverData
    }
}


