package com.zqh.nettyRpc.Netty.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author：zhengqh
 * @date 2020/2/14 13:58
 **/
//自定义协议
@Data
public class InvokerProtocol implements Serializable {
    private String className;//服务名
    private String methodName;//方法名称
    private Class<?> [] params;//形参列表
    private Object values[];//实参列表
}

