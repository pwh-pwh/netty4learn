package netty.king.DiscardNetty.client

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.SimpleChannelInboundHandler

/**
 * @author coderpwh
 * @date 2022-05-24 16:52
 * @version 1.0.0 v
 */
class DiscardNettyClientHandler:SimpleChannelInboundHandler<Object>() {
    private lateinit var content:ByteBuf
    private lateinit var ctx:ChannelHandlerContext
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        println("Client channel active...")
        this.ctx = ctx
        content = ctx.alloc().directBuffer(DiscardNettyClient.SIZE)
            .writeZero(DiscardNettyClient.SIZE)
        generateTraffic()
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        content.release()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Object) {

    }
    private fun generateTraffic() {
        println("traffic ...")
        ctx.writeAndFlush(content.retainedDuplicate())
            .addListener(trafficGenerator)
    }

    private val trafficGenerator = ChannelFutureListener {
        if (it.isSuccess) {
            generateTraffic()
        } else {
            it.cause().printStackTrace()
            it.channel().close()
        }
    }

}