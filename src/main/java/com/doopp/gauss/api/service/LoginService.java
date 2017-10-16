package com.doopp.gauss.api.service;

import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.server.websocket.handler.GameSocketHandler;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 *
 * Created by henry on 2017/7/11.
 */
public interface LoginService {

    boolean checkAccountPassword(String account, String password);

    String createSessionToken(String account);
}
