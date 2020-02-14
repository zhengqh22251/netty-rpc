package com.zqh.nettyRpc.Netty.rpc.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


/**
 * @Author：zhengqh
 * @date 2020/2/14 14:04
 **/

// 发布服务 暴露服务
public class RpcRegistry {
    private int port;
    public RpcRegistry(int port){
        this.port= port;
    }

    //启动方法
    public void start(){
      // 之前使用ServerSocket 或者 ServerSocketChannel
        ServerBootstrap server = new ServerBootstrap();
        // 基于nio实现的  Selector 主线程
        // worker线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();// 类似与线程池 默认是cpu核心数的2倍
        EventLoopGroup workGroup = new NioEventLoopGroup();// 类似与线程池 默认是cpu核心数的2倍
        // 责任链模型
        server.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                       //在netty 所有的业务逻辑处理 都放在一个队列中
                       // 这个队列包含各种各样的处理逻辑 封装成一个队形 无锁化窜性任务队列 Pipeline

                     ChannelPipeline pipeline =  socketChannel.pipeline();
                     // 处理逻辑封装
                     //对于自定义协议 编解码  className .....
                     pipeline.addLast(
                             new LengthFieldBasedFrameDecoder
                                     (Integer.MAX_VALUE,0,4,0,4));

                     //自定义编码
                     pipeline.addLast(new LengthFieldPrepender(4));

                     // 实参处理
                     pipeline.addLast("encoder",new ObjectEncoder());
                     pipeline.addLast("decoder",
                         new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                     // 前面编解码完成对数据的解析
                     // 执行自己的逻辑
                     //1、注册  个每一个对象起一个名字  对外提供服务的名称
                     //2、服务位置的登记
                      pipeline.addLast(new RpcRegistryHandler());

                    }
                })
              .option(ChannelOption.SO_BACKLOG,128)
              .childOption(ChannelOption.SO_KEEPALIVE,true);

        // 启动 相当于死循环 在轮询
        try {

            ChannelFuture f=  server.bind(this.port).sync();
            System.out.println("服务启动====端口:"+port);
            // 同步状态
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new RpcRegistry(8080).start();
    }
}
