package com.leo.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.seckill.pojo.TOrder;
import com.leo.seckill.pojo.TUser;
import com.leo.seckill.vo.GoodsVo;
import com.leo.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author leo
 * @since 2021-08-12
 */
public interface ITOrderService extends IService<TOrder> {

    /**
     * 秒杀
     * @param tUser
     * @param goods
     * @return
     */
    TOrder seckill(TUser tUser, GoodsVo goods);

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    OrderDetailVo detail(Long orderId);

    /**
     * 获取秒杀地址
     * @param tUser
     * @param goodsId
     * @return
     */
    String createPath(TUser tUser, Long goodsId);

    /**
     * 校验秒杀地址
     * @param tUser
     * @param goodsId
     * @param path
     * @return
     */
    boolean checkPath(TUser tUser, Long goodsId, String path);
}
