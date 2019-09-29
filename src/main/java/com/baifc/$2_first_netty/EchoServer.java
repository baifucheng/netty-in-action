package com.baifc.$2_first_netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * projectName: netty-in-action
 * packageName: com.baifc.$2_first_netty
 * Created: 2019/9/29.
 * Auther: baifc
 * Description: echoServer端引导类：
 *         绑定到服务器将在其上监听并接收传入请求的端口
 *         配置Channel，将有关入站消息，通知给EchoServerHandler实例
 */
public class EchoServer {

    private final int port;

    EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("error");
        }

        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        final EchoServerHandler handler = new EchoServerHandler();

        // 创建EventLoopGroup实例，以进行事件的处理，如接收新连接，或读写数据
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建ServerBootstrap引导和绑定服务器
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(group)
                    // 指定channel为NIO传输channel
                    .channel(NioServerSocketChannel.class)
                    // 使用指定的端口，设置套接字地址
                    .localAddress(new InetSocketAddress(port))
                    // 添加一个EchoServerHandler实例到子Channel的ChannelPipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        /*
                            这里用到了ChannelInitializer类，这个是关键
                            当一个新的连接建立时，一个新的子Channel将会被创建
                            而ChannelInitializer会把EchoServerHandler实例添加到这个子Channel的pipeline中
                         */
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            // EchoServerHandler被标注为Sharable，所以总是可以取到相同的实例
                            channel.pipeline().addLast(handler);
                        }
                    });

            // 异步的绑定服务器，调用sync方法阻塞，直到绑定完成
            // 对sync的调用，将会导致当前Thread阻塞，一直阻塞到绑定操作完成为止
            ChannelFuture future = bootstrap.bind().sync();

            // 获取channel的closeFuture，并且阻塞当前线程，直到它完成
            future.channel().closeFuture().sync();
        } finally {
            // 关闭EventLoopGroup，并关闭所有资源，包括所有被创建的线程
            group.shutdownGracefully().sync();
        }
    }
}
