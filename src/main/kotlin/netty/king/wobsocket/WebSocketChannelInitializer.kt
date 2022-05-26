package netty.king.wobsocket

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.stream.ChunkedWriteHandler

/**
 * @author coderpwh
 * @date 2022-05-26 9:35
 * @version 1.0.0 v
 */
class WebSocketChannelInitializer:ChannelInitializer<NioSocketChannel>() {
    override fun initChannel(ch: NioSocketChannel) {
        ch.pipeline()
            .addLast(HttpServerCodec())
            .addLast(ChunkedWriteHandler())
            .addLast(HttpObjectAggregator(1024*32))
            .addLast(WebSocketServerProtocolHandler("/websocket"))
            .addLast(WebSocketServerHandler())
    }

}