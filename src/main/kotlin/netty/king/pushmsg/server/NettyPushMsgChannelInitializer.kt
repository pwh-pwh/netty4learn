package netty.king.pushmsg.server

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.logging.LoggingHandler
import io.netty.handler.stream.ChunkedWriteHandler

/**
 * @author coderpwh
 * @date 2022-05-26 10:44
 * @version 1.0.0 v
 */
class NettyPushMsgChannelInitializer : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        ch.pipeline()
            .addLast(LoggingHandler("DEBUG"))
            .addLast(HttpServerCodec())
            .addLast(ChunkedWriteHandler())
            .addLast(HttpObjectAggregator(1024 * 32))
            .addLast(NettyPushMsgServerHandler())
    }
}