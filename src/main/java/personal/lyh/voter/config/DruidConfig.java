package personal.lyh.voter.config;
//在config包下面，自定义Druid组件配置以及装载，不让Springboot根据配置文件自动生成Druid组件并装载到beans中
//而是由我们自己来配置并装载

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DruidConfig
{
    @ConfigurationProperties(prefix = "spring.datasource")//貌似默认使用application.yml？
    @Bean//返回值存入bean容器
    public DataSource druidDataSource()
    {
        return new DruidDataSource();//生成实例放入bean
    }

    //可配置 Druid 监控管理后台的Servlet，通过网站访问的方式监控druid数据源的各数据
    @Bean
    public ServletRegistrationBean statViewServlet()
    {
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        // \-这些参数可以在 com.alibaba.druid.support.http.StatViewServlet
        // 的父类 com.alibaba.druid.support.http.ResourceServlet 中找到
        Map<String, String> initParams = new HashMap<>();
        initParams.put("loginUsername", "admin"); //后台管理界面的登录账号
        initParams.put("loginPassword", "123456"); //后台管理界面的登录密码
        //后台允许谁可以访问
        //initParams.put("allow", "localhost")：表示只有本机可以访问
        //initParams.put("allow", "")：为空或者为null时，表示允许所有访问
        initParams.put("allow", "");
        //deny：Druid 后台拒绝谁访问
        //initParams.put("kuangshen", "192.168.1.20");表示禁止此ip访问
        //设置初始化参数
        bean.setInitParameters(initParams);
        return bean;
    }
}


