package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.gateway.properties.AuthProperties;
import com.atguigu.gmall.model.user.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author fanyudong
 */
@Slf4j
@Component
public class GlobalAuthFilter implements GlobalFilter {
    @Autowired
    AuthProperties authProperties;


    // ant风格匹配

    AntPathMatcher pathMatcher =  new AntPathMatcher();
    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * @param exchange request+response
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        log.info("拦截到请求【{}】",path);
        // 判断需要拦截还是放行

        //1.rpc/inner/** 所有请求都不能访问。响应Result.fail();

        if (pathMatch(authProperties.getNoAuthUrl(),path)){
            //当前浏览器访问了非授权页 /rpc/inner/**
            //打回去，响应错误的json
            return writeJson(response, ResultCodeEnum.NOAUTH_URL);
        }

        //2、再看是否需要登录才能访问
        if (pathMatch(authProperties.getLoginUrl(),path)){
            //3、验证登录的用户； 拿到令牌
          String token =  getToken(request);
          return checkTokenOrRedirect(exchange, chain, request, response, token);
        }
        //3、如果已经登录了，进行操作addCart。但是addCart不是需要登录的
        //如果已经登录了，无论这个请求是什么，都可以把用户id透传下去。
        String token =  getToken(request);
        if (!StringUtils.isEmpty(token)){
            //说明请求带token
            return checkTokenOrRedirect(exchange, chain, request, response, token);
        }

        //4、没有就直接放行。放行之前也要把可能携带的临时id透传下去
       String tempId =  getTempId(exchange);
        //5、放行之前，需要给请求头中加一个 UserId 字段。
        //request 是不允许改的。我们需要创建一个新的
        ServerWebExchange newExchange = exchange.mutate()
                .request(request.mutate()
                        .header("userTempId", tempId)
                        .build())
                .response(response)
                .build();




        // 6.放行
        return chain.filter(newExchange);
    }
    /**
     * 获取令牌
     *
     * @param request
     * @return
     */
    private String getToken(ServerHttpRequest request) {

        String token = "";
        //1、先看Cookie有没有
        HttpCookie cookie = request.getCookies().getFirst("token");
        // token=""
        if(cookie!=null){
            token = cookie.getValue();
            if (StringUtils.isEmpty(token)){
                token = request.getHeaders().getFirst("token");
            }
        }else {
            //2、尝试去token头中取
            token = request.getHeaders().getFirst("token");
        }


        return token;

    }

    private Mono<Void> checkTokenOrRedirect(ServerWebExchange exchange, GatewayFilterChain chain, ServerHttpRequest request, ServerHttpResponse response, String token) {
       UserInfo info =  validToken(token);
       if (info != null){
           // 校验token通过
           Long id = info.getId();
          String tempId =  getTempId(exchange);
           //5、放行之前，需要给请求头中加一个 UserId 字段。
           //request 是不允许改的。我们需要创建一个新的
           ServerHttpRequest newReq = request.mutate()
                   .header("userId", id.toString())
                   .header("userTempId", tempId)
                   .build();
           ServerWebExchange newExchange = exchange.mutate()
                   .request(newReq)
                   .response(response)
                   .build();
           // 放行
           return chain.filter(newExchange);


       } else{
           //5、校验token 不通过。 打回登录页，去登录
           log.info("用户令牌【{}】非法，打回登录页", token);
           return locationToPage(response,authProperties.getLoginPage());

       }

    }

    private UserInfo validToken(String token) {
        // 1.没令牌
        if (StringUtils.isEmpty(token)){
            return null;
        }

        UserInfo info = getUserInfo(token);
        if (info == null){
            // 有令牌是假的
            return null;
        }

        return info;

    }

    /**
     * 根据令牌去redis检索用户信息
     *
     * @param token
     * @return
     */
    private UserInfo getUserInfo(String token) {
        String json = redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_PREFIX + token);
        if (StringUtils.isEmpty(json)){
            return null;

        }
        UserInfo info = Jsons.toObj(json, UserInfo.class);
        return info;
    }

    /**
     * 调到指定页面
     * @param response
     * @param loginPage
     * @return
     */

    private Mono<Void> locationToPage(ServerHttpResponse response, String loginPage) {
        //1、给浏览器响应一个状态码 302。 响应头命令浏览器跳转一个位置  Location:  xjkjsalj
        // (2xx  3xx)失败  (4xx[客户端失败] 5xx[服务器完蛋])失败
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().set("Location",loginPage);
        return response.setComplete();
    }

    private String getTempId(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String id = "";
        //1.先看cookie有没u有
        HttpCookie cookie = request.getCookies().getFirst("userTempId");
        if (cookie!=null){
            id = cookie.getValue();
            // id 为空，去请求头中取
            if (StringUtils.isEmpty(id)){
                id = request.getHeaders().getFirst("userTempId");
            }
        } else{

            id = request.getHeaders().getFirst("userTempId");
        }

        return id;
    }
    /**
     * 写出json
     *
     * @param response
     * @param codeEnum
     * @return
     */
    private Mono<Void> writeJson(ServerHttpResponse response, ResultCodeEnum codeEnum) {


        Result<String> result = Result.build("", codeEnum);
        //从response的buffer工厂，拿到响应体的databuffer
        DataBuffer wrap = response.bufferFactory().wrap(Jsons.toStr(result).getBytes(StandardCharsets.UTF_8));
        //指定字符编码
        response.getHeaders().add("content-type","application/json;charset=utf-8");

        return response.writeWith(Mono.just(wrap));
    }

    /**
     * 路径匹配
     *
     * @param patterns
     * @param path
     * @return
     */

    private boolean pathMatch(List<String> patterns, String path) {
        long count = patterns.stream()
                .filter(pattern->pathMatcher.match(pattern,path))
                .count();
        return count > 0;
    }










}
