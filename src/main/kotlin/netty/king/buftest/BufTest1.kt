package netty.king.buftest

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.ByteBufUtil

/**
 * @author coderpwh
 * @date 2022-05-25 11:33
 * @version 1.0.0 v
 */

fun test1() {
    var byteBuf = ByteBufAllocator.DEFAULT.buffer(100)
    if (byteBuf.hasArray()) {
        var array = byteBuf.array()
        var offset = byteBuf.arrayOffset()+byteBuf.readerIndex()
        var length = byteBuf.readableBytes()
        
    }
}