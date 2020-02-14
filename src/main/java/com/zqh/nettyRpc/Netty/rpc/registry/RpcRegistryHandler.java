package com.zqh.nettyRpc.Netty.rpc.registry;

import com.zqh.nettyRpc.Netty.rpc.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：zhengqh
 * @date 2020/2/14 14:27
 **/
public class RpcRegistryHandler extends ChannelInboundHandlerAdapter {

    // 客户端连接的时候  ，就会回调
    // 根据包名 将所有符合条件的class扫描 放入容器  如果是分布式就读取配置文件的信息（服务名+地址）
    // 给对应的class 起一个唯一的服务名  保存到容器中
    // 客户端连接之后   会获取 协议内容InvokerProtocol 对象 Object msg
    // 要去注册好的容器中找到对应的符合条件的服务
    // 通过远程调用获取provider提供的服务 得到返回结果

    private List<String> classNames  =new ArrayList<>();

    private Map<String,Object> registryMap = new ConcurrentHashMap<>();

    //构造函数 初始化扫描class
    public RpcRegistryHandler(){
        //简化版 直接将包名称 传入
        scannerClass("com.zqh.nettyRpc.Netty.rpc.provider");
        // 注册
        doRegistry();
    }


    private void doRegistry() {
       if(classNames.isEmpty()){return;}
        for (String className : classNames) {
            try {
                Class<?>  clazz = Class.forName(className);
                Class<?>  t=  clazz.getInterfaces()[0];//接口名称作为服务名
                String name  = t.getName();
                // 本来服务名是要存放 服务网络地址的 这里就直接使用实例=反射调用
                registryMap.put(name,clazz.newInstance());

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

  //正常来说  是读取配置文件
    private void scannerClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.","/"));
        File classPath =new File(url.getFile());
        for (File f:classPath.listFiles()) {
            if(f.isDirectory()){
                scannerClass(packageName+"."+f.getName());
            }else{
                classNames.add(packageName+"."+f.getName().replace(".class",""));
            }

        }
    }


    // 客户端连接之后   会获取 协议内容InvokerProtocol 对象 Object msg
    // 要去注册好的容器中找到对应的符合条件的服务
    // 通过远程调用获取provider提供的服务 得到返回结果
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //if(msg instanceof  InvokerProtocol){
            Object result=  new Object();
            InvokerProtocol  request = (InvokerProtocol) msg;
        //}

        if(registryMap.containsKey(request.getClassName())){
            Object clazz  =registryMap.get(request.getClassName());
            Method method = clazz.getClass().getMethod(request.getMethodName(),request.getParams());
            result = method.invoke(clazz,request.getValues());
        }
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    // 异常的时候 回调
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
          cause.printStackTrace();
          ctx.close();
    }
}
