package im.server.logic.service

import common.beans.Msg
import common.server.MsgRpcService
import common.server.actions.RpcResponse
import im.server.logic.chains.MsgChainPipeline
import im.server.logic.service.pipelines.CommonService
import org.apache.dubbo.config.annotation.DubboService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.PostConstruct

//@MotanService

@DubboService
@Component
class MsgRpcServiceImpl : MsgRpcService {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var scheduler: ScheduledThreadPoolExecutor

    @Autowired
    private lateinit var executor: ThreadPoolExecutor

    @Autowired
    private lateinit var commonService: CommonService

    @Autowired
    private lateinit var pipeline: MsgChainPipeline

    private val submit_size = AtomicInteger(0)
    private val finish_size = AtomicInteger(0)
    private var last_complete = 0


    @PostConstruct
    fun init() {
        scheduler.scheduleAtFixedRate({
            val all = submit_size.get()
            val complete = finish_size.get()
            val undone = all - complete
            val done = complete - last_complete
            last_complete = complete

            val avg = done / 10

            val avg_cost = if (done != 0) {
                (10 * 1000) / done
            } else {
                0
            }

            logger.info(
                "state all:{}, finish:{}, undone:{}, done:{}, avg:{} avg_cost:{}",
                all,
                complete,
                undone,
                done,
                avg,
                avg_cost
            )
        }, 0, 10, TimeUnit.SECONDS)

        logger.info("init rpc service")
    }


    override fun handleMsg(msg: Msg, server_id: String, channel_id: String): RpcResponse {

        val ack = commonService.checkMsgAndGenAck1(msg)
        val response = RpcResponse.ack(ack)
        executor.submit {
            pipeline.run(msg, server_id, channel_id)
            finish_size.incrementAndGet()
        }

        submit_size.incrementAndGet()
        return response
    }
}