package com.d108.sduty;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableAspectJAutoProxy
@SpringBootApplication
@MapperScan(basePackages = "com.d108.sduty.repo")
public class SdutyServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SdutyServerApplication.class, args);
	}

}
