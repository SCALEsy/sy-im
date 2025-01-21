package sy.im.client

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import common.beans.GateWayInfo
import common.beans.ImState
import common.netty.JsonDecoder
import common.netty.JsonEncoder
import io.netty.bootstrap.Bootstrap
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
import io.netty.handler.traffic.ChannelTrafficShapingHandler
import org.apache.commons.lang3.concurrent.BasicThreadFactory
import org.slf4j.LoggerFactory
import sy.im.client.handler.SimpleClientDispatchHanler
import sy.im.client.interfaces.IClient
import sy.im.client.msg.DefaultMsgSaver
import sy.im.client.msg.MsgReceiver
import sy.im.client.msg.MsgSender
import sy.im.client.msg.UserDataCache
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ImIClient(override val config: ClientConfig) : IClient {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val bootstrap = Bootstrap()
    private val eventLoop = NioEventLoopGroup(BasicThreadFactory.Builder().namingPattern("netty-%d").build())
    private val schedulers = ScheduledThreadPoolExecutor(1)
    private val msgSaver = DefaultMsgSaver()
    private val userDataCache = UserDataCache(config.user_id, config.web_uri, config.save_path)
    private val msgSender = MsgSender(config, msgSaver, userDataCache, schedulers)
    private val msgReceiver = MsgReceiver(config, msgSaver, userDataCache)
    private val mapper = ObjectMapper().registerKotlinModule()


    private val scheduled = ScheduledThreadPoolExecutor(
        1, BasicThreadFactory.Builder().namingPattern("ping-thread-%d").build()
    )
    val ping_task = Runnable { msgSender.send_ping() }
    //build - init - run -close

    /**
     *
     *
     *          cache msg
     *         ^
     *        /
     *     queue
     *      ^
     *      |
     *    sender        receiver
     *
     */


    init {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        mapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY
        )
        //userDataCache.init()
        msgSender.start()
        msgReceiver.start()
        bootstrap.group(eventLoop).channel(NioSocketChannel::class.java)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            //.option(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark(256 * 1024, 512 * 1024))
            //.option(ChannelOption.TCP_NODELAY, true)
            .handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(channel: SocketChannel) {
                    channel.pipeline().addLast(ChannelTrafficShapingHandler(1024 * 1024, 1024 * 1024))
                        .addLast("idle", IdleStateHandler(0, config.idle_write_time, 0, TimeUnit.SECONDS))

                        .addLast("prepender", LengthFieldPrepender(4)).addLast("endcode", ByteArrayEncoder())
                        .addLast("json-encoder", JsonEncoder(mapper))

                        .addLast("frame-decode", LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4))
                        .addLast("decode", ByteArrayDecoder()).addLast("json-decoder", JsonDecoder(mapper))
                        .addLast("ping-handler", SimpleClientDispatchHanler(msgSender, msgReceiver))
                }
            })
    }

    constructor(id: Int) : this(ClientConfig(id))

    override fun connect() {
        if (!userDataCache.inited()) {
            userDataCache.init()
        }
        val info = if (config.direct_ip != null && config.direct_port != null) {
            GateWayInfo("direct", config.direct_ip!!, config.direct_port!!)
        } else {
            userDataCache.gateway()
        }
        if (info == null) {
            logger.error("load gateway error")
            return
        }
        logger.info("connect start ${info.ip}:${info.port}")
        val future = bootstrap.connect(info.ip, info.port).sync()
        future.addListener { f ->
            if (!f.isSuccess) {
                logger.error("connect fail :{}", config.retries)/*eventLoop.schedule({
                    config.retries++
                    connect()
                }, 10, TimeUnit.SECONDS)*/
            } else {
                config.channel = future.channel()
                msgSender.setChannel(config.channel!!)
                msgSender.send_greet()
                config.state = ImState.Connected

                scheduled.remove(ping_task)
                scheduled.scheduleAtFixedRate(ping_task, 5, 30, TimeUnit.SECONDS)
            }
        }
        future.channel().closeFuture()

    }

    fun send_msg(msg: String, dest_id: Int) {
        if (check_state()) {
            msgSender.send_chat(msg, dest_id)
        } else {
            logger.info("send fail case state")
        }
    }

    fun send_group(msg: String, name: String) {
        if (check_state()) {
            //outBox.send_group(msg, group_id)
            msgSender.send_group(msg, name)
        }
    }

    fun test_error() {
        if (check_state()) {
            msgSender.send_error()
        }
    }

    fun show_state() {
        logger.info("this,user_id:{}", config.user_id)
        logger.info("this state:{}", config.state)
        logger.info("active:{}", config.channel?.isActive)
        logger.info("register:{}", config.channel?.isRegistered)
        logger.info("open:{}", config.channel?.isOpen)
        logger.info("writable:{}", config.channel?.isWritable)
        logger.info("send count:{}", userDataCache.sum)
    }

    override fun set_state(state: ImState) {
        config.state = state
    }

    override fun setRetry(num: Int) {
        config.retries = num
    }


    fun load_unread(dialog_id: String) {

        val list = userDataCache.loadUnread(dialog_id)
        //logger.info("{} last id {}", list.size, list.last().id)
        val msg_id = list.last().id
        userDataCache.refresh_Id(dialog_id, msg_id)
        list.forEach { msg ->
            msgReceiver.add(msg)
        }
        msgSender.refresh_offset(dialog_id, msg_id)
    }

    fun stop() {
        config.state = ImState.LoginOut
        if (config.channel != null && config.channel!!.isActive) {
            config.channel!!.close()
        }
        msgReceiver.stop()
        eventLoop.shutdownGracefully()
    }

    private fun check_state(): Boolean {
        if (config.state != ImState.Logined || config.channel == null || !config.channel!!.isActive) {
            logger.error("state error")
            return false
        }
        return true
    }

    fun ok(): Boolean {
        return check_state()
    }

    fun sum(): Int {
        return userDataCache.sum.get()
    }

    fun fail_sum(): Int {
        return userDataCache.fail_sum.get()
    }

}

