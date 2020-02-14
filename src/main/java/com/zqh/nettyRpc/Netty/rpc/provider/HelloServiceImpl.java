package com.zqh.nettyRpc.Netty.rpc.provider;

import com.zqh.nettyRpc.Netty.rpc.api.IhelloService;

/**
 * @Author：zhengqh
 * @date 2020/2/14 14:01
 **/
public class HelloServiceImpl implements IhelloService {
    @Override
    public String sayHello(String content) {
        return "helloservice: "+ content;
    }
}
