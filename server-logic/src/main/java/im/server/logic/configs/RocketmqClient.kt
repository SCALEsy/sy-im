package im.server.logic.configs

import com.lmax.disruptor.BlockingWaitStrategy
import com.lmax.disruptor.EventFactory
import com.lmax.disruptor.WorkHandler
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.dsl.ProducerType
import im.server.logic.disruptors.MqHolder
import org.apache.commons.lang3.concurrent.BasicThreadFactory
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.rocketmq.spring.core.RocketMQTemplate
import org.slf4j.LoggerFactory

class RocketmqClient(private val template: RocketMQTemplate) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val disruptor = Disruptor<MqHolder>(
        EventFactory {
            MqHolder()
        },
        1024 * 8,
        BasicThreadFactory.Builder().namingPattern("disruptor-%d").build(),
        ProducerType.MULTI,
        BlockingWaitStrategy()
    )

    init {
        val handers = (0 until 8).map {
            WorkHandler<MqHolder> { holder ->
                try {
                    val exchange = holder.exchange
                    val server_id = holder.server_id
                    val obj = holder.obj
                    template.convertAndSend("$exchange:$server_id", obj)
                } catch (e: Exception) {
                    logger.error(ExceptionUtils.getStackTrace(e))
                }
            }
        }.toTypedArray()
        disruptor.handleEventsWithWorkerPool(*handers)
        disruptor.start()
    }

    fun send(exchange: String, serverId: String, obj: Any) {
        disruptor.publishEvent { holder, seq ->
            holder.exchange = exchange
            holder.server_id = serverId
            holder.obj = obj
        }

    }

}
