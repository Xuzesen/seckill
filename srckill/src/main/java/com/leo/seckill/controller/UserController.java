package com.leo.seckill.controller;


import com.leo.seckill.pojo.TUser;
import com.leo.seckill.rabbitmq.MQSender;
import com.leo.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author leo
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;

    /**
     * 用户信息(测试)
     * @param tUser
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(TUser tUser){
        return RespBean.success(tUser);
    }

//    /**
//     * 测试发送RabbitMQ消息
//     */
//    @RequestMapping("/mq")
//    @ResponseBody
//    public void mq(){
//        mqSender.send("Hello");
//    }
//
//    /**
//     * Fanout模式
//     */
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public void mq01(){
//        mqSender.send("Hello");
//    }
//
//    /**
//     * Direct模式
//     */
//    @RequestMapping("/mq/direct01")
//    @ResponseBody
//    public void mq02(){
//        mqSender.send01("Hello,Red");
//    }
//
//    /**
//     * Direct模式
//     */
//    @RequestMapping("/mq/direct02")
//    @ResponseBody
//    public void mq03(){
//        mqSender.send02("Hello,Green");
//    }
//
//    /**
//     * Topic模式
//     */
//     @RequestMapping("/mq/topic01")
//     @ResponseBody
//     public void mq04() {
//     	mqSender.send03("Hello,Red");
//     }
//
//    /**
//     * Topic模式
//     */
//    @RequestMapping("/mq/topic02")
//     @ResponseBody
//     public void mq05() {
//     	mqSender.send04("Hello,Green");
//     }
//
//    /**
//     * Header模式
//     */
//    @RequestMapping("/mq/header01")
//     @ResponseBody
//     public void mq06() {
//     	mqSender.send05("Hello,Header01");
//     }
//
//    /**
//     * Header模式
//     */
//     @RequestMapping("/mq/header02")
//     @ResponseBody
//     public void mq07() {
//     	mqSender.send06("Hello,Header02");
//     }

}
