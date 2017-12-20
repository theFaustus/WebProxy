package com.isa.pad.marketwarehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.isa.pad.marketwarehouse")
public class MarketWarehouseApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketWarehouseApplication.class, args);
	}
}
