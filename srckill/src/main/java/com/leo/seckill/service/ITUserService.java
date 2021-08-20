package com.leo.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.seckill.pojo.TUser;
import com.leo.seckill.vo.LoginVo;
import com.leo.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author leo
 * @since 2021-08-10
 */
public interface ITUserService extends IService<TUser> {

    /**
     * 登录
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据cookie获取用户
     * @param userTicker
     * @return
     */
    TUser getUserByCookie(String userTicker,HttpServletRequest request,HttpServletResponse response);

    /**
     * 更新密码
     * @param userTicket
     * @param password
     * @return
     */
    RespBean updatePassword(String userTicket,String password,HttpServletRequest request,HttpServletResponse response);

}
