package com.leo.seckill.vo;

import com.leo.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 登录参数
 * @author Leo
 * @create 2021-08-11 15:57
 */
@Data
public class LoginVo {

    @NotNull
    @IsMobile
    private  String mobile;

    @NotNull
    @Length(min = 32)
    private String password;

}
