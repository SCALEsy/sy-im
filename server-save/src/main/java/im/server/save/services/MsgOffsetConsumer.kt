package im.server.save.services

import common.beans.MsgOffset
import common.server.MsgOffsetRepository
import im.server.save.task.OffsetTask
import im.server.save.task.SaveTaskDispatcher
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener
import org.apache.rocketmq.spring.core.RocketMQListener
import org.apache.shardingsphere.infra.hint.HintManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Component
@RocketMQMessageListener(consumerGroup = "\${server.name}-reached", topic = "\${im.server.msg-reached.topic}")
open class MsgOffsetConsumer : RocketMQListener<MsgOffset> {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    /*private val buffer = CacheBuilder.newBuilder().initialCapacity(1000)
        .maximumSize(10000)
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build<Long, Msg>()*/

    @Autowired
    private lateinit var saver: MsgOffsetRepository

    @Autowired
    private lateinit var dispatcher: SaveTaskDispatcher

    @PostConstruct
    fun init() {
        dispatcher.setOffsetTask(OffsetTask { list ->
            list.forEach { offset ->
                try {
                    save(offset)
                } catch (e: Exception) {
                    logger.error(ExceptionUtils.getStackTrace(e))
                }
            }
        })
    }

    @Transactional(rollbackFor = [Exception::class])
    open fun save(offset: MsgOffset) {
        val instance = HintManager.getInstance()
        try {
            instance.addDatabaseShardingValue("msg_offset", offset.DBValue())
            instance.addTableShardingValue("msg_offset", offset.tableValue())
            val saved = saver.load_single(offset.user_id, offset.dialog_id)
            if (saved != null) {
                saver.replaceMsgId(offset.msg_id, offset.user_id, offset.dialog_id)
            } else {
                saver.save(offset)
            }
        } catch (e: Exception) {
            throw e
        } finally {
            instance.close()
        }
    }

    override fun onMessage(msg: MsgOffset) {
        dispatcher.dispatch(msg)
    }


}