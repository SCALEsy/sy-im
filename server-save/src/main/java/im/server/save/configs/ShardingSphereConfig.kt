package im.server.save.configs


import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.HintShardingStrategyConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.util.*
import javax.sql.DataSource


@Configuration
open class ShardingSphereConfig {
    @Value("\${datasource.url0}")
    private lateinit var url0: String

    @Value("\${datasource.url1}")
    private lateinit var url1: String

    @Value("\${datasource.url2}")
    private lateinit var url2: String

    @Value("\${datasource.driverClassName}")
    private lateinit var driver: String

    @Value("\${datasource.username}")
    private lateinit var name: String

    @Value("\${datasource.password}")
    private lateinit var password: String

    @Bean
    open fun datasource1(): DataSource {
        val config = HikariConfig()
        config.driverClassName = driver
        config.jdbcUrl = url0
        config.username = name
        config.password = password
        return HikariDataSource(config)
    }

    @Bean
    open fun datasource2(): DataSource {
        val config = HikariConfig()
        config.driverClassName = driver
        config.jdbcUrl = url1
        config.username = name
        config.password = password
        return HikariDataSource(config)
    }

    @Bean
    open fun datasource3(): DataSource {
        val config = HikariConfig()
        config.driverClassName = driver
        config.jdbcUrl = url2
        config.username = name
        config.password = password
        return HikariDataSource(config)
    }

    @Bean
    @Primary
    open fun config(): DataSource {
        val msg = ShardingTableRuleConfiguration(
            "msg",
            "im\${0..2}.msg\${0..2}"
        )

        val offset = ShardingTableRuleConfiguration(
            "msg_offset",
            "im\${0..2}.msg_offset\${0..2}"
        )
        /*msg_rule.databaseShardingStrategy = StandardShardingStrategyConfiguration("from_id", "db-inline")
        msg_rule.tableShardingStrategy = StandardShardingStrategyConfiguration("from_id", "table-inline")*/


        msg.databaseShardingStrategy = HintShardingStrategyConfiguration("force-db")
        msg.tableShardingStrategy = HintShardingStrategyConfiguration("force-table")

        offset.databaseShardingStrategy = HintShardingStrategyConfiguration("force-db")
        offset.tableShardingStrategy = HintShardingStrategyConfiguration("force-table")

        val rule_config = ShardingRuleConfiguration()
        rule_config.tables.add(msg)
        rule_config.tables.add(offset)


        rule_config.shardingAlgorithms["force-db"] = ShardingSphereAlgorithmConfiguration("FORCE", Properties())
        rule_config.shardingAlgorithms["force-table"] = ShardingSphereAlgorithmConfiguration("FORCE", Properties())

        // 配置 t_order 被拆分到多个子库的算法
        /*val dbShardingAlgorithmProps = Properties()
        dbShardingAlgorithmProps.setProperty("algorithm-expression", "im\${from_id % 3}")
        rule_config.shardingAlgorithms["db-inline"] =
            ShardingSphereAlgorithmConfiguration("INLINE", dbShardingAlgorithmProps)
        // 配置 t_order 被拆分到多个子表的算法
        val tableShardingAlgorithmProps = Properties()
        tableShardingAlgorithmProps.setProperty("algorithm-expression", "msg\${from_id % 3}")
        rule_config.shardingAlgorithms["table-inline"] =
            ShardingSphereAlgorithmConfiguration("INLINE", tableShardingAlgorithmProps)*/


        val map = mapOf("im0" to datasource1(), "im1" to datasource2(), "im2" to datasource3())
        val sourceProperty = Properties()
        sourceProperty.put("sql-show", true)
        return ShardingSphereDataSourceFactory.createDataSource(map, listOf(rule_config), sourceProperty)
    }
}