//package com.back.usercenter.Intercptor;
//
//import com.back.usercenter.constant.StatusConstant;
//import org.apache.commons.lang3.ObjectUtils;
//import org.springframework.core.MethodParameter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.AnnotatedType;
//
///**
// * @author yang
// * @create 2024-05-12 21:21
// */
//@Component
//public class PermissionCheckInterceptor extends HandlerInterceptorAdapter {
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println("asdadads");
//        HttpSession session = request.getSession();
//
//        Object attribute = session.getAttribute(StatusConstant.USER_LOGIN_STATE);
//        if (ObjectUtils.isEmpty(attribute)) {
//            return false;
//        }
//        String contextPath = request.getContextPath();
//        String method = request.getMethod();
//
//        HandlerMethod handlerMethod = (HandlerMethod) handler;
//        Class<?> beanType = handlerMethod.getBeanType();
//        AnnotatedType[] annotatedInterfaces = beanType.getAnnotatedInterfaces();
//        Annotation[] annotations = beanType.getAnnotations();
//
//        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
//
//        return true;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        super.postHandle(request, response, handler, modelAndView);
//    }
//}
