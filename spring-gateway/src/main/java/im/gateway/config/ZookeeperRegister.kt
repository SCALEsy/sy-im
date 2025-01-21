package im.gateway.config

import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch
import javax.annotation.PreDestroy

@Component
class ZookeeperRegister {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Value("\${im.server.name}")
    private lateinit var server_name: String

    @Value("\${im.server.zookeeper.url}")
    private lateinit var zk_url: String

    @Value("\${im.server.ip}")
    private lateinit var bind_ip: String

    @Value("\${im.server.port}")
    private lateinit var port: String


    private val basePath = "/gateway"

    private val session_timeout = 10000


    private val countdown = CountDownLatch(1)

    private lateinit var zk: ZooKeeper

    //@PostConstruct
    fun register() {

        zk = ZooKeeper(zk_url, session_timeout, Watcher { e ->
            if (e.state == Watcher.Event.KeeperState.SyncConnected) {
                countdown.countDown()
            }
        })
        countdown.await()
        val exists = zk.exists(basePath, false)
        if (exists == null) {
            zk.create(basePath, basePath.toByteArray(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
        }
        val path = basePath + "/" + server_name

        //val inetAddress = InetAddress.getLocalHost().hostAddress
        zk.create(
            path,
            "${bind_ip}:$port".toByteArray(),
            ZooDefs.Ids.READ_ACL_UNSAFE,
            CreateMode.EPHEMERAL_SEQUENTIAL
        )
        logger.info("register zk ok")

    }


    @PreDestroy
    fun close() {
        logger.info("close zk")
        zk.close()
    }
}