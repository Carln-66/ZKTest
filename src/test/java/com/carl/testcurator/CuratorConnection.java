package com.carl.testcurator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @Auther: Carl
 * @Date: 2021/04/17/10:03
 * @Description:
 * session重连策略
 * 3秒后重连一次，只重连一次
 * RetryPolicy retryPolicy = new RetryOneTime(3000);
 *
 * 每三秒重连一次，重连三次
 * RetryPolicy retryPolicy = new RetryNTimes(3, 3000);
 *
 * 每三秒重连一次，总等待时间超过10秒之后停止重连
 * RetryPolicy retryPolicy = new RetryUtilElapsed(10000, 3000);
 */
public class CuratorConnection {
    public static void main(String[] args) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        //创建连接对象
        CuratorFramework client = CuratorFrameworkFactory.builder()
                //IP地址端口号
                .connectString("192.168.134.137:2181,192.168.134.137:2182,192.168.134.137:2183")
                //会话超时时间
                .sessionTimeoutMs(5000)
                //客户端和服务器端断开连接后在3秒之后进行一次重连
//                .retryPolicy(new RetryOneTime(3000))
                .retryPolicy(retryPolicy)
                //命名空间
                .namespace("create")
                //构建连接对象
                .build();

        //打开连接
        client.start();
        System.out.println(client.isStarted());
        //关闭连接
        client.close();
    }
}
