package eunsungspring.todolist.config;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins("http://localhost:5173") // React 앱 주소
        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
        .allowCredentials(true); // 쿠키(세션) 인증 요청 허용
  }

  @Bean
  public FilterRegistrationBean<Filter> loginCheckFilter() {
    FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();

    filterRegistrationBean.setFilter(new LoginCheckFilter());
    filterRegistrationBean.setOrder(1); // 필터 체인 순서 (낮을수록 먼저 실행)
    filterRegistrationBean.addUrlPatterns("/api/*"); // 이 URL 패턴에만 필터 적용

    return filterRegistrationBean;
  }
}
