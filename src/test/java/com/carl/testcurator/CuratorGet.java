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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: Carl
 * @Date: 2021/04/17/11:12
 * @Description:
 */
public class CuratorGet {
    String IP = "192.168.134.137:2184, 192.168.134.137:2186, 192.168.134.137:2187";
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
    public void get1() throws Exception {
        //读取节点数
        byte[] bytes = client.getData()
                //节点路径
                .forPath("/node1");
        System.out.println(new String(bytes));
    }

    @Test
    public void get2() throws Exception {
        //读取数据时读取节点的属性
        Stat stat = new Stat();
        byte[] bytes = client.getData()
                .storingStatIn(stat)
                .forPath("/node1");
        System.out.println(new String(bytes));
        System.out.println(stat.getVersion());
    }

    final AtomicInteger number = new AtomicInteger();
    volatile boolean bol = false;
    @Test
    public void get3() throws Exception {
        System.out.println(number.getAndIncrement());
        synchronized (this) {
            try {
                if (!bol) {
                    System.out.println(bol);
                    bol = true;
                    long start = System.currentTimeMillis();
                    client.getData()
                            .inBackground(new BackgroundCallback() {
                                @Override
                                public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                                    //节点路径
//                                        System.out.println(curatorEvent.getPath());
                                    //事件类型
                                    System.out.println(curatorEvent.getType());
                                    //数据
                                    System.out.println(new String(curatorEvent.getData()));
                                }
                            })
                            .forPath("/node1");
                    long end = System.currentTimeMillis();
                    System.out.println(end - start);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("并发数量为" + number.intValue());
        }
    }
}
