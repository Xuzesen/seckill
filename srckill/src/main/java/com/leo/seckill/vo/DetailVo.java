package com.leo.seckill.vo;

import com.leo.seckill.pojo.TUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 详情返回对象
 * @author Leo
 * @create 2021-08-14 16:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {

    private TUser tUser;
    private GoodsVo goodsVo;
    private int secKillStatus;
    private int remainSeconds;

}
