package com.doopp.gauss.common.utils;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CommonUtils {

    /**
     * Returns 是否是移动客户端
     *
     * @param request Http Servlet Request
     * @return true or false
     */
    public static boolean isMobileClient(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        String[] agent = {"Android", "iPhone", "iPod", "iPad", "Windows Phone", "MQQBrowser"};
        boolean flag = false;
        if (!ua.contains("Windows NT") || (ua.contains("Windows NT") && ua.contains("compatible; MSIE 9.0;"))) {
            // 排除 苹果桌面系统
            if (!ua.contains("Windows NT") && !ua.contains("Macintosh")) {
                for (String item : agent) {
                    if (ua.contains(item)) {
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * Returns 客户端的 IP
     *
     * @param request Http Servlet Request
     * @return true or false
     */
    public static String clientIp(HttpServletRequest request)
    {
        String ip = request.getHeader("X-Forwarded-For");
        //
        if (ip!=null && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (ip!=null && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    public static Class<?> Map;<K, V> getMaxValueKey(final Map<K, V> map) {
        if (map == null) return null;
        int ii = 0;
        Integer maxValue = 0;
        Long maxValueKey = null;
        for(K key : map.keySet()) {
            maxValue = (maxValueKey==null) ? map.get(playerId) : 0;
            maxValueKey = (maxValueKey==null) ? playerId : null;
            if (map.get(playerId)>=maxValue) {
                maxValue = map.get(playerId);
                maxValueKey = playerId;
            }
        }
        return maxValueKey;
    }
}
