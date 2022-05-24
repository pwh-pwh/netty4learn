package netty.king.echo.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

/**
 * @author coderpwh
 * @date 2022-05-24 17:46
 * @version 1.0.0 v
 */
class EchoNettyServer(val port: Int = System.getProperty("port","8686").toInt()) {
    fun run() {
        var boosGroup = NioEventLoopGroup()
        var workerGroup = NioEventLoopGroup()
        try {
            var sbs = ServerBootstrap()
            sbs.group(boosGroup,workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .handler(LoggingHandler(LogLevel.INFO))
                .childHandler(
                    object :ChannelInitializer<SocketChannel>(){
                        override fun initChannel(ch: SocketChannel) {
                            ch.pipeline()
                                .addLast(StringDecoder())
                                .addLast(StringEncoder())
                                .addLast(EchoNettyServerHandler())
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
    EchoNettyServer().run()
}