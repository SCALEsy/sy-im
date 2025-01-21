package im.server.save

import im.server.save.task.SaveTaskDispatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@SpringBootApplication
@EntityScan(basePackages = ["common.beans", "common.server"])
@EnableJpaRepositories(basePackages = ["common.server"])
open class DataApplication : CommandLineRunner {
    @Autowired
    private lateinit var dispatcher: SaveTaskDispatcher

    companion object {
        @JvmStatic
        open fun main(vararg args: String) {
            val context = SpringApplication.run(DataApplication::class.java, *args)
        }
    }

    override fun run(vararg args: String) {
        dispatcher.start()
    }
}