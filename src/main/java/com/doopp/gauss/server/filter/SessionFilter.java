package com.doopp.gauss.server.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.service.AccountService;
import com.doopp.gauss.common.utils.ApplicationContextUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/*
 * Created by henry on 2017/4/16.
 */
@Component
public class SessionFilter extends OncePerRequestFilter {

    // private final Logger logger = LoggerFactory.getLogger(SessionFilter.class);

    /*
     * 登录验证过滤器
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // get bean
        AccountService accountService = (AccountService) ApplicationContextUtil.getBean("accountService");

        // 不过滤的uri
        String[] notFilters = new String[]{

                // api
                "/api/platform-login",
                "/api/register",
                "/api/login",
                "/api/logout",

                // static
                "/js",
                "/image",
                "/css",
                "/favicon.ico",
                "/webjars",

                // helper
                "/helper",
                "/druid"
        };

        // 请求的uri
        String uri = request.getRequestURI();

        // logger.info(" >>> request uri : " + uri);

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
                String sessionToken = request.getHeader("session-token");

                // 从 url query 里获取 access token
                if (sessionToken == null) {
                    sessionToken = request.getParameter("session-token");
                }

                // 如果 token 存在，反解 token
                if (sessionToken != null) {
                    User user = accountService.getCacheUser(sessionToken);
                    // 如果能找到用户
                    if (user != null) {
                        // 如果是 websocket 传 user id
                        if (request.getHeader("Upgrade").equals("websocket")) {
                            request.setAttribute("userId", user.getId());
                            filterChain.doFilter(request, response);
                            return;
                        }
                        // http 请求，传 user 对象
                        else {
                            request.setAttribute("sessionUser", user);
                            filterChain.doFilter(request, response);
                            return;
                        }
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
        } catch (Exception e) {
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

    //    private static void displayPortal(HttpServletRequest request, HttpServletResponse response, Manager manager) throws Exception {
    //        // assign
    //        Map<String, Object> model = new HashMap<>();
    //        model.put("manager", manager);
    //
    //        // get bean
    //        ServletContext servletContext = request.getServletContext();
    //        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
    //        FreeMarkerConfigurer freeMarkerConfigurer = (FreeMarkerConfigurer) ctx.getBean("freeMarkerConfigurer");
    //
    //        // template
    //        String data = FreeMarkerTemplateUtils.processTemplateIntoString(
    //                freeMarkerConfigurer.getConfiguration().getTemplate("backend/portal.html"), model);
    //        response.setCharacterEncoding("UTF-8");
    //        response.setContentType("text/html; charset=UTF-8");
    //        PrintWriter out = response.getWriter();
    //        out.write(data);
    //    }
}