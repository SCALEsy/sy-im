package im.server.logic.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
open class RabbitmqConfig {
    @Value("\${im.server.name}")
    private lateinit var name: String

    @Value("\${im.server.save.exchange}")
    private lateinit var save_exchange: String

    @Value("\${im.server.msg.exchange}")
    private lateinit var msg_exchange: String

    @Value("\${im.server.msg-reached.exchange}")
    private lateinit var msg_reached_exchange: String
    /*@Bean
    open fun queue(): Queue {
        return Queue(name, false, false, true)
    }

    @Bean
    open fun exchange(): DirectExchange {
        return DirectExchange(msg_exchange, true, true)
    }*/

    /*@Bean
    open fun bind(): Binding {
        return BindingBuilder.bind(queue()).to(exchange()).with(name)
    }*/

    /*@Bean(name = ["gateway"])
    open fun sender(template: RabbitTemplate): MQSender {
        return RabbitMqSender(template, msg_exchange)
    }

    @Bean(name = ["msg-reached"])
    open fun msg_reached(template: RabbitTemplate): MQSender {
        return RabbitMqSender(template, msg_reached_exchange)
    }

    @Bean
    open fun converter(): MessageConverter {
        val mapper = ObjectMapper().registerKotlinModule()
        val converter = Jackson2JsonMessageConverter(mapper)
        //converter.classMapper = classMapper()
        return converter
    }
*/
    /*@Bean
    open fun classMapper(): ClassMapper {
        val mapper = DefaultClassMapper()
        val map = mapOf("im.server.logic.beans.MsgReachedInfo" to MsgReachedInfo::class.java)
        mapper.setIdClassMapping(map)
        return mapper
    }*/
}