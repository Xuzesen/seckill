package com.leo.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leo.seckill.pojo.SeckillMessage;
import com.leo.seckill.pojo.TOrder;
import com.leo.seckill.pojo.TSeckillOrder;
import com.leo.seckill.pojo.TUser;
import com.leo.seckill.rabbitmq.MQSender;
import com.leo.seckill.service.ITGoodsService;
import com.leo.seckill.service.ITOrderService;
import com.leo.seckill.service.ITSeckillOrderService;
import com.leo.seckill.utils.JsonUtil;
import com.leo.seckill.vo.GoodsVo;
import com.leo.seckill.vo.RespBean;
import com.leo.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Leo
 * @create 2021-08-12 21:04
 */
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    @Autowired
    private ITGoodsService goodsService;
    @Autowired
    private ITSeckillOrderService seckillOrderService;
    @Autowired
    private ITOrderService orderService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> script;


    private Map<Long,Boolean> EmptyStockMap = new HashMap<>();

    /**
     * 秒杀
     * @param model
     * @param tUser
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSeckill2")
    public String doSecKill2(Model model, TUser tUser,Long goodsId){
        if(tUser==null){
            //用户不存在，返回登录页面
            return "login";
        }
        model.addAttribute("tUser",tUser);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if(goods.getStockCount()<1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        //判断是否重复抢购
        TSeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<TSeckillOrder>().eq("user_id",
                tUser.getId()).eq("goods_id", goodsId));
        if(seckillOrder!=null){
            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        TOrder order = orderService.seckill(tUser,goods);
        model.addAttribute("order",order);
        model.addAttribute("goods",goods);
        return "orderDetail";
    }

    /**
     * 秒杀
     * @param tUser
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(@PathVariable String path, TUser tUser, Long goodsId){
        if(tUser==null){
            //用户不存在，返回登录页面
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //检查地址
        boolean check = orderService.checkPath(tUser,goodsId,path);
        if(!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        //判断是否重复抢购
        TSeckillOrder seckillOrder = (TSeckillOrder) redisTemplate.opsForValue().get("order:"+tUser.getId()+":"+goodsId);
        if(seckillOrder!=null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //通过内存标记，减少Redis的访问
        if(EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存
//        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        Long stock = (Long)redisTemplate.execute(script, Collections.singletonList("seckillGoods:"+goodsId),Collections.EMPTY_LIST);
        if(stock<0){
            EmptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods:"+goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(tUser, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        //0返回排队中
        return RespBean.success(0);

/*
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if(goods.getStockCount()<1){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢购
//        TSeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<TSeckillOrder>().eq("user_id",
//                tUser.getId()).eq("goods_id", goodsId));
        TSeckillOrder seckillOrder = (TSeckillOrder) redisTemplate.opsForValue().get("order:"+tUser.getId()+":"+goodsId);
        if(seckillOrder!=null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        TOrder order = orderService.seckill(tUser,goods);
        return RespBean.success(order);
 */
    }

    /**
     * 秒杀结果
     * @param tUser
     * @param goodsId
     * @return orderId 成功   -1 失败  0  排队中
     */
    @RequestMapping(value="/result",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(TUser tUser,Long goodsId){
        if(tUser==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(tUser,goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 获取秒杀地址
     * @param tUser
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(TUser tUser,Long goodsId){
        if(tUser==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        String str = orderService.createPath(tUser,goodsId);
        return RespBean.success(str);
    }

    /**
     * 初始化，商品库存数量加载到Redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVo ->{
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(),false);
        });
    }
}
