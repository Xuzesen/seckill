package com.leo.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leo.seckill.pojo.TGoods;
import com.leo.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author leo
 * @since 2021-08-12
 */
public interface TGoodsMapper extends BaseMapper<TGoods> {

    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     * @return
     * @param goodsId
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
