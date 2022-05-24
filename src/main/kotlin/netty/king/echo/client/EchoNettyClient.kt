package netty.king.echo.client

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.util.CharsetUtil

/**
 * @author coderpwh
 * @date 2022-05-24 17:47
 * @version 1.0.0 v
 */
class EchoNettyClient(
    val host: String = System.getProperty("host","localhost"),
    val port: Int = System.getProperty("port","8686").toInt()
) {
    companion object {
        val SIZE = System.getProperty("size","256").toInt()
    }
    
    fun run() {
        var group = NioEventLoopGroup()
        try {
            var bs = Bootstrap()
            bs.group(group)
                .channel(NioSocketChannel::class.java)
                .handler(
                    object :ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            ch.pipeline()
                                .addLast(StringDecoder())
                                .addLast(StringEncoder())
                                .addLast(EchoNettyClientHandler())
                        }
                    }
                )

            var future = bs.connect(host, port).sync()
            var cli_msg = "Hello EchoNetty!"
            future.channel().flush()
            future.channel().writeAndFlush(cli_msg)
            Unpooled.copiedBuffer("hello world!", CharsetUtil.UTF_8)
            future.channel().closeFuture().sync()

        } finally {
            group.shutdownGracefully()
        }
        
        
        
    }

}

fun main(args: Array<String>) {
    EchoNettyClient().run()
}