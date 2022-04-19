package fs.project.interceptor;
import fs.project.session.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
@Slf4j
//현재 세션이 없다면 바로 인터셉트 걸어주는 것.
public class LoginCheckIntercept implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 현재 요청된 URI받음.
        String requestURI = request.getRequestURI();
        log.info("인증 체크 인터셉트 실행 {}", requestURI);
        // 현재 세션 담기
        HttpSession session = request.getSession();
        // 세션이 null값이거나 또는 로그인한 정보가 없다면.
        if (session == null || session.getAttribute(SessionConst.LOGIN_USER) == null) {
            log.info("미인증 사용자 요청");
            // 홈화면을 redirect한다.
            response.sendRedirect("/?redirectURL="+requestURI);
            // 인터셉트 종료.
            return false;
        }
        //다음 인터셉트로 이동
        return true;
    }

}
