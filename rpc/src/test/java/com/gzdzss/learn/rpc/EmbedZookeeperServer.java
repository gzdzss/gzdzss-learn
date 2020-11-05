package com.gzdzss.learn.rpc;

import org.apache.curator.test.TestingServer;

import java.io.File;
import java.io.IOException;

/**
 * @author Andrew
 * @create 2020/11/5 5:52 下午
 */
public class EmbedZookeeperServer {
    
    private static TestingServer testingServer;
    
    /**
     * Embed ZooKeeper.
     *
     * @param port ZooKeeper port
     */
    public static void start(final int port) {
        try {
            testingServer = new TestingServer(port, new File(String.format("target/test_zk_data/%s/", System.nanoTime())));
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            ex.printStackTrace();
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    Thread.sleep(1000L);
                    testingServer.close();
                } catch (final InterruptedException | IOException ignore) {
                }
            }));
        }
    }
    
    public static void main(String[] args) {
        EmbedZookeeperServer.start(2181);
    }
    
}
