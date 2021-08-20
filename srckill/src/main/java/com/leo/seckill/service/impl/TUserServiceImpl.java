package com.leo.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.seckill.exception.GlobalException;
import com.leo.seckill.mapper.TUserMapper;
import com.leo.seckill.pojo.TUser;
import com.leo.seckill.service.ITUserService;
import com.leo.seckill.utils.CookieUtil;
import com.leo.seckill.utils.MD5Utils;
import com.leo.seckill.utils.UUIDUtil;
import com.leo.seckill.vo.LoginVo;
import com.leo.seckill.vo.RespBean;
import com.leo.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-08-10
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements ITUserService {

    @Autowired
    private TUserMapper tUserMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
//        //参数校验
//        if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//        if(!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }
        //根据手机号获取用户
        TUser tUser = tUserMapper.selectById(mobile);
        if(null==tUser){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //判断密码是否正确
        if(!MD5Utils.formPassToDBPass(password,tUser.getSalt()).equals(tUser.getPassword())){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //生成Cookie
        String ticket = UUIDUtil.uuid();
        //将用户信息存入redis中
        redisTemplate.opsForValue().set("tUser:" + ticket,tUser);
//        request.getSession().setAttribute(ticket,tUser);
        CookieUtil.setCookie(request,response,"userTicket",ticket);
        return RespBean.success(ticket);
    }

    /**
     * 根据cookie获取用户
     *
     * @param userTicker
     * @return
     */
    @Override
    public TUser getUserByCookie(String userTicker,HttpServletRequest request,HttpServletResponse response) {
        if(StringUtils.isEmpty(userTicker)){
            return null;
        }
        TUser tUser = (TUser) redisTemplate.opsForValue().get("tUser:"+userTicker);
        if(tUser!=null){
            CookieUtil.setCookie(request,response,"userTicket",userTicker);
        }
        return tUser;
    }

    /**
     * 更新密码
     * @param userTicket
     * @param password
     * @return
     */
    @Override
    public RespBean updatePassword(String userTicket, String password,HttpServletRequest request,HttpServletResponse response) {
        TUser tUser = getUserByCookie(userTicket, request, response);
        if(tUser==null){
            throw new GlobalException(RespBeanEnum.MOBILE_NO_EXIST);
        }
        tUser.setPassword(MD5Utils.inputPassToDBPass(password,tUser.getSalt()));
        int result = tUserMapper.updateById(tUser);
        if(1==result){
            //删除redis
            redisTemplate.delete("tUser:"+userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}
