package roomescape.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

  private final MemberService memberService;

  public AuthenticationInterceptor(MemberService memberService) {
    this.memberService = memberService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    System.out.println("Debug: start");
    String token = null;
    if (request.getCookies() != null) {
      for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
        if ("token".equals(cookie.getName())) {
          token = cookie.getValue();
          break;
        }
      }
    }

    if (token == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
      return false;
    }

    Member member = memberService.findMemberByToken(token);
    if (member == null) {
      throw new IllegalArgumentException("유효하지 않은 토큰이나 사용자입니다.");
    }

    System.out.println("Debug: Role = " + member.getRole());
    if (!"ADMIN".equals(member.getRole())) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }

    return true;
  }
}