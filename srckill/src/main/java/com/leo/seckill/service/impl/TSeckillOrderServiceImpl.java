package com.leo.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.seckill.mapper.TSeckillOrderMapper;
import com.leo.seckill.pojo.TSeckillOrder;
import com.leo.seckill.pojo.TUser;
import com.leo.seckill.service.ITSeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-08-12
 */
@Service
public class TSeckillOrderServiceImpl extends ServiceImpl<TSeckillOrderMapper, TSeckillOrder> implements ITSeckillOrderService {

    @Autowired
    private TSeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 秒杀结果
     * @param tUser
     * @param goodsId
     * @return orderId 成功   -1 失败  0  排队中
     */
    @Override
    public Long getResult(TUser tUser, Long goodsId) {
        TSeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<TSeckillOrder>().eq("user_id", tUser.getId())
                .eq("goods_id", goodsId));
        if(null!=seckillOrder){
            return seckillOrder.getOrderId();
        }else if(redisTemplate.hasKey("isStockEmpty:"+goodsId)){
            return -1L;
        }else {
            return 0L;
        }

    }
}
