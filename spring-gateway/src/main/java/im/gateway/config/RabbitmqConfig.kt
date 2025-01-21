package im.gateway.config

import org.springframework.context.annotation.Configuration

@Configuration
open class RabbitmqConfig {
    /*@Value("\${im.server.name}")
    private lateinit var name: String

    @Value("\${im.server.save.exchange}")
    private lateinit var save_exchange: String

    @Value("\${im.server.msg.exchange}")
    private lateinit var msg_exchange: String

    @Bean
    open fun queue(): Queue {
        return Queue(name, false, false, true)
    }

    @Bean
    open fun exchange(): DirectExchange {
        return DirectExchange(msg_exchange, true, true)
    }

    @Bean
    open fun bind(): Binding {
        return BindingBuilder.bind(queue()).to(exchange()).with(name)
    }

    @Bean
    open fun converter(): MessageConverter {
        val mapper = ObjectMapper()
        mapper.registerModule(KotlinModule())
        return Jackson2JsonMessageConverter(mapper)
    }*/
}