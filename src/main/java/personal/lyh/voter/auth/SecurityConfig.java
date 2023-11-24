package personal.lyh.voter.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import personal.lyh.voter.other.MyUtil;
import personal.lyh.voter.service.IUserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    //与SpringSecurity+Jwt有关联的类：
    //SecurityConfig,User,IUserService,UserService,JwtLoginFilter,JwtValidateFilter,JwtUtil,MyFilter
    //登录时，执行顺序是：JwtLoginFilter->UserService->JwtLoginFilter+JwtUtil
    //请求时，执行顺序是：JwtValidateFilter+JwtUtil

    @Autowired
    IUserService userService;//需要实现UserDetailService接口，内含根据用户名取得用户的方法

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        //配置用户认证的方法，userService需实现UserDetailsService接口
        //到时候登录时，就会使用接口的登录方法来获取确切的认证用户
        auth.userDetailsService(userService).passwordEncoder(new PasswordEncoder(){

            //密码加密
            @Override
            public String encode(CharSequence rawPassword) {
                return MyUtil.encrypt(rawPassword.toString(),"");
            }

            //登录的密码（参数一）对比从数据库取得的用户的密码（参数二）
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                String rawPwd = MyUtil.encrypt(rawPassword.toString(),"");
                return rawPwd.equals(encodedPassword);
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //为项目中的各接口配置相应的权限
        http.authorizeRequests()
                .antMatchers("/**").permitAll()//各方法内部会有登录判定，这里就不需要拦截了
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().permitAll()
                .and()
                .addFilter(new JwtLoginFilter(authenticationManager()))
                .addFilter(new JwtValidateFilter(authenticationManager(),userService))
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.headers().frameOptions().disable();
    }

    //网络摘抄，意义暂时不明
//    @Bean
//    CorsConfigurationSource corsConfigurationSource()
//    {
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
//        return source;
//    }
}