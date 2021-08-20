package com.leo.seckill.controller;


import com.leo.seckill.pojo.TUser;
import com.leo.seckill.service.ITOrderService;
import com.leo.seckill.vo.OrderDetailVo;
import com.leo.seckill.vo.RespBean;
import com.leo.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author leo
 *
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private ITOrderService orderService;

    /**
     * 订单详情
     * @param tUser
     * @param orderId
     * @return
     */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(TUser tUser,Long orderId){
        if(tUser==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo detail =  orderService.detail(orderId);
        return RespBean.success(detail);
    }

}
