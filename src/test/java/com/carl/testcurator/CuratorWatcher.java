package com.carl.testcurator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @Auther: Carl
 * @Date: 2021/04/17/11:32
 * @Description:
 */
public class CuratorWatcher {
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
    public void watcher1() throws Exception {
        //监视某个节点的数据变化
        //arg1: 连接对象
        //arg2: 监视节点路径
        NodeCache nodeCache = new NodeCache(client, "/watcher1");
        //启动监视器对象
        nodeCache.start();
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            //当节点变化时回调的方法
            @Override
            public void nodeChanged() throws Exception {
                System.out.println(nodeCache.getCurrentData().getPath());
                System.out.println(new String(nodeCache.getCurrentData().getData()));
            }
        });
        Thread.sleep(100000);
        //关闭监视器
        nodeCache.close();
    }

    @Test
    public void watcher2() throws Exception {
        //监视子节点的变化
        //arg1: 连接对象
        //arg2: 监视节点路径
        //arg3: 事件中是否可以获取节点的数据
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, "/watcher1", true);
        pathChildrenCache.start();
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            //当子节点方法变化时回调的方法
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                System.out.println(pathChildrenCacheEvent.getType());
                System.out.println(pathChildrenCacheEvent.getData().getPath());
                System.out.println(new String(pathChildrenCacheEvent.getData().getData()));
            }
        });
        Thread.sleep(100000);
        pathChildrenCache.close();

    }
}
