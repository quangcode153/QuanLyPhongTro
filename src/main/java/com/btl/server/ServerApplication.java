package com.btl.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lớp khởi chạy (Main Application Class) của hệ thống Backend Spring Boot.
 * Nạp cấu hình tự động, khởi động IoC Container và khởi chạy máy chủ nhúng Tomcat.
 */
@SpringBootApplication
public class ServerApplication {

	/**
	 * Phương thức khởi chạy chính của ứng dụng Spring Boot.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}
}
