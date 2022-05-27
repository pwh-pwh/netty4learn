package netty.king.pushmsg.server

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory
import io.netty.util.CharsetUtil
import java.util.Date
import java.util.logging.Logger


/**
 * @author coderpwh
 * @date 2022-05-26 10:44
 * @version 1.0.0 v
 */
class NettyPushMsgServerHandler: SimpleChannelInboundHandler<Any>() {

    private lateinit var handshaker: WebSocketServerHandshaker

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Any?) {
        if (msg is FullHttpRequest) {
            handleHttpRequest(ctx,msg)
        } else if (msg is WebSocketFrame) {
            handlerWebSocketFrame(ctx,msg)
        } else {

        }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        NettyPushMsgChannelSupervise.addChannel(ctx.channel())
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        NettyPushMsgChannelSupervise.removeChannel(ctx.channel())
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    private fun handlerWebSocketFrame(
        ctx: ChannelHandlerContext,
        frame: WebSocketFrame
    ) {
        if (frame is CloseWebSocketFrame) {
            handshaker.close(ctx.channel(),frame.retain())
            return
        }
        if (frame is PingWebSocketFrame) {
            ctx.channel().write(
                PongWebSocketFrame(frame.content().retain())
            )
            return
        }
        if (frame !is TextWebSocketFrame) {
            throw UnsupportedOperationException("${frame.javaClass.name} frame types not supported")
        }

        var request = (frame as TextWebSocketFrame).text()
        println("Server received: ${request}")
        var tws = TextWebSocketFrame("${request} client id(${ctx.channel().id()}) request, " +
                "server pushes at ${Date().toString()}.")
        NettyPushMsgChannelSupervise.sendToAll(tws)

    }

    fun handleHttpRequest(ctx: ChannelHandlerContext,req: FullHttpRequest) {
        if(!req.decoderResult().isSuccess||!"websocket".equals(req.headers().get("Upgrade"))) {
            sendHttpResponse(ctx,req, DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST))
            return
        }
        val wsFactory = WebSocketServerHandshakerFactory(
            "ws://localhost:8080/pushmsg",null,false
        )
        handshaker = wsFactory.newHandshaker(req)
        if (handshaker==null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel())
        } else {
            handshaker.handshake(ctx.channel(),req)
        }


    }

    companion object {
        fun sendHttpResponse(
            ctx: ChannelHandlerContext,
            req: FullHttpRequest,
            res: DefaultFullHttpResponse
        ) {
            if(res.status().code()!=200) {
                var buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8)
                res.content().writeBytes(buf)
                buf.release()
            }
            var f = ctx.channel().writeAndFlush(res)
            if (res.status().code()!=200||!isKeepAlive(req)) {
                f.addListener(ChannelFutureListener.CLOSE)
            }
        }
        fun isKeepAlive(req: FullHttpRequest):Boolean{
            return req.headers().get("Connection").equals("keep-alive")
        }

    }


}