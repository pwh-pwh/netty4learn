package netty.king.pushmsg.client

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory
import io.netty.handler.codec.http.websocketx.WebSocketVersion
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.net.URI

/**
 * @author coderpwh
 * @date 2022-05-26 10:43
 * @version 1.0.0 v
 */
class NettyPushMsgClient {
    companion object {
        val URL = System.getProperty("url", "ws://localhost:8080/pushmsg")
    }

    fun run() {
        val uri = URI(URL)
        val scheme = uri.scheme ?: "ws"
        val host = uri.host ?: "localhost"
        val port: Int
        if (uri.port == -1) {
            if ("ws".equals(scheme)) {
                port = 80
            } else if ("wss".equals(scheme)) {
                port = 443
            } else {
                port = -1
            }
        } else {
            port = uri.port
        }

        if (!"ws".equals(scheme) && "wss".equals(scheme)) {
            println("Only WS(S) is supported.")
            return
        }
        val ssl = "wss".equals(scheme)
        val sslCtx: SslContext?
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build()
        } else {
            sslCtx = null;
        }
        var group = NioEventLoopGroup()

        try {
            val handler = NettyPushMsgClientHandler(
                WebSocketClientHandshakerFactory.newHandshaker(
                    uri, WebSocketVersion.V13, null, true, DefaultHttpHeaders()
                )
            )
            var b = Bootstrap()
            b.group(group)
                .channel(NioSocketChannel::class.java)
                .handler(
                    object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            if (sslCtx != null) {
                                ch.pipeline().addLast(sslCtx.newHandler(ch.alloc(), host, port))
                            }
                            ch.pipeline()
                                .addLast(HttpClientCodec())
                                .addLast(HttpObjectAggregator(1024 * 32))
                                .addLast(WebSocketClientCompressionHandler.INSTANCE)
                                .addLast(handler)
                        }
                    }
                )
            val ch = b.connect(uri.host,uri.port).sync().channel()
            handler.handShakeFuture.sync()
            var console = BufferedReader(
                InputStreamReader(System.`in`)
            )
            while (true) {
                var msg = console.readLine()
                if (msg == null) {
                    break
                } else if ("bye".equals(msg.lowercase())) {
                    ch.writeAndFlush(CloseWebSocketFrame())
                    ch.closeFuture().sync()
                } else if ("ping".equals(msg.lowercase())) {
                    var frame = PingWebSocketFrame(
                        Unpooled.wrappedBuffer(arrayOf<Byte>(8, 1, 8, 1).toByteArray())
                    )
                    ch.writeAndFlush(frame)
                } else {
                    var frame = TextWebSocketFrame(msg)
                    ch.writeAndFlush(frame)
                }
            }
        } finally {
            group.shutdownGracefully()
        }

    }

}

fun main() {
    NettyPushMsgClient().run()
}