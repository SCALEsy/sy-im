package im.server.web.configs

//@Configuration
open class RabbitmqConfig {
    /*@Value("\${im.server.save.queue}")
    private lateinit var name: String

    @Value("\${im.server.save.exchange}")
    private lateinit var exchange_name: String

    @Value("\${im.server.msg-reached.name}")
    private lateinit var reached_name: String

    @Value("\${im.server.msg-reached.exchange}")
    private lateinit var reached_exchange_name: String

    @Bean
    open fun queue(): Queue {
        return Queue(name, false, false, true)
    }

    @Bean
    open fun exchange(): FanoutExchange {
        return FanoutExchange(exchange_name, true, false)
    }

    @Bean
    open fun bind(): Binding {
        return BindingBuilder.bind(queue()).to(exchange())
    }


    @Bean
    open fun queueReached(): Queue {
        return Queue(reached_name, false, false, true)
    }

    @Bean
    open fun exchangeReached(): FanoutExchange {
        return FanoutExchange(reached_exchange_name, true, false)
    }

    @Bean
    open fun bindReached(): Binding {
        return BindingBuilder.bind(queueReached()).to(exchangeReached())
    }

    @Bean
    open fun converter(): MessageConverter {
        val mapper = ObjectMapper().registerKotlinModule()
        val converter = Jackson2JsonMessageConverter(mapper)
        converter.classMapper = classMapper()

        return converter
    }

    @Bean
    open fun classMapper(): ClassMapper {
        val mapper = DefaultClassMapper()
        val map = mapOf("im.server.logic.beans.MsgReachedInfo" to MsgReachedInfo::class.java)
        mapper.setIdClassMapping(map)
        mapper.setTrustedPackages("*")
        return mapper
    }*/

    /*@Bean
    open fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }*/
}