package com.leo.seckill.vo;

import com.leo.seckill.pojo.TGoods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品返回对象
 * @author Leo
 * @create 2021-08-12 14:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVo extends TGoods {

    private BigDecimal seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;


}
