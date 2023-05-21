package com.xueyu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author durance
 */

@SpringBootApplication
@MapperScan("com.xueyu.dao")
public class LoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginApplication.class);
	}

}
