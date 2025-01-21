package im.server.save.services

import common.beans.Msg
import common.server.MsgBean
import common.server.MsgRepository
import common.server.insert
import common.server.toBean

import im.server.save.task.MsgTask
import im.server.save.task.SaveTaskDispatcher
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener
import org.apache.rocketmq.spring.core.RocketMQListener
import org.apache.shardingsphere.infra.hint.HintManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.annotation.PostConstruct

@Component
@RocketMQMessageListener(consumerGroup = "\${server.name}-msg", topic = "\${im.server.save.topic}")
open class MsgConsumer : RocketMQListener<Msg> {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var saver: MsgRepository

    @Autowired
    private lateinit var dispatcher: SaveTaskDispatcher


    @PostConstruct
    fun init() {
        dispatcher.setMsgTask(MsgTask { list ->
            list.forEach { msg ->
                try {
                    save(msg)
                } catch (e: Exception) {
                    logger.error(ExceptionUtils.getStackTrace(e))
                }
            }
        })
    }


    override fun onMessage(msg: Msg) {
        dispatcher.dispatch(msg)
    }


    @Transactional(rollbackFor = [Exception::class])
    open fun save(msg: Msg) {
        val instance = HintManager.getInstance()
        try {
            instance.addDatabaseShardingValue("msg", msg.DBValue())
            instance.addTableShardingValue("msg", msg.tableValue())
            val exests = saver.findById(msg.id)
            if (!exests.isPresent) {
                saver.insert(msg.toBean())
            } else {
                val bean = mergeById(exests.get(), msg.toBean())
                saver.update_state(bean.id, bean.state?.name, bean.time ?: Date())
            }
        } catch (e: Exception) {
            throw e
        } finally {
            instance.close()
        }
    }

    private fun mergeById(a: Msg, b: Msg): Msg {
        val sa = a.state?.num ?: 0
        val sb = b.state?.num ?: 0
        return if (sa > sb) {
            a
        } else {
            b
        }
    }

    private fun mergeById(a: MsgBean, b: MsgBean): MsgBean {
        val sa = a.state?.num ?: 0
        val sb = b.state?.num ?: 0
        return if (sa > sb) {
            a
        } else {
            b
        }
    }
}