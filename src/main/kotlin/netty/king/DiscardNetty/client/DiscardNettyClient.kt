package netty.king.DiscardNetty.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel

/**
 * @author coderpwh
 * @date 2022-05-24 16:52
 * @version 1.0.0 v
 */
class DiscardNettyClient(
    val host:String = System.getProperty("host","localhost"),
    val port:Int = System.getProperty("port","8686").toInt()) {
    companion object {
        val SIZE = System.getProperty("size","256").toInt()
    }

    fun run() {
        var group = NioEventLoopGroup()
        try {
            var bootstrap = Bootstrap()
            bootstrap
                .group(group)
                .channel(NioSocketChannel::class.java)
                .handler(
                    object :ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            ch.pipeline().addLast(
                                DiscardNettyClientHandler()
                            )
                        }

                    }
                )
            var future = bootstrap.connect(host, port).sync()
            future.channel().closeFuture().sync()


        } finally {
            group.shutdownGracefully()
        }
    }

}

fun main(args: Array<String>) {
    DiscardNettyClient().run()
}