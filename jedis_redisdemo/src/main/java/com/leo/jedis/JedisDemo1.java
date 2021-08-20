package com.leo.jedis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Leo
 * @create 2021-08-06 10:09
 */
public class JedisDemo1 {

    public static void main(String[] args) {
        //创建一个Jedis对象
        Jedis jedis = new Jedis("192.168.44.155",6379);

        //测试
        jedis.auth("123456");
        String value = jedis.ping();
        System.out.println(value);

    }

    //操作key string
    @Test
    public void demo1(){
        //创建一个Jedis对象
        Jedis jedis = new Jedis("192.168.44.155",6379);
        jedis.auth("123456");

        //添加
        jedis.set("name","leo");
        //获取
        String name = jedis.get("name");
        System.out.println(name);

        //批量添加
        jedis.mset("str1","v1","str2","v2","str3","v3");
        System.out.println(jedis.mget("str1","str2","str3"));

        System.out.println("===========");
        Set<String> keys = jedis.keys("*");
        for(String key:keys){
            System.out.println(key);
        }
    }

    @Test
    public void demo2(){
        //创建一个Jedis对象
        Jedis jedis = new Jedis("192.168.44.155",6379);
        jedis.auth("123456");

        jedis.lpush("key1","leo","lee","jack");
        List<String> values = jedis.lrange("key1", 0, -1);
        System.out.println(values);
    }

    @Test
    public void demo3(){
        //创建一个Jedis对象
        Jedis jedis = new Jedis("192.168.44.155",6379);
        jedis.auth("123456");

        jedis.sadd("mingz","lucy");
        jedis.sadd("mingz","jack");
        Set<String> names = jedis.smembers("mingz");
        for(String name:names){
            System.out.println(name);
        }
    }

    @Test
    public void demo4(){
        //创建一个Jedis对象
        Jedis jedis = new Jedis("192.168.44.155",6379);
        jedis.auth("123456");

        jedis.hset("hash1","userName","lisi");
        System.out.println(jedis.hget("hash1","userName"));
        System.out.println("=========");
        Map<String,String> map = new HashMap<String,String>();
        map.put("telphone","13810169999");
        map.put("address","atguigu");
        map.put("email","abc@163.com");
        jedis.hmset("hash2",map);
        List<String> result = jedis.hmget("hash2", "telphone","address","email");
        for (String element : result) {
            System.out.println(element);
        }
    }

    @Test
    public void demo5(){
        //创建一个Jedis对象
        Jedis jedis = new Jedis("192.168.44.155",6379);
        jedis.auth("123456");

        jedis.zadd("zset01", 100d, "z3");
        jedis.zadd("zset01", 90d, "l4");
        jedis.zadd("zset01", 80d, "w5");
        jedis.zadd("zset01", 70d, "z6");

        Set<String> zrange = jedis.zrange("zset01", 0, -1);
        for (String e : zrange) {
            System.out.println(e);
        }

    }


}
