package com.jiang.mall;

import com.jiang.mall.util.EncryptionUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static com.jiang.mall.util.EncryptionUtils.getCpuId;


@MapperScan("com.jiang.mall.dao")
@SpringBootApplication
@EnableTransactionManagement
public class MallApplication {

    public static void main(String[] args) {
        String keyInput = getCpuId();
        System.out.println("keyInput: " + keyInput);
        System.out.println("keyOutput: " + EncryptionUtils.encryptToSHA256(keyInput, "jiang"));
        SpringApplication.run(MallApplication.class, args);
    }

}
