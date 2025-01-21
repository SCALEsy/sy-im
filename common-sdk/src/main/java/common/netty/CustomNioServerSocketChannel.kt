package common.netty

import io.netty.channel.ChannelId
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.util.internal.SocketUtils
import io.netty.util.internal.logging.InternalLoggerFactory

class CustomNioServerSocketChannel : NioServerSocketChannel() {
    private val logger = InternalLoggerFactory.getInstance(this.javaClass)

    override fun newId(): ChannelId {
        return CustomChannelId.newInstance()
    }

    override fun doReadMessages(buf: MutableList<Any>?): Int {
        val ch = SocketUtils.accept(this.javaChannel())

        try {
            if (ch != null) {
                buf!!.add(CustomNioSocketChannel(this, ch))
                return 1
            }
        } catch (var6: Throwable) {
            logger.warn("Failed to create a new channel from an accepted socket.", var6)
            try {
                ch!!.close()
            } catch (var5: Throwable) {
                logger.warn("Failed to close a socket.", var5)
            }
        }

        return 0
    }
}

