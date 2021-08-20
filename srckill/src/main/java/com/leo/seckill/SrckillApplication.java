package com.leo.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.leo.seckill.mapper")
public class SrckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrckillApplication.class, args);
    }

}
