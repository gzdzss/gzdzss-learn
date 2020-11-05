package com.gzdzss.learn.rpc.registry;

import java.io.Serializable;

/**
 * @author Andrew
 * @create 2020/11/5 5:39 下午
 */
public class ServerInfo implements Serializable {
    
    private String host;
    
    private int port;
    
    public ServerInfo() {
    }
    
    public ServerInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
}
