package im.server.save.configs

import im.server.save.task.SaveTaskDispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AppConfig {

    @Bean
    open fun dispatcher(): SaveTaskDispatcher {
        return SaveTaskDispatcher(8)
    }
}