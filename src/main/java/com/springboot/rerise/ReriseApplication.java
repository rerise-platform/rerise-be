package com.springboot.rerise;
/*
 * Perplexity AI API의 전체 응답을 매핑하는 클래스.
 * API 호출 후 받아오는 JSON 데이터의 최상위 구조를 나타냅니다.
 */
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // 추가
import org.springframework.web.client.RestTemplate;

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
        System.setProperty("PERPLEXITY_API_KEY", dotenv.get("PERPLEXITY_API_KEY"));

        // 3. 스프링 부트 실행
        SpringApplication.run(ReriseApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
