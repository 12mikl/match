package com.back.partnerback.configure;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author yang
 * @create 2023-07-08 15:32
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class SwaggerConfig implements WebMvcConfigurer {

    // 放开资源拦截
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                // 创建接口文档的具体信息
                .apiInfo(webApiInfo())
                // 创建选择器，控制哪些接口被加入文档
                .select()
                // 指定@ApiOperation标注的接口被加入文档
                .apis(RequestHandlerSelectors.basePackage("com.back.partnerback.controller"))
                .build();
    }
    // 创建接口文档的具体信息，会显示在接口文档页面中
    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                // 文档标题
                .title("伙伴匹配系统接口文档")
                // 文档描述
                .description("本文档描述了用户管理系统的接口定义")
                // 版本
                .version("1.0")
//                // 联系人信息
//                .contact(new Contact("baobao", "http://baobao.com", "baobao@qq.com"))
                // 版权
                .license("baobao")
                // 版权地址
                .licenseUrl("http://www.xxx.com")
                .build();
    }
}
