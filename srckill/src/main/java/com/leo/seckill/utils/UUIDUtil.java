package com.leo.seckill.utils;

import java.util.UUID;

/**
 * @author Leo
 * @create 2021-08-11 19:53
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
