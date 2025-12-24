package com.empmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@ServletComponentScan //開啟了SpringBoot對Servlet組件的支持
@SpringBootApplication
@EnableAsync
public class EmpMallWebManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmpMallWebManagementApplication.class, args);
    }


}
