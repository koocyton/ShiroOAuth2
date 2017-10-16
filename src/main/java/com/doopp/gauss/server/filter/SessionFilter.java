package com.doopp.gauss.server.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.doopp.gauss.api.service.RestResponseService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Created by henry on 2017/4/16.
 */
@Component
public class SessionFilter extends OncePerRequestFilter {

    private final static Logger logger = LoggerFactory.getLogger(SessionFilter.class);

    /*
     * 登录验证过滤器
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 不过滤的uri
        String[] notFilters = new String[]{
                // 一般接口
                "/api/v1/register",
                "/api/v1/login",
                "/api/v1/logout",
                // web
                "/web-chat-room",
                "/web-login-form",
                "/chat-room",
                // css , js
                "/webjars",
                "/js",
                "/image",
                "/css",
                "/favicon.ico",
                // db view
                "/d/druid"
        };

        // 请求的uri
        String uri = request.getRequestURI();

        logger.info(" >>> request.getRequestURI() : " + uri);

        // 是否过滤
        boolean filterFlag = true;

        // 如果uri中包含不过滤的uri，则不进行过滤
        for (String notFilter : notFilters) {
            if (uri.contains(notFilter) || uri.equals("/api")) {
                filterFlag = false;
                break;
            }
        }

        // 执行过滤 验证通过的会话
        try {
            if (filterFlag) {
                // 从 header 里拿到 access token
                String accessToken = request.getHeader("access-token");
                // 如果 token 存在，且长度正确
                if (accessToken != null && accessToken.length() >= 32) {
                    // access token cache
                    Cache tokenCache = CacheManager.create().getCache("session-cache");
                    // get user id
                    Long userId = (Long) tokenCache.get("access-token:" + accessToken).getObjectValue();
                    // 如果能找到用户
                    if (userId != null) {
                        // 当前用户
                        request.setAttribute("currentUserId", userId);
                        // 往下执行
                        filterChain.doFilter(request, response);
                        return;
                    }
                    // 如果不能找到用户
                    else {
                        RestResponseService.writeErrorResponse(response, "Session failed");
                        return;
                    }
                }
                // 如果 token 不对
                else {
                    RestResponseService.writeErrorResponse(response, "Session failed");
                    return;
                }
            }
            // 不用校验
            filterChain.doFilter(request, response);
        }
        catch (Exception e) {
            e.printStackTrace();
            RestResponseService.writeErrorResponse(response, e.getMessage());
        }
    }
}