package netty.king.pushmsg.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel

/**
 * @author coderpwh
 * @date 2022-05-26 10:43
 * @version 1.0.0 v
 */
class NettyPushMsgServer(val port:Int = System.getProperty("port","8080").toInt()) {

    fun run() {
        var boss = NioEventLoopGroup()
        var worker = NioEventLoopGroup()
        var serverBootstrap = ServerBootstrap()
        try {
            serverBootstrap.group(boss,worker)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(NettyPushMsgChannelInitializer())

            var future = serverBootstrap.bind(port).sync()
            future.channel().closeFuture().sync()
        } finally {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }

    }
}

fun main() {
    NettyPushMsgServer().run()
}