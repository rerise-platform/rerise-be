package com.springboot.rerise;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReriseApplication {

    public static void main(String[] args) {
        // 1. .env 파일 로드
        Dotenv dotenv = Dotenv.load();

        // 2. 환경변수 등록
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASS", dotenv.get("DB_PASS"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("GEMINI_API_KEY", dotenv.get("GEMINI_API_KEY"));
        // 3. 스프링 부트 실행
        SpringApplication.run(ReriseApplication.class, args);
    }

}
