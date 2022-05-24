package netty.king.DiscardNetty.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

/**
 * @author coderpwh
 * @date 2022-05-24 16:24
 * @version 1.0.0 v
 */
class DiscardNettyServer(val port:Int = System.getProperty("port","8686").toInt()) {
    fun run() {
        var boosGroup = NioEventLoopGroup()
        var workerGroup = NioEventLoopGroup()
        try {
            var sbs = ServerBootstrap()
            sbs.group(boosGroup,workerGroup)
                .handler(LoggingHandler(LogLevel.INFO))
                .channel(NioServerSocketChannel::class.java)
                .childHandler(
                    object :ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            ch.pipeline().addLast(
                                DiscardNettyHandler()
                            )
                        }
                    }
                )
            var future = sbs.bind(port).sync()
            future.channel().closeFuture().sync()

        } finally {
            boosGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }

    }
}

fun main(args: Array<String>) {
    DiscardNettyServer().run()
}