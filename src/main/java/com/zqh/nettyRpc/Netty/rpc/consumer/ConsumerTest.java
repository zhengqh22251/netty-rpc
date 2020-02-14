package com.zqh.nettyRpc.Netty.rpc.consumer;

import com.zqh.nettyRpc.Netty.rpc.api.IhelloService;
import com.zqh.nettyRpc.Netty.rpc.api.ItestService;
import com.zqh.nettyRpc.Netty.rpc.consumer.proxy.RpcProxy;

/**
 * @Author：zhengqh
 * @date 2020/2/14 15:49
 **/
public class ConsumerTest {

    public static void main(String[] args) {
        IhelloService ihelloService = RpcProxy.create(IhelloService.class);
        System.out.println(ihelloService.sayHello("郑求华"));

        ItestService itestService =RpcProxy.create(ItestService.class);
        System.out.println(itestService.getNum(4,6));
    }
}
