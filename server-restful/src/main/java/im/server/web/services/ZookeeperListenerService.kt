package im.server.web.services

import common.beans.GateWayInfo
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooKeeper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.concurrent.CountDownLatch
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service
class ZookeeperListenerService {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private lateinit var zk: ZooKeeper

    @Value("\${im.server.zookeeper.url}")
    private lateinit var zk_url: String

    private val base_path: String = "/gateway"

    private val session_timeout = 10000

    private val countdown = CountDownLatch(1)

    private var services = emptyList<GateWayInfo>()

    @PostConstruct
    fun listen() {
        zk = ZooKeeper(zk_url, session_timeout, Watcher { e ->
            if (e.state == Watcher.Event.KeeperState.SyncConnected) {
                countdown.countDown()
            }
            if (e.type == Watcher.Event.EventType.NodeChildrenChanged && e.path == base_path) {
                loadZkInfo()
            }
        })

        countdown.await()
        loadZkInfo()
        logger.info("load zk service info")
    }

    @PreDestroy
    fun close() {
        zk.close()
        logger.info("close zk")
    }

    private fun loadZkInfo() {
        val children = zk.getChildren(base_path, true)
        services = children.map { node ->

            val bytes = zk.getData(base_path + "/" + node, false, null)
            val str = String(bytes).split(":", limit = 2)
            GateWayInfo(node, str[0], str[1].toInt())
        }

    }

    fun loadAll(): List<GateWayInfo> {
        return services
    }

    fun loadBalance(user_id: Int): GateWayInfo? {
        if (services.size <= 0) {
            return null
        }
        //val rand = Random(System.currentTimeMillis()).nextInt()
        val index = user_id.mod(services.size)
        return services[index]
    }
}