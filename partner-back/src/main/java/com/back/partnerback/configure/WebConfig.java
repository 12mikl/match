//package com.back.usercenter.configure;
//
//import com.back.usercenter.Intercptor.PermissionCheckInterceptor;
//import com.back.usercenter.constant.StatusConstant;
//import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.config.annotation.*;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
///**
// * @author yang
// * @create 2024-05-12 20:33
// */
//@Configuration
//@EnableSwagger2
//@EnableKnife4j
//public class WebConfig implements WebMvcConfigurer {
//
//    @Resource
//    private PermissionCheckInterceptor permissionCheckInterceptor;
//
//    /**
//     * 添加拦截器，可添加多个，执行顺序按@Order，order越小先执行
//     * @param registry
//     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(permissionCheckInterceptor).addPathPatterns("/user/**"). // /**表示拦截所有路径，后面则是忽略掉路径
//                excludePathPatterns("/user/userLogin","/user/userQuit",
//                        "/user/userRegister","doc.html","classpath:/META-INF/resources/");
//    }
//
//    /**
//     *  静态资源的地址映射
//     * @param registry
//     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        System.out.println("registry1");
//        registry.addResourceHandler("doc.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }
//
//    /**
//     * 请请求转到一个页面上
//     * @param registry
//     */
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("index");
//        System.out.println("registry2");
//    }
//
//    /**
//     *  实现ajax跨域请求
//     * @param registry
//     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        System.out.println("registry3");
//    }
//
//
//    @Bean
//    public Docket webApiConfig(){
//        return new Docket(DocumentationType.SWAGGER_2)
//                // 创建接口文档的具体信息
//                .apiInfo(webApiInfo())
//                // 创建选择器，控制哪些接口被加入文档
//                .select()
//                // 指定@ApiOperation标注的接口被加入文档
//                .apis(RequestHandlerSelectors.basePackage("com.back.usercenter.controller"))
//                .build();
//    }
//
//    // 创建接口文档的具体信息，会显示在接口文档页面中
//    private ApiInfo webApiInfo(){
//        return new ApiInfoBuilder()
//                // 文档标题
//                .title("用户管理系统接口文档")
//                // 文档描述
//                .description("本文档描述了用户管理系统的接口定义")
//                // 版本
//                .version("1.0")
////                // 联系人信息
////                .contact(new Contact("baobao", "http://baobao.com", "baobao@qq.com"))
//                // 版权
//                .license("baobao")
//                // 版权地址
//                .licenseUrl("http://www.xxx.com")
//                .build();
//    }
//}
