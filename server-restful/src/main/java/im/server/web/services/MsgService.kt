package im.server.web.services


import common.beans.WebResponse
import common.server.MsgRepository
import org.apache.shardingsphere.infra.hint.HintManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class MsgService {
    @Autowired
    private lateinit var msgRepository: MsgRepository

    fun unread(dialog_id: String, page: Int, size: Int, id: Long? = null): WebResponse<Any> {

        val instance = HintManager.getInstance()
        try {
            instance.addDatabaseShardingValue("msg", dialog_id.hashCode())
            instance.addTableShardingValue("msg", dialog_id.hashCode())
            val page = PageRequest.of(page - 1, size)
            val start = id ?: 0
            val msg = msgRepository.unread(dialog_id, start, page)
            return WebResponse.ok(msg.content, msg.totalElements)
        } catch (e: Exception) {
            throw e
        } finally {
            instance.close()
        }
    }

}