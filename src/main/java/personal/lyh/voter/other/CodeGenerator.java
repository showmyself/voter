package personal.lyh.voter.other;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//有更新，参考网址：https://blog.csdn.net/qq_42682745/article/details/120626012
//如果mybatis-plus-generator版本换成3.5.2，fileOverride将失效，所以就用3.5.1
public class CodeGenerator {
    public static void main(String[] args) {
        List<String> tables = new ArrayList<>();
        tables.add("pack");//添加表名

        FastAutoGenerator.create("jdbc:mysql://localhost:3306/voter","root","1122")
                .globalConfig(builder -> {
                    builder.author("lyh")//作者
                            .outputDir(System.getProperty("user.dir")+"\\src\\main\\java")//输出路径
//                            .enableSwagger()//开启swagger，需要引入相关依赖，暂时不需要
                            .commentDate("yyyy-MM-dd")
                            .fileOverride();
                })
                .packageConfig(builder -> {
                    builder.parent("generate")
                            .moduleName("template")
                            .entity("entity")
                            .service("service")
                            .serviceImpl("service.impl")
                            .controller("controller")
                            .mapper("mapper")
                            .xml("mapper")
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml,System.getProperty("user.dir")+"\\src\\main\\resources\\mapper"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables)
//                            .addTablePrefix("p_")//目前数据库表名没有前缀，所以不需要
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")

                            .entityBuilder()
                            .enableLombok()
//                            .logicDeleteColumnName("deleted")//暂时没有逻辑删除字段
                            .enableTableFieldAnnotation()

                            .controllerBuilder()
                            .enableHyphenStyle()
                            .enableRestStyle()
                            .formatFileName("%sController")

                            .mapperBuilder()
                            .enableBaseResultMap()//生成通用的resultMap
                            .superClass(BaseMapper.class)
                            .formatMapperFileName("%sMapper")
                            .enableMapperAnnotation()
                            .formatXmlFileName("%sMapper");
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}

