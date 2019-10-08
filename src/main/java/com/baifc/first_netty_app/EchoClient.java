package com.baifc.first_netty_app;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * projectName: netty-in-action
 * packageName: com.baifc.$2_first_netty
 * Created: 2019/10/8.
 * Auther: baifc
 * Description: echo客户端引导类
 */
public class EchoClient {

    private final String host;
    private final int port;

    EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void start() throws Exception {
        final EchoClientHandler handler = new EchoClientHandler();

        // 创建EventLoopGroup实例，以进行事件的处理，这些事件包括创建新的连接，处理入站和出站的数据等
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    // 设置服务器的ip和端口
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        // 当连接被建立时，一个EchoClientHandler实例将被安装到该Channel的pipeline中
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(handler);
                        }
                    });

            // 连接到远程节点，阻塞等待，直到连接完成
            ChannelFuture future = bootstrap.connect().sync();
            System.out.println("-----------bind end-------------");

            // 阻塞，直到channel关闭
            future.channel().closeFuture().sync();
            System.out.println("-----------closeFuture end-------------");
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("参数错误！");
            return;
        }
        new EchoClient(args[0], Integer.parseInt(args[1])).start();
    }
}
