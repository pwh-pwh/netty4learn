package netty.king.pushmsg.client

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException
import java.util.Objects

/**
 * @author coderpwh
 * @date 2022-05-26 10:43
 * @version 1.0.0 v
 */
class NettyPushMsgClientHandler(val handShaker:WebSocketClientHandshaker): SimpleChannelInboundHandler<Any>() {
    lateinit var handShakeFuture:ChannelPromise
    override fun handlerAdded(ctx: ChannelHandlerContext) {
        handShakeFuture = ctx.newPromise()
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        handShaker.handshake(ctx.channel()).sync()
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        println("WebSocket Client disconnected!")
    }

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: Any?) {
        var ch = ctx!!.channel()
        if (!handShaker.isHandshakeComplete) {
            try {
                handShaker.finishHandshake(ch,msg as FullHttpResponse)
                println("WebSocket Client connected!")
                handShakeFuture.setSuccess()
            } catch (e: WebSocketHandshakeException) {
                println("WebSocket Client failed to connect")
                handShakeFuture.setFailure(e)
            }
            return
        }
        if (msg is FullHttpResponse) {
            var response = msg
            throw IllegalStateException("Unexpected FullHttpResponse (getStatus=${response.status()},content=${response.content()}")
        }
        var frame = msg as WebSocketFrame
        if (frame is TextWebSocketFrame) {
            println("WebSocket Client received message: ${frame.text()}")
        } else if (frame is PongWebSocketFrame) {
            println("WebSocket Client received pong")
        } else if (frame is CloseWebSocketFrame) {
            println("WebSocket Client will be close")
            ch.close()
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        if (!handShakeFuture.isDone) {
            handShakeFuture.setFailure(cause)
        }
        ctx.close()
    }
}