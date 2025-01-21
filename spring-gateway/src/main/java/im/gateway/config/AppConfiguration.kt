package im.gateway.config

import common.server.LoginService
import common.server.MsgRpcService
import im.gateway.sdk.ImServer
import im.gateway.sdk.ImServerConfig
import im.gateway.sdk.caches.ChannelCache
import org.apache.dubbo.config.annotation.DubboReference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ScheduledThreadPoolExecutor

@Configuration
open class AppConfiguration {
    /* @Value("\${im.server.port}")
     private val port: String? = null*/
    @Value("\${im.server.name}")
    private lateinit var name: String

    /* @Value("\${im.server.save.exchange}")
     private lateinit var save_exchange: String*/

    @Autowired
    private lateinit var channelCache: ChannelCache

    @DubboReference
    private lateinit var loginService: LoginService

    @DubboReference
    private lateinit var msgRpcService: MsgRpcService

    @Bean
    @ConfigurationProperties(prefix = "im.server")
    open fun config(): ImServerConfig {
        return ImServerConfig()
    }

    @Bean
    open fun scheduled(): ScheduledThreadPoolExecutor {
        return ScheduledThreadPoolExecutor(1)
    }


    @Bean
    open fun socketServer(): ImServer {
        return ImServer(config(), channelCache, loginService, msgRpcService)
    }

}
