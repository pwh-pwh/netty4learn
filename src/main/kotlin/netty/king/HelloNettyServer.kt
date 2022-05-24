package netty.king

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.util.ReferenceCountUtil

/**
 * @author coderpwh
 * @date 2022-05-24 15:45
 * @version 1.0.0 v
 */
class HelloNettyServer(val port:Int) {
    fun run() {
        val boosGroup = NioEventLoopGroup()
        val workerGroup = NioEventLoopGroup()
        try {
            val sbs = ServerBootstrap()
            sbs.group(boosGroup,workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .handler(LoggingHandler(LogLevel.INFO))
                .childHandler(
                    object :ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            ch.pipeline().addLast(
                                object :ChannelInboundHandlerAdapter() {
                                    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
                                        cause.printStackTrace()
                                        ctx.close()
                                    }
                                    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
                                        if (msg is ByteBuf) {
                                            try {
                                                while(msg.isReadable) {
                                                    print(msg.readByte().toChar())
                                                }
                                            } finally {
                                                ReferenceCountUtil.release(msg)
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                )
                .option(ChannelOption.SO_BACKLOG,128)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
            var f = sbs.bind(port).sync()
            f.channel().closeFuture().sync()
        } finally {
            workerGroup.shutdownGracefully()
            boosGroup.shutdownGracefully()
        }
    }
}