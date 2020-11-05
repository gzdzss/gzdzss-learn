package com.gzdzss.learn.rpc.registry;

import org.apache.curator.x.discovery.ServiceInstance;

/**
 * @author Andrew
 * @create 2020/11/5 5:40 下午
 */
public abstract class AbstractServiceInstanceListener<T> implements ServiceInstanceListener<T> {
    
    @Override
    public void onFresh(ServiceInstance<T> serviceInstance, ServerInfoEvent event) {
        switch (event) {
            case ON_REGISTER:
                onRegister(serviceInstance);
                break;
            case ON_UPDATE:
                onUpdate(serviceInstance);
                break;
            case ON_REMOVE:
                onRemove(serviceInstance);
                break;
        }
    }
}
