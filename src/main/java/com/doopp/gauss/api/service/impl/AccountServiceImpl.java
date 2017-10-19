package com.doopp.gauss.api.service.impl;

import com.doopp.gauss.api.dao.UserDao;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.AccountService;
import com.doopp.gauss.api.utils.EncryHelper;
import com.doopp.gauss.server.redis.CustomShadedJedis;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

    private final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private CustomShadedJedis sessionRedis;

    @Autowired
    private CustomShadedJedis userRedis;

    @Override
    public UserEntity getUserOnLogin(String account, String password) throws Exception {
        UserEntity user = userDao.fetchByAccount(account);
        if (user==null) {
            throw new Exception("user not found");
        }
        String hashPassword = hashPassword(user, password);
        if (!user.getPassword().equals(hashPassword)) {
            throw new Exception("password is error");
        }
        return user;
    }

    @Override
    public String hashPassword(UserEntity user, String newPassword) {
        return EncryHelper.md5(user.getAccount() + " " + newPassword + " " + user.getSalt());
    }

    @Override
    public String registerSession(UserEntity user) throws Exception {
        // create access token
        OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
        String accessToken = oauthIssuerImpl.accessToken();
        // cache access token
        sessionRedis.set(accessToken, user.getId().toString());
        return accessToken;
    }

    @Override
    @Cacheable(cacheNames = "session", key = "#accessToken" )
    public UserEntity getUserByToken(String accessToken) {
        // get cache by access token
        String userId = sessionRedis.get(accessToken);
        if (userId==null) {
            return null;
        }
        // get user
        Object userObject = userRedis.get(userId.getBytes());
        if (userObject==null) {
            UserEntity user = userDao.fetchById(Long.valueOf(userId));
            userRedis.set(userId.getBytes(), user);
            return user;
        }
        return (UserEntity) userObject;
    }
}
