package common.netty

import io.netty.channel.ChannelId
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import java.nio.channels.SocketChannel

class CustomNioSocketChannel : NioSocketChannel {

    constructor(serverSocketChannel: NioServerSocketChannel, ch: SocketChannel) : super(serverSocketChannel, ch) {

    }

    protected override fun newId(): ChannelId {
        return CustomChannelId.newInstance()
    }
}

