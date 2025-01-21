package common.server.shardingspere

import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingAlgorithm
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingValue
import java.util.*

class ForceHintAlgorithm : HintShardingAlgorithm<Int> {

    //private val logger = LoggerFactory.getLogger(this.javaClass)

    private lateinit var properties: Properties
    override fun doSharding(
        coll: MutableCollection<String>,
        hintValue: HintShardingValue<Int>
    ): MutableCollection<String> {
        val res = mutableListOf<String>()
        for ((i, name) in coll.withIndex()) {

            for (v in hintValue.values) {
                val index = v % 3
                if (i == index) {
                    res.add(name)
                }
            }
        }

        return res
    }

    override fun init(p0: Properties) {
        properties = p0
    }

    override fun getProps(): Properties {
        return properties
    }

    override fun getType(): String {
        return "FORCE"
    }
}