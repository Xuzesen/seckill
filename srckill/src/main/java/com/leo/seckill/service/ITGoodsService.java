package com.leo.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.seckill.pojo.TGoods;
import com.leo.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author leo
 * @since 2021-08-12
 */
public interface ITGoodsService extends IService<TGoods> {

    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     * @param goodsId
     * @return
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
