package im.server.logic.configs

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import im.server.logic.distributed.MQSender
import im.server.logic.distributed.RocketmqSender
import org.apache.rocketmq.spring.core.RocketMQTemplate
import org.apache.rocketmq.spring.support.RocketMQMessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.messaging.converter.CompositeMessageConverter
import org.springframework.messaging.converter.MappingJackson2MessageConverter

@Configuration
open class RocketmqConfig {
    @Value("\${im.server.name}")
    private lateinit var name: String

    @Value("\${im.server.save.exchange}")
    private lateinit var save_exchange: String

    @Value("\${im.server.msg.exchange}")
    private lateinit var msg_exchange: String

    @Value("\${im.server.msg-reached.exchange}")
    private lateinit var msg_reached_exchange: String

    @Value("\${im.server.name}")
    private lateinit var server_name: String
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
    @Bean
    open fun sender(template: RocketMQTemplate): RocketmqClient {
        return RocketmqClient(template)
    }

    @Bean(name = ["gateway"])
    open fun wrapper_gateway(template: RocketMQTemplate): MQSender {
        return RocketmqSender(msg_exchange, server_name, sender(template))
    }

    @Bean(name = ["msg-reached"])
    open fun wrapper_reached(template: RocketMQTemplate): MQSender {
        return RocketmqSender(msg_reached_exchange, server_name, sender(template))
    }

    @Bean(name = ["msg-save"])
    open fun wrapper_msg(template: RocketMQTemplate): MQSender {
        return RocketmqSender(save_exchange, server_name, sender(template))
    }

    @Bean
    @Primary
    open fun converters(): RocketMQMessageConverter {

        //mapper.registerModule(KotlinModule())

        val converter = RocketMQMessageConverter()
        val composite = converter.messageConverter as CompositeMessageConverter

        composite.converters.forEach { c ->
            if (c is MappingJackson2MessageConverter) {
                val mapper = ObjectMapper().registerKotlinModule()
                mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                mapper.activateDefaultTyping(
                    //BasicPolymorphicTypeValidator.builder().allowIfBaseType(Any::class.java).build(),
                    LaissezFaireSubTypeValidator.instance,
                    ObjectMapper.DefaultTyping.NON_FINAL,
                    JsonTypeInfo.As.PROPERTY
                )
                c.objectMapper = mapper
            }
        }
        return converter
    }
}