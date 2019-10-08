package com.baifc.first_netty_app;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * projectName: netty-in-action
 * packageName: com.baifc.$2_first_netty
 * Created: 2019/9/29.
 * Auther: baifc
 * Description: EchoServerHandler 会响应传入的消息，所以它需要实现ChannelInboundHandler接口，用来定义响应入站事件的方法
 *      这里只用到了少量的方法，所以只需要继承ChannelInboundHandlerAdapter就足够了，ChannelInboundHandlerAdapter提供了所以它需要实现ChannelInboundHandler的默认实现
 */
@Sharable   // 表示一个channelHandler可以被多个channel安全的共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 对于每个传入的消息都要调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 将接收到的信息打印至控制台上
        ByteBuf in = (ByteBuf) msg;
        System.out.println(" Server received: " + in.toString(CharsetUtil.UTF_8));

        // 将接收到的消息写给发送者，而不冲刷出站消息（TODO 这句话是什么意思？）
        ctx.write(in);
    }

    /**
     * 通知ChannelInboundHandler最后一次给channelRead()的调用，是这次批量读取的最后一条消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将消息冲刷到远程节点
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                // 关闭该Channel
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 打印异常
        cause.printStackTrace();
        // 关闭Channel
        ctx.close();
    }

}
