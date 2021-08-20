package com.leo.jedis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * @author Leo
 * @create 2021-08-08 16:27
 */
public class RedisClusterDemo {

    public static void main(String[] args) {
        //创建对象
        HostAndPort hostAndPort = new HostAndPort("192.168.44.155", 6379);
        JedisCluster jedisCluster = new JedisCluster(hostAndPort);

        //进行操作
        jedisCluster.set("b1","v1");

        String value = jedisCluster.get("b1");
        System.out.println("value:" + value);
        jedisCluster.close();

    }

}
