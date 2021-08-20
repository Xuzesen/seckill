package com.leo.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.seckill.exception.GlobalException;
import com.leo.seckill.mapper.TOrderMapper;
import com.leo.seckill.pojo.TOrder;
import com.leo.seckill.pojo.TSeckillGoods;
import com.leo.seckill.pojo.TSeckillOrder;
import com.leo.seckill.pojo.TUser;
import com.leo.seckill.service.ITGoodsService;
import com.leo.seckill.service.ITOrderService;
import com.leo.seckill.service.ITSeckillGoodsService;
import com.leo.seckill.service.ITSeckillOrderService;
import com.leo.seckill.utils.MD5Utils;
import com.leo.seckill.utils.UUIDUtil;
import com.leo.seckill.vo.GoodsVo;
import com.leo.seckill.vo.OrderDetailVo;
import com.leo.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-08-12
 */
@Service
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements ITOrderService {

    @Autowired
    private ITSeckillGoodsService seckillGoodsService;
    @Autowired
    private TOrderMapper orderMapper;
    @Autowired
    private ITSeckillOrderService seckillOrderService;
    @Autowired
    private ITGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 秒杀
     * @param tUser
     * @param goods
     * @return
     */
    @Transactional
    @Override
    public TOrder seckill(TUser tUser, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //秒杀商品表减库存
        TSeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<TSeckillGoods>().eq("goods_id", goods.getId()));

        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        boolean result = seckillGoodsService.update(new UpdateWrapper<TSeckillGoods>().setSql("stock_count = " +
                "stock_count-1").eq(
                "goods_id", goods.getId()).gt("stock_count", 0));
        if(seckillGoods.getStockCount()<1){
            //判断是否还有库存
            valueOperations.set("isStockEmpty:"+goods.getId(),"0");
            return null;
        }

        //生成订单
        TOrder order = new TOrder();
        order.setUserId(tUser.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        //生成秒杀订单
        TSeckillOrder seckillOrder = new TSeckillOrder();
        seckillOrder.setUserId(tUser.getId());
        seckillOrder.setOrderId(order.getId());
        seckillGoods.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order:"+tUser.getId()+":"+goods.getId(),seckillOrder);
        return order;
    }

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if(orderId==null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        TOrder order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detail = new OrderDetailVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);
        return detail;
    }

    /**
     * 获取秒杀地址
     * @param tUser
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(TUser tUser, Long goodsId) {
        String str = MD5Utils.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + tUser.getId()+":"+goodsId,str,60, TimeUnit.SECONDS);
        return str;
    }

    /**
     * 校验秒杀地址
     * @param tUser
     * @param goodsId
     * @param path
     * @return
     */
    @Override
    public boolean checkPath(TUser tUser, Long goodsId, String path) {
        if(tUser==null||goodsId<0|| StringUtils.isEmpty(path)){
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:"+tUser.getId()+":"+goodsId);
        return path.equals(redisPath);
    }
}
