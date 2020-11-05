package com.gzdzss.learn.rpc;

public class DemoServiceImpl implements DemoService {
    public String sayHello(String param) {
        System.out.println("param" + param);
        return "hello:" + param;
    }
}