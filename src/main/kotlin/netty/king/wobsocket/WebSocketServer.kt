package netty.king.wobsocket

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel

/**
 * @author coderpwh
 * @date 2022-05-26 9:17
 * @version 1.0.0 v
 */
class WebSocketServer(val port:Int = System.getProperty("port","8080").toInt()) {

    fun run() {
        var bossGroup = NioEventLoopGroup()
        var workerGroup = NioEventLoopGroup()
        try {
            var sbs = ServerBootstrap()
            sbs.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(
                    WebSocketChannelInitializer()
                )

            var future = sbs.bind(port).sync()
            future.channel().closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }

    }
}

fun main() {
    WebSocketServer().run()
}