package com.carl.testcurator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @Auther: Carl
 * @Date: 2021/04/17/11:27
 * @Description:
 */
public class CuratorExists {
    String IP = "192.168.134.137:2181, 192.168.134.137:2182, 192.168.134.137:2183";
    CuratorFramework client;

    @Before
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("get")
                .build();
        client.start();
    }

    @After
    public void after() {
        client.close();
    }

    @Test
    public void exists1() throws Exception {
        Stat stat = client.checkExists()
                .forPath("/node2");
        System.out.println(stat);
    }

    @Test
    public void exists2() throws Exception {
        //异步方式
        client.checkExists()
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        System.out.println(curatorEvent.getPath());
                        System.out.println(curatorEvent.getType());
                        System.out.println(curatorEvent.getStat());
                    }
                })
                .forPath("/get");
        Thread.sleep(10000);
    }
}
