package com.leo.seckill.rabbitmq;

import com.leo.seckill.pojo.SeckillMessage;
import com.leo.seckill.pojo.TSeckillOrder;
import com.leo.seckill.pojo.TUser;
import com.leo.seckill.service.ITGoodsService;
import com.leo.seckill.service.ITOrderService;
import com.leo.seckill.utils.JsonUtil;
import com.leo.seckill.vo.GoodsVo;
import com.leo.seckill.vo.RespBean;
import com.leo.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Leo
 * @create 2021-08-15 10:49
 */
@Service
@Slf4j
public class MQReceiver {

//    @RabbitListener(queues = "queue")
//    public void receive(Object msg){
//        log.info("接收消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive01(Object msg){
//        log.info("QUEUE01接收消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive02(Object msg){
//        log.info("QUEUE02接收消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct01")
//    public void receive03(Object msg){
//        log.info("QUEUE01接收消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receive04(Object msg){
//        log.info("QUEUE02接收消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic01")
//    private void receive05(Object msg) {
//     	log.info("QUEUE01接收消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic02")
//    private void receive06(Object msg) {
//     	log.info("QUEUE02接收消息：" + msg);
//    }
//
//     @RabbitListener(queues = "queue_header01")
//     public void receive07(Message message) {
//     	log.info("QUEUE01接收Message对象：" + message);
//     	log.info("QUEUE01接收消息：" + new String(message.getBody()));
//     }
//
//     @RabbitListener(queues = "queue_header02")
//     public void receive08(Message message) {
//     	log.info("QUEUE02接收Message对象：" + message);
//     	log.info("QUEUE02接收消息：" + new String(message.getBody()));
//     }

    @Autowired
    private ITGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ITOrderService orderService;

    /**
     * 下单操作
     */
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接收的消息：" + message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        TUser tUser = seckillMessage.getTUser();
        //判断库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount()<1){
            return ;
        }
        //判断是否重复抢购
        TSeckillOrder seckillOrder = (TSeckillOrder) redisTemplate.opsForValue().get("order:"+tUser.getId()+":"+goodsId);
        if(seckillOrder!=null){
            return ;
        }
        //下单操作
        orderService.seckill(tUser,goodsVo);
    }


}
