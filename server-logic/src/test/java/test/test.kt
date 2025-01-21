package im.server.logic.chains

import common.beans.AckState
import common.beans.Msg


fun main() {
    val pipeline = DefaultChainPipeline(MsgRunnable { msg ->
        println("i am end")
    })

    pipeline
            .register(Filter { msg, chain ->
                println("i am 1 ${msg.server_id}")
                chain.doFilter(msg)
            }).register(Filter { msg, chain ->
                println("i am 2 ${msg.server_id}")
                chain.doFilter(msg)
            })
            .register(Filter { msg, chain ->
                println("i am 3 ${msg.server_id}")
                chain.doFilter(msg)
            })
            .register(Filter { msg, chain ->
                println("i am 4 ${msg.server_id}")
                chain.doFilter(msg)
            })

    pipeline.run(Msg.ack(1, AckState.ClientSend, 1), "zxc", "xzc")

}