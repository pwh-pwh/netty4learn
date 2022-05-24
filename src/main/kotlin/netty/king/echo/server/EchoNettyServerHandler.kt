package netty.king.echo.server

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * @author coderpwh
 * @date 2022-05-24 17:47
 * @version 1.0.0 v
 */
@Sharable
class EchoNettyServerHandler :ChannelInboundHandlerAdapter() {
    override fun channelActive(ctx: ChannelHandlerContext) {
        println("server channel active...")
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        println("server channel read....")
        println("data is $msg")
        var result = "server to client!"
        var buffer = Unpooled.buffer()
        buffer.writeBytes(result.toByteArray())
        ctx.channel().writeAndFlush(buffer)
        println("===")
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        println("server channel read complete.")
        ctx.flush()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.channel().close()
    }
}