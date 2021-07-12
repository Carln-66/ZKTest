package com.carl.testcurator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @Auther: Carl
 * @Date: 2021/04/17/10:51
 * @Description:
 */
public class CuratorSet {
    String IP = "192.168.134.137:2181, 192.168.134.137:2182, 192.168.134.137:2183";
    CuratorFramework client;

    @Before
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("set")
                .build();
        client.start();
    }

    @After
    public void after() {
        client.close();
    }

    @Test
    public void set1() throws Exception {
        client.setData()
                //arg1: 节点路径
                //arg2: 节点的数据
                .forPath("/node1", "node11".getBytes(StandardCharsets.UTF_8));
        System.out.println("结束");
    }

    @Test
    public void set2() throws Exception {
        client.setData()
                //指定版本号
                .withVersion(-1)
                .forPath("/node1", "node111".getBytes(StandardCharsets.UTF_8));
        System.out.println("结束");
    }

    @Test
    public void set3() throws Exception {
        client.setData()
                .withVersion(-1)
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        //节点路径
                        System.out.println(curatorEvent.getPath());
                        //节点的数据类型
                        System.out.println(curatorEvent.getType());
                    }
                })
                .forPath("/node1", "node1".getBytes(StandardCharsets.UTF_8));
        //由于是异步方式，先休眠
        Thread.sleep(5000);
        System.out.println("结束");
    }
}
