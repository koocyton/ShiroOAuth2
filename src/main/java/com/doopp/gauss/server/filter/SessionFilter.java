package com.doopp.gauss.server.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.doopp.gauss.api.Exception.EmptyException;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.AccountService;
import com.doopp.gauss.server.redis.CustomShadedJedis;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Created by henry on 2017/4/16.
 */
@Component
public class SessionFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(SessionFilter.class);

    // private final RedisSessionHelper redisSessionHelper = new RedisSessionHelper();

    //@Autowired
    //private ShardedJedis sessionRedis;

    /*
     * 登录验证过滤器
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException {

        // get bean
        ServletContext servletContext = request.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        AccountService accountService = (AccountService) ctx.getBean("accountService");

        // 不过滤的uri
        String[] notFilters = new String[] {
            "/api/v1/register",
            "/api/v1/login",
            "/api/v1/fast-login",
            "/api/v1/logout",
            "/demo",
            "/api-help",
            "/js",
            "/image",
            "/css",
            "/favicon.ico",
            "/webjars",
            "/d/druid"
        };

        // 请求的uri
        String uri = request.getRequestURI();

        logger.info(" >>> request uri : " + uri);

        // 是否过滤
        boolean doFilter = true;

        // 如果uri中包含不过滤的uri，则不进行过滤
        for (String notFilter : notFilters) {
            if (uri.contains(notFilter) || uri.equals("/api")) {
                doFilter = false;
                break;
            }
        }

        // 执行过滤 验证通过的会话
        try {
            if (doFilter) {
                // 从 header 里拿到 access token
                String accessToken = request.getHeader("access-token");

                // 从 url query 里获取 access token
                if (accessToken==null) {
                    accessToken = request.getParameter("access-token");
                }

                // 如果 token 存在，且长度正确
                if (accessToken!=null && accessToken.length()>=32) {
                    UserEntity user = accountService.getUserByToken(accessToken);
                    // 如果能找到用户
                    if (user!=null) {
                        request.setAttribute("currentUser", user);
                        filterChain.doFilter(request, response);
                        return;
                    }
                    // 如果不能找到用户
                    else {
                        writeErrorResponse(404, response, "not found user");
                        return;
                    }
                }
                // 如果 token 不对
                else {
                    writeErrorResponse(501, response, "token failed");
                    return;
                }
            }
            // 不用校验
            filterChain.doFilter(request, response);
        }
        catch(Exception e) {
            // logger.info(" >>> e.getMessage : " +  e.getMessage());
            e.printStackTrace();
            writeErrorResponse(501, response, e.getMessage());
        }
    }

    private static void writeErrorResponse(int errorCode, HttpServletResponse response, String message) throws IOException {
        response.setStatus(errorCode);
        String data = "{\"errcode\":" + errorCode + ", \"errmsg\":\"" + message + "\"}";
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(data);
    }
}