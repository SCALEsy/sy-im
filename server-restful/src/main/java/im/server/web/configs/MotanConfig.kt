package server.web.configs

import org.springframework.context.annotation.Configuration


@Configuration
open class MotanConfig {


    /*@Bean
    open fun annotationBean(): AnnotationBean {
        val annotation = AnnotationBean()
        //annotation.`package` = "im.server.logic"

        return annotation
    }

    @Bean(name = ["protocol"])
    open fun protocolConfig1(): ProtocolConfigBean {
        val config = ProtocolConfigBean()
        config.setDefault(true)
        config.name = "motan"
        config.maxContentLength = 1048576
        return config
    }

    @Bean(name = ["registry"])
    open fun registryConfig(@Value("\${im.server.zookeeper.url}") zk_url: String): RegistryConfigBean {
        val config = RegistryConfigBean()
        config.regProtocol = "zk"
        config.address = zk_url
        config.connectTimeout = 10000
        config.name = "data-client"
        return config
    }*/

    /*@Bean
    open fun baseServiceConfig(): BasicServiceConfigBean? {
        val config = BasicServiceConfigBean()
        config.export = "imServerLogic:8002"
        config.group = "logics"
        config.setAccessLog(false)
        config.shareChannel = true
        config.module = "server-logic-rpc"
        config.application = "server-logic"
        config.setRegistry("registryConfig1")
        return config
    }*/

    /*@Bean(name = ["logic-rpc"])
    open fun baseRefererConfig(): BasicRefererConfigBean {
        val config = BasicRefererConfigBean()
        config.setProtocol("protocol")
        config.group = "logics"
        config.module = "server-logic-rpc"
        config.application = "server-logic"
        config.setRegistry("registry")
        config.setCheck(false)
        config.setAccessLog(true)
        config.retries = 2
        config.throwException = true
        return config

    }*/
}