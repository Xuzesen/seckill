package com.leo.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.seckill.pojo.TSeckillOrder;
import com.leo.seckill.pojo.TUser;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author leo
 * @since 2021-08-12
 */
public interface ITSeckillOrderService extends IService<TSeckillOrder> {

    /**
     * 秒杀结果
     * @param tUser
     * @param goodsId
     * @return orderId 成功   -1 失败  0  排队中
     */
    Long getResult(TUser tUser, Long goodsId);
}
