package com.gzdzss.learn.rpc.proxy;

import com.gzdzss.learn.rpc.Constants;
import com.gzdzss.learn.rpc.protocol.Header;
import com.gzdzss.learn.rpc.protocol.Message;
import com.gzdzss.learn.rpc.protocol.Request;
import com.gzdzss.learn.rpc.registry.Registry;
import com.gzdzss.learn.rpc.registry.ServerInfo;
import com.gzdzss.learn.rpc.transport.Connection;
import com.gzdzss.learn.rpc.transport.DemoRpcClient;
import com.gzdzss.learn.rpc.transport.NettyResponseFuture;
import io.netty.channel.ChannelFuture;
import org.apache.curator.x.discovery.ServiceInstance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.gzdzss.learn.rpc.Constants.MAGIC;
import static com.gzdzss.learn.rpc.Constants.VERSION_1;

/**
 * @author Andrew
 * @create 2020/11/5 5:38 下午
 */
public class DemoRpcProxy implements InvocationHandler {
    // 需要代理的服务(接口)名称
    private String serviceName;
    
    public Map<Method, Header> headerCache = new ConcurrentHashMap<>();
    
    
    // 用于与Zookeeper交互，其中自带缓存
    private Registry<ServerInfo> registry;
    
    public DemoRpcProxy(String serviceName,
                        Registry<ServerInfo> registry) throws Exception {
        this.serviceName = serviceName;
        this.registry = registry;
    }
    
    public static <T> T newInstance(Class<T> clazz, Registry<ServerInfo> registry) throws Exception {
        // 创建代理对象
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                new DemoRpcProxy("demoService", registry));
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 从Zookeeper缓存中获取可用的Server地址,并随机从中选择一个
        List<ServiceInstance<ServerInfo>> serviceInstances =
                registry.queryForInstances(serviceName);
        ServiceInstance<ServerInfo> serviceInstance =
                serviceInstances.get(ThreadLocalRandom.current().nextInt(serviceInstances.size()));
        // 创建请求消息，然后调用remoteCall()方法请求上面选定的Server端
        String methodName = method.getName();
        Header header = headerCache.computeIfAbsent(method, h -> new Header(MAGIC, VERSION_1));
        Message<Request> message = new Message(header, new Request(serviceName, methodName, args));
        return remoteCall(serviceInstance.getPayload(), message);
    }
    
    protected Object remoteCall(ServerInfo serverInfo, Message message) throws Exception {
        if (serverInfo == null) {
            throw new RuntimeException("get available server error");
        }
        Object result;
        try {
            // 创建DemoRpcClient连接指定的Server端
            DemoRpcClient demoRpcClient = new DemoRpcClient(serverInfo.getHost(), serverInfo.getPort());
            ChannelFuture channelFuture = demoRpcClient.connect().awaitUninterruptibly();
            // 创建对应的Connection对象，并发送请求
            Connection connection = new Connection(channelFuture, true);
            NettyResponseFuture responseFuture = connection.request(message, Constants.DEFAULT_TIMEOUT);
            // 等待请求对应的响应
            result = responseFuture.getPromise().get(Constants.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }
    
   
}
