package netty.king.wobsocket

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.util.concurrent.GlobalEventExecutor

/**
 * @author coderpwh
 * @date 2022-05-26 9:17
 * @version 1.0.0 v
 */
class WebSocketServerHandler: SimpleChannelInboundHandler<TextWebSocketFrame>() {
    //记录所有客户端channel
    companion object {
        val clients = DefaultChannelGroup(GlobalEventExecutor.INSTANCE)
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: TextWebSocketFrame) {
        var text = msg.text()
        println("receive msg:${text}")
        clients.writeAndFlush(TextWebSocketFrame("server has receive msg:${text}"))
    }

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        println("channel has be add. channel id is ${ctx.channel().id().asLongText()}")
        clients.add(ctx.channel())
    }

    override fun handlerRemoved(ctx: ChannelHandlerContext) {
        println("channel has be remove. channel id is ${ctx.channel().id().asLongText()}")
        clients.remove(ctx.channel())
    }
}