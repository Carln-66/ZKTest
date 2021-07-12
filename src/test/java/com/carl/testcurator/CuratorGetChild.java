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

import java.util.List;

/**
 * @Auther: Carl
 * @Date: 2021/04/17/11:20
 * @Description:
 */
public class CuratorGetChild {
    String IP = "192.168.134.137:2181, 192.168.134.137:2182, 192.168.134.137:2183";
    CuratorFramework client;

    @Before
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
    }

    @After
    public void after() {
        client.close();
    }

    @Test
    public void getChild1() throws Exception {
        //获取子节点数据
        List<String> strings = client.getChildren()
                //节点路径
                .forPath("/get");
        strings.forEach(System.out::println);
    }

    @Test
    public void getChild2() throws Exception{
        client.getChildren()
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        //节点路径
                        System.out.println(curatorEvent.getPath());
                        //事件类型
                        System.out.println(curatorEvent.getType());
                        List<String> children = curatorEvent.getChildren();
                        children.forEach(System.out::println);
                    }
                })
                .forPath("/get");
        Thread.sleep(10000);
    }
}
