package netty.king.echo.client

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * @author coderpwh
 * @date 2022-05-24 17:47
 * @version 1.0.0 v
 */
class EchoNettyClientHandler :ChannelInboundHandlerAdapter(){
    var firstMessage: ByteBuf
    init {
        firstMessage = Unpooled.buffer()
        for (i in 0..firstMessage.capacity()) {
            firstMessage.writeByte(i)
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.writeAndFlush(firstMessage)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        println("server return data:${msg}")
        ctx.channel().close()
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}