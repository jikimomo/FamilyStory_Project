package fs.project;
import fs.project.argumentresolver.LoginMemberArgumentResolver;
import fs.project.interceptor.LoginCheckIntercept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //인터셉트 순서 체인식으로 적용. order(2) 가 있으면 .order(1)을 수행하고 다음 인터셉트 적용.
        registry.addInterceptor(new LoginCheckIntercept())
                .order(1) //첫번째 인터셉트
                .addPathPatterns("/**") //하위 전부 허용하되,
                .excludePathPatterns("/","/css/**", "/*.ico", "/error", "/signUp",
                        "/users/findId","/users/findPw" , "/kakaoLogin" ,"/logout", "/images/*.jpg","/images/*.png",
                        "/pade", "/users/findPw", "/users/images/*.png", "/moveimage","/validateMem"
                        ); // url 에는 인터셉트 먹이지마.
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        //만든 애노테이션 추가. @Login
        resolvers.add(new LoginMemberArgumentResolver());
    }

}
