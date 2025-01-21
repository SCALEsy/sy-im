package common.server.shardingspere

import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingAlgorithm
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingValue
import java.util.*

class DirectShardingAlgorithm : HintShardingAlgorithm<Int> {

    private var properties: Properties = Properties()
    override fun init(p0: Properties) {
        properties = p0
    }

    override fun getProps(): Properties {
        return properties
    }

    override fun doSharding(p0: MutableCollection<String>, p1: HintShardingValue<Int>): Collection<String> {
        return p0
    }


    override fun getType(): String {
        return "DIRECT"
    }

}