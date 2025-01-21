package im.server.logic

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(basePackages = ["common.beans", "common.server"])
@EnableJpaRepositories(basePackages = ["common.server"])
@EnableDubbo
open class LogicApplication {
    companion object {
        @JvmStatic
        open fun main(vararg args: String) {
            val context = SpringApplication.run(LogicApplication::class.java, *args)

        }
    }
}