package im.server.web

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(basePackages = ["common.beans", "common.server"])
@EnableJpaRepositories(basePackages = ["common.server"])
open class WebApplication {
    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            val context = SpringApplication.run(WebApplication::class.java, *args)
        }
    }
}