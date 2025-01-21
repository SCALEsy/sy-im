package im.server.web.configs


import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.HintShardingStrategyConfiguration
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.util.*
import javax.sql.DataSource


@Configuration
open class ShardingSphereConfig {
    @Value("\${datasource.url}")
    private lateinit var url: String

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
    open fun datasource(): DataSource {
        val config = HikariConfig()
        config.driverClassName = driver
        config.jdbcUrl = url
        config.username = name
        config.password = password
        return HikariDataSource(config)
    }

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
        val friends = ShardingTableRuleConfiguration("friends", "im.friends")
        val user = ShardingTableRuleConfiguration("user", "im.user")
        val groups = ShardingTableRuleConfiguration("group_user", "im.group_user")
        val group_info = ShardingTableRuleConfiguration("group_info", "im.group_info")

        group_info.databaseShardingStrategy = StandardShardingStrategyConfiguration("id", "db-direct")
        group_info.tableShardingStrategy = StandardShardingStrategyConfiguration("id", "table-direct")
        groups.databaseShardingStrategy = StandardShardingStrategyConfiguration("id", "db-direct")
        groups.tableShardingStrategy = StandardShardingStrategyConfiguration("id", "table-direct")

        val msg = ShardingTableRuleConfiguration("msg", "im\${0..2}.msg\${0..2}")
        val offset = ShardingTableRuleConfiguration("msg_offset", "im\${0..2}.msg_offset\${0..2}")
        msg.databaseShardingStrategy = HintShardingStrategyConfiguration("force-db")
        msg.tableShardingStrategy = HintShardingStrategyConfiguration("force-table")

        offset.databaseShardingStrategy = HintShardingStrategyConfiguration("force-db")
        offset.tableShardingStrategy = HintShardingStrategyConfiguration("force-table")

        val normal_rules = ShardingRuleConfiguration()
        val rule_config = ShardingRuleConfiguration()
        normal_rules.tables.add(friends)
        normal_rules.tables.add(user)
        normal_rules.tables.add(groups)
        normal_rules.tables.add(group_info)
        normal_rules.bindingTableGroups.add("group_info,group_user")

        rule_config.tables.add(msg)
        rule_config.tables.add(offset)
        rule_config.shardingAlgorithms["force-db"] = ShardingSphereAlgorithmConfiguration("FORCE", Properties())
        rule_config.shardingAlgorithms["force-table"] = ShardingSphereAlgorithmConfiguration("FORCE", Properties())


        /*val dbProperty = Properties()
        dbProperty.setProperty("algorithm-expression", "im")
        val tableProps = Properties()
        tableProps.setProperty("algorithm-expression", "groups")*/
        normal_rules.shardingAlgorithms["db-direct"] = ShardingSphereAlgorithmConfiguration("DIRECT", Properties())
        normal_rules.shardingAlgorithms["table-direct"] = ShardingSphereAlgorithmConfiguration("DIRECT", Properties())


        val map = mapOf("im0" to datasource1(), "im1" to datasource2(), "im2" to datasource3(), "im" to datasource())
        val sourceProperty = Properties()
        sourceProperty.put("sql-show", true)
        return ShardingSphereDataSourceFactory.createDataSource(map, listOf(rule_config, normal_rules), sourceProperty)
    }
}