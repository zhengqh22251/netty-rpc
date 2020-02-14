package com.zqh.nettyRpc.Netty.rpc.provider;

import com.zqh.nettyRpc.Netty.rpc.api.ItestService;

/**
 * @Author：zhengqh
 * @date 2020/2/14 14:02
 **/
public class TestServiceImpl implements ItestService {
    @Override
    public int getNum(int a, int b) {
        return a+b;
    }
}
