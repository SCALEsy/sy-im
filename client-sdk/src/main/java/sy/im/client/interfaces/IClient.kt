package sy.im.client.interfaces

import common.beans.ImState
import sy.im.client.ClientConfig

interface IClient {
    val config: ClientConfig
    fun connect()
    fun set_state(state: ImState)
    fun setRetry(num: Int)
    //fun getConfig(): ClientConfig
}