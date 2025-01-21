package common.netty

import common.snowflake.SnowFlakeSeq
import io.netty.channel.ChannelId

class CustomChannelId private constructor() : ChannelId {
    private var value: Long = snowFlakeSeq.nextId()

    override fun asShortText(): String {
        return value.toString()
    }

    override fun asLongText(): String {
        return value.toString()
    }

    override fun compareTo(o: ChannelId): Int {
        return if (o is CustomChannelId) this.asLongText().compareTo(o.asLongText()) else -1
    }

    companion object {
        private const val serialVersionUID = 1096867517162468422L
        private val snowFlakeSeq = SnowFlakeSeq()
        fun newInstance(): CustomChannelId {
            return CustomChannelId()
        }


        fun fromLongText(text: String): CustomChannelId {
            val id = CustomChannelId()
            id.value = text.toLong()
            return id
        }
    }
}