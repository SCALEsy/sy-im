package im.server.logic.chains

import common.beans.Msg

open class MsgFilterChain(open val filter: Filter, var next: MsgFilterChain? = null) : MsgChain {
    override fun doFilter(msg: MsgWrapper) {
        next?.also {
            filter.doHandler(msg, it)
        }
    }
}

class MsgFilterChainOut(private val runnable: MsgRunnable) : MsgFilterChain(End()) {
    override fun doFilter(msg: MsgWrapper) {
        runnable.run(msg)
    }


}