package netty.king.pushmsg.server

import io.netty.channel.Channel
import io.netty.channel.ChannelId
import io.netty.channel.group.DefaultChannelGroup
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.util.concurrent.GlobalEventExecutor
import java.util.concurrent.ConcurrentHashMap

/**
 * @author coderpwh
 * @date 2022-05-26 10:44
 * @version 1.0.0 v
 */
class NettyPushMsgChannelSupervise {
    companion object {
        val GlobalGroup = DefaultChannelGroup(GlobalEventExecutor.INSTANCE)
        val ChannelMap = ConcurrentHashMap<String, ChannelId>()
        fun addChannel(channel: Channel) {
            GlobalGroup.add(channel)
            ChannelMap.put(channel.id().asShortText(),channel.id())
        }
        fun removeChannel(channel: Channel) {
            GlobalGroup.remove(channel)
            ChannelMap.remove(channel.id().asShortText())
        }

        fun findChannel(id:String) = GlobalGroup.find(ChannelMap.get(id))

        fun sendToAll(tws:TextWebSocketFrame) = GlobalGroup.writeAndFlush(tws)

    }
}