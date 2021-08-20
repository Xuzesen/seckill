package com.leo.jedis;

import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 * @author Leo
 * @create 2021-08-06 14:20
 */
public class PhoneCode {

    public static void main(String[] args) {
        //模拟验证码发送
//        verifyCode("13480303758");
        //校验
//        getRedisCode("13480303758","348945");
    }

    //1.生成6位的数组验证码
    public static String getCode(){
        Random random = new Random();
        String code = "";
        for(int i=0;i<6;i++){
            int rand = random.nextInt(10);
            code+=rand;
        }
        return code;
    }

    //2.每个手机每天只能发送三次，验证码发送到redis中，设置过期时间120秒
    public static void verifyCode(String phone){
        //创建一个Jedis对象  连接redis
        Jedis jedis = new Jedis("192.168.44.155",6379);
        jedis.auth("123456");

        //拼接key
        //手机发送次数key
        String countKey = "VerifyCode" + phone + ":count";
        //验证码key
        String codeKey = "VerifyCode" + phone + ":code";

        //每个手机每天只能发送三次
        String count = jedis.get(countKey);
        if(count==null){
            //没有发送次数，第一次发送
            //设置发送次数是1
            jedis.setex(countKey,24*60*60,"1");
        }else if (Integer.parseInt(count)<=2){
            //发送次数+1
            jedis.incr(countKey);
        }else if (Integer.parseInt(count)>2){
            //发送3次，不能再发送
            System.out.println("今天的发送次数已超过三次");
            jedis.close();
        }
        //发送的验证码要放到redis里面
        String vcode = getCode();
        jedis.setex(codeKey,120,vcode);
        jedis.close();
    }

    //3.验证码校验
    public static void getRedisCode(String phone,String code){
        //从redis中获取验证码
        //创建一个Jedis对象  连接redis
        Jedis jedis = new Jedis("192.168.44.155",6379);
        jedis.auth("123456");
        //验证码key
        String codeKey = "VerifyCode" + phone + ":code";
        String redisCode = jedis.get(codeKey);
        //判断
        if(redisCode.equals(code)){
            System.out.println("成功");
        }else {
            System.out.println("失败");
        }
        jedis.close();
    }

}
