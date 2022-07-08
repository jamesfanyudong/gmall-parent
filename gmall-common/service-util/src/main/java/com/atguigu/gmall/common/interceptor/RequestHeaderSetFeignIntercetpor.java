package com.atguigu.gmall.common.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author fanyudong
 */
@Component
public class RequestHeaderSetFeignIntercetpor implements RequestInterceptor {
    //feign在异步情况下丢失老请求。RequestContextHolder 跟线程绑定，异步属于新线程

    @Override
    public void apply(RequestTemplate template) {
        //1、先从Spring给我们提供的RequestContextHolder 中拿到当前线程的请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null){
            //2、拿到当前请求
            HttpServletRequest request = attributes.getRequest();
            //3、把原来所有的请求头继续放到 template 中，方便feign远程调用创建请求之前能得到所有的
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()){
                //请求头名
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                if ("userTempId".equalsIgnoreCase(headerName) || "userId".equalsIgnoreCase(headerName)){
                    //存到模板请求头中
                    template.header(headerName,headerValue);
                }


            }


        }




    }
}
