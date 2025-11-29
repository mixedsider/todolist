package eunsungspring.todolist.config;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  // 1. SPA 라우팅 처리를 위한 설정
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    // 루트 (/) 요청을 index.html로 포워딩
    registry.addViewController("/")
            .setViewName("forward:/index.html");

    // SPA 라우팅 경로를 index.html로 포워딩 (JS, CSS를 제외한 경로)
    registry.addViewController("/{path:[^\\.]*}")
            .setViewName("forward:/index.html");
  }

  // 2. 정적 리소스 핸들링 강제 등록 (IDE 실행 시 Classpath 문제 방지용)
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 모든 URL 경로 (/**)에 대해 클래스패스 내 /static/ 폴더를 정적 리소스 위치로 설정
    registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/");
  }

  // 💡 configureDefaultServletHandling 메소드는 삭제되었습니다.
  // @Override
  // public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
  //     configurer.enable(); // 이 부분이 에러를 유발하므로 제거합니다.
  // }
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins("http://localhost:**") // React 앱 주소
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
