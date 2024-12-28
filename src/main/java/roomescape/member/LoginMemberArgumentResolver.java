package roomescape.member;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

  private MemberService memberService;

  public LoginMemberArgumentResolver(MemberService memberService) {
    this.memberService = memberService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(Member.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    jakarta.servlet.http.HttpServletRequest request = webRequest.getNativeRequest(jakarta.servlet.http.HttpServletRequest.class);
    if(request == null) {
      throw new IllegalArgumentException("요청이 유효하지 않습니다.");
    }
    jakarta.servlet.http.Cookie[] cookies = request.getCookies();
    if(cookies == null) {
      throw new IllegalArgumentException("유효한 쿠키를 찾을 수 없습니다.");
    }

    String token = null;
    for (jakarta.servlet.http.Cookie cookie : cookies) {
      if ("token".equals(cookie.getName())) {
        token = cookie.getValue();
        break;
      }
    }
    if(token == null) {
      throw new IllegalArgumentException("유효한 토큰을 찾을 수 없습니다.");
    }

    Member member = memberService.findMemberByToken(token);
    if (member == null) {
      throw new IllegalArgumentException("유효하지 않은 토큰 또는 사용자입니다.");
    }

    return member;
  }
}