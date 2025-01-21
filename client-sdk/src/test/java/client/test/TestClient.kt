package client.test

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.google.common.util.concurrent.RateLimiter
import common.beans.Msg
import common.beans.MsgType
import common.netty.JsonDecoder
import common.netty.JsonEncoder
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder
import io.netty.handler.timeout.IdleStateHandler
import org.apache.commons.lang3.concurrent.BasicThreadFactory
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import sy.im.client.ClientConfig
import sy.im.client.msg.DefaultMsgSaver
import sy.im.client.msg.MsgReceiver
import sy.im.client.msg.MsgSender
import sy.im.client.msg.UserDataCache
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

object TestClient {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val bootstrap = Bootstrap()
    private val eventLoop = NioEventLoopGroup(2, BasicThreadFactory.Builder().namingPattern("nio-%d").build())
    private val ip = "localhost"
    private val port = 9090
    private val mapper = ObjectMapper()

    private lateinit var channel: Channel

    @Suppress("UnstableApiUsage")
    private val limiter = RateLimiter.create(1000.0)

    val config = ClientConfig(1)
    private val schedulers = ScheduledThreadPoolExecutor(3)
    private val msgSaver = DefaultMsgSaver()
    private val userDataCache = UserDataCache(config.user_id, config.web_uri, config.save_path)
    private val msgSender = MsgSender(config, msgSaver, userDataCache, schedulers)
    private val msgReceiver = MsgReceiver(config, msgSaver, userDataCache)

    init {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        mapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY
        )
    }

    fun start() {
        try {

            bootstrap.group(eventLoop).channel(NioSocketChannel::class.java)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //.option(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark(256 * 1024, 512 * 1024))
                //.option(ChannelOption.TCP_NODELAY, true)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(channel: SocketChannel) {
                        channel.pipeline().addLast("idle", IdleStateHandler(0, 30, 0, TimeUnit.SECONDS))

                            .addLast("prepender", LengthFieldPrepender(4)).addLast("endcode", ByteArrayEncoder())
                            .addLast("json-encoder", JsonEncoder(mapper))

                            .addLast("frame-decode", LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4))
                            .addLast("decode", ByteArrayDecoder()).addLast("json-decoder", JsonDecoder(mapper))

                            //.addLast("msg-decode", ProtoToJavaDecoder())
                            .addLast("main-handler", TestClientHandler())
                    }
                })
            logger.info("im client:{} bind:{}", "server", port)
            val future = bootstrap.connect(ip, port).sync()
            future.addListener { f ->
                if (f.isSuccess) {
                    channel = future.channel()
                    msgSender.setChannel(channel)
                    logger.info("start client")

                }
            }
        } catch (e: Exception) {
            logger.error(ExceptionUtils.getStackTrace(e))
            eventLoop.shutdownGracefully()
        } finally {

        }
    }

    @JvmStatic
    fun main(vararg args: String) {
        start()
        var ping = 0L
        logger.info(Thread.currentThread().name)

        schedulers.scheduleAtFixedRate(
            {
                logger.info("ping {} {}", ping, channel.isWritable)
                val msg = Msg.ping(ping, 1)
                ping++
                send(msg)

            }, 5, 10, TimeUnit.SECONDS
        )
        //Thread.sleep(7000)
        for (id in (1L until 100000)) {
            val msg = Msg(id, 1, 2, "xx", id, msg_type = MsgType.Chat, body = "id $id")
            send(msg)
        }
    }

    fun send(msg: Msg) {
        val acq = limiter.acquire()
        if (channel.isWritable) {
            logger.info("write {}->{}", msg.id, msg.msg_type)
            channel.writeAndFlush(msg)
        } else {
            channel.flush()
            Thread.sleep(100)
        }

        //logger.info("wait:{}", acq)
    }
}