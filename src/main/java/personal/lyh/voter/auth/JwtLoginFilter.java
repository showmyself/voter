package personal.lyh.voter.auth;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import personal.lyh.voter.entity.Result;
import personal.lyh.voter.entity.User;
import personal.lyh.voter.entity.dto.LoginDto;
import personal.lyh.voter.other.MyValidator;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

//此类由SecurityConfig主动设置，因此不在这里添加@Component注解
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter
{
    private AuthenticationManager authenticationManager;

    //构造方法
    public JwtLoginFilter(AuthenticationManager authenticationManager) {

        this.authenticationManager = authenticationManager;
        //此处设定了接口后，无需在Controller层添加接口
        super.setFilterProcessesUrl("/user/login");
    }

    //根据用户传来的用户名和密码和验证码尝试登录
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
    {
        //将post请求携带的json数据引入LoginDto
        LoginDto dto=null;
        try {
            dto = JSONObject.parseObject(request.getInputStream(), StandardCharsets.UTF_8, LoginDto.class);
        } catch (IOException e) {
            try {
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(JSON.toJSONString(Result.fail("参数错误，登录失败")));
            } catch (IOException ignored) { }
            return null;
        }

        //参数校验
        if(!MyValidator.verifyString(dto.getUsernameOrEmail(),3,36)||
        !MyValidator.verifyString(dto.getPassword(),4,36))
        {
            try {
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(JSON.toJSONString(Result.fail("参数格式验证不通过，登录失败")));
            } catch (IOException ignored) { }
            return null;
        }

        //先检查验证码是否正确，验证码从session中取
//        Object captcha = request.getSession().getAttribute("captcha");
//        boolean captchaPass=false;
//        if(captcha!=null && dto.getCaptcha() != null && ((String) captcha).equalsIgnoreCase(dto.getCaptcha()))
//            captchaPass=true;
//        if(!captchaPass)
//        {
//            try {
//                response.setCharacterEncoding("UTF-8");
//                response.getWriter().print(JSON.toJSONString(Result.fail("验证码错误")));
//            } catch (IOException ignored) { }
//            return null;
//        }

        //生成用户名密码认证实体
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsernameOrEmail(),
                        dto.getPassword(),
                        new ArrayList<>())
                //之后是进入实现了UserDetailService接口的UserService内的loadUserByUsername方法，SpringSecurity会根据查询得到的User与当前User进行对比
        );
    }

    //用户登录成功之后做什么，此处会返回给用户一个合法的jwt串
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException
    {
        User user = (User) authResult.getPrincipal();
        user.setPassword(null);
        String jwtToken = JwtUtil.generateJwt(user);

        //设置当前线程所属的用户id
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getId().toString(), null, null));

        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(JSON.toJSONString(Result.success("登录成功", MapUtil.builder()
                .put("user", user)
                .put("token",jwtToken)
                .map())));
    }

    //用户登录失败后做什么，此处返回一条消息
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(JSON.toJSONString(Result.fail("用户名或密码错误")));
    }
}
