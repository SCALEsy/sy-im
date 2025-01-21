package im.server.logic.configs

import im.server.logic.caches.MsgIdGenerator
import im.server.logic.caches.SnowFlakeMsgIdGenerator
import im.server.logic.chains.DefaultChainPipeline
import im.server.logic.chains.MsgChainPipeline
import im.server.logic.service.pipelines.PipelineMsgRunnable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ScheduledThreadPoolExecutor

@Configuration
open class AppConfig {

    @Bean
    open fun idGen(): MsgIdGenerator {
        return SnowFlakeMsgIdGenerator(1, 1)
    }

    @Bean
    open fun schedule(): ScheduledThreadPoolExecutor {

        return ScheduledThreadPoolExecutor(5)
    }

    @Bean
    open fun pipeline(pipelineMsgRunnable: PipelineMsgRunnable): MsgChainPipeline {
        return DefaultChainPipeline(pipelineMsgRunnable)
    }
}