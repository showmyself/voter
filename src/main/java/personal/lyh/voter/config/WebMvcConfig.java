package personal.lyh.voter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import personal.lyh.voter.other.MyConstant;

//应为类型要求为WebMvcConfigurer，所以我们实现其接口
//可以使用自定义类扩展MVC的功能
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer
{
    //到时候会被SpringBoot读取

    //扩展视图
    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
        // 浏览器使用下述url ， 就会跳转到对应的页面；
        registry.addViewController("/index").setViewName("index.html");//viewName自动拼接为完整路径（根据静态资源映射）
        //上述可以添加多条
    }

    //配置静态资源的url，在properties中配置有时会失效，此处就能够成功，以后统一使用这种方式：
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:"+ MyConstant.resourceBasePath +"/");//末尾需加一个/，否则url上要输入两个static
//                .addResourceLocations("file:/IdeaProjects/voter/src/main/resources/static/");//放本地的时候用
//                .addResourceLocations("file:/opt/mysave/voter/static/");//放公网的时候用
    }

    //配置自定义的拦截器

    //@Autowired
    //private WebInterceptor myInterceptor;

    // 这个方法用来注册拦截器，自己写好的拦截器需要通过这里添加注册才能生效
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        //这里也可以通过new MyInterceptor()来解决
//        //registry.addInterceptor(myInterceptor).addPathPatterns("/**");
//        //.excludePathPatterns("/user/login", "/user/register");
//    }

    //扩展过滤器可以在实现了Filter接口的类上标明@Configuration注解即可


}