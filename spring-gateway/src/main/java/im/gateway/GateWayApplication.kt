package im.gateway

import im.gateway.config.ZookeeperRegister
import im.gateway.sdk.ImServer
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

@SpringBootApplication
@EnableDubbo
open class GateWayApplication : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var scheduled: ScheduledThreadPoolExecutor

    @Autowired
    private lateinit var zookeeperRegister: ZookeeperRegister

    @Autowired
    private lateinit var imServer: ImServer


    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            SpringApplication.run(GateWayApplication::class.java, *args)
        }
    }

    override fun run(vararg args: String?) {
        imServer.start()
        zookeeperRegister.register()
        scheduled.scheduleAtFixedRate({
            val data = imServer.serverData()
            logger.info(
                "server online:{} receive:{} fail_ack:{} mq rev:{} mq fail:{}",
                data.online_users,
                data.receive_count.get(),
                data.ack_fail.get(),
                data.mq_receive.get(),
                data.mq_fail.get()
            )
        }, 10, 10, TimeUnit.SECONDS)
    }
}
