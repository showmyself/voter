package personal.lyh.voter.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import personal.lyh.voter.service.IUserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//此类由SecurityConfig主动设置，因此不在这里添加@Component注解
public class JwtValidateFilter extends BasicAuthenticationFilter
{
    IUserService userService;

    //构造方法
    public JwtValidateFilter(AuthenticationManager authenticationManager,IUserService userService) {
        super(authenticationManager);
        this.userService=userService;
    }

    //过滤请求
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        //此处调用了下文的setAuthentication方法
        SecurityContextHolder.getContext().setAuthentication(setAuthentication(request.getHeader("token")));
        super.doFilterInternal(request, response, chain);
    }

    //根据jwt配置本次请求所认证的用户信息，如果为空则springSecurity会返回认证失败
    private UsernamePasswordAuthenticationToken setAuthentication(String jwt) {

        //检查是否为合法的jwt串，内含过期检测
        if (!JwtUtil.verify(jwt))
            return null;//返回了null，就代表没有用户没有登录认证或认证失败

        //获取userId
        String userId = JwtUtil.getUserId(jwt);

        if (userId==null||userId.equals("")) {
            return null;
        }

        //本项目的principal只需要userId
        return new UsernamePasswordAuthenticationToken(userId, null, null);
    }
}
