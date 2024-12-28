package roomescape;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.member.AuthenticationInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final AuthenticationInterceptor authenticationInterceptor;

  public WebConfig(AuthenticationInterceptor authenticationInterceptor) {
    this.authenticationInterceptor = authenticationInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authenticationInterceptor)
        .addPathPatterns("/admin/**")
        .excludePathPatterns("/login", "/register");
  }
}
