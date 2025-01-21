package im.server.logic.caches

import common.snowflake.SnowFlakeMaker

interface MsgIdGenerator {
    fun generator(size: Int): List<Long>
    fun generator(dialog_id: String): Long
}

class SnowFlakeMsgIdGenerator(val center: Long, val machine: Long) : MsgIdGenerator {
    val idGenerator = SnowFlakeMaker(center, machine)


    override fun generator(size: Int): List<Long> {
        val list = (1..size).map { x ->
            idGenerator.nextId()
        }
        return list
    }

    override fun generator(dialog_id: String): Long {
        return idGenerator.nextId()
    }

    companion object {
        fun instance(center: Long, machine: Long): SnowFlakeMsgIdGenerator {
            return SnowFlakeMsgIdGenerator(center, machine)
        }
    }
}