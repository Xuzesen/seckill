package com.leo.seckill.vo;

import com.leo.seckill.pojo.TOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单详情返回对象
 * @author Leo
 * @create 2021-08-14 21:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVo {

    private TOrder order;
    private GoodsVo goodsVo;

}
