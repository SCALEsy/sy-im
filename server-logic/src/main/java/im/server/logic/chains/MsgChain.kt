package im.server.logic.chains

import common.beans.Msg

data class MsgWrapper(val msg: Msg, val server_id: String, val channel_id: String)

fun interface Filter {
    fun doHandler(msg: MsgWrapper, chain: MsgChain)
}

interface MsgChain {


    fun doFilter(msg: MsgWrapper)
}


class Head : Filter {

    override fun doHandler(msg: MsgWrapper, chain: MsgChain) {
        println("i am head")
        chain.doFilter(msg)
    }
}

class End : Filter {

    override fun doHandler(msg: MsgWrapper, chain: MsgChain) {
        println("i am end,never call")
    }
}


interface MsgChainPipeline {

    fun register(filter: Filter): MsgChainPipeline


    fun run(msg: Msg, server_id: String, channel_id: String)
}


fun interface MsgRunnable {

    fun run(wrapper: MsgWrapper)
}