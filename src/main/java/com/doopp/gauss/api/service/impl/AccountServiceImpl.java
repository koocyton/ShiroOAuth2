package com.doopp.gauss.api.service.impl;

import com.doopp.gauss.api.dao.UserDao;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.AccountService;
import com.doopp.gauss.api.utils.EncryHelper;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

    private final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private Ehcache sessionCache;

    @Override
    public UserEntity getUserOnLogin(String account, String password) throws Exception {
        UserEntity user = userDao.fetchByAccount(account);
        if (user==null) {
            throw new Exception("user not found");
        }
        String hashPassword = hashPassword(user, password);
        if (user.getPassword().equals(hashPassword)) {
            throw new Exception("password is error");
        }
        return user;
    }

    @Override
    public String hashPassword(UserEntity user, String newPassword) {
        return EncryHelper.md5(user.getAccount() + " newPassword " + user.getSalt());
    }

    @Override
    public String registerSession(UserEntity user) throws Exception {
        // create access token
        OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
        String accessToken = oauthIssuerImpl.accessToken();
        // cache access token
        logger.info(" >> " + sessionCache);
        //sessionCache.put(accessToken, user.getId());
        //int userId = (int) sessionCache.get(accessToken).get();
        //if (userId==user.getId()) {
        //    throw new Exception("server can not login");
        //}
        return accessToken;
    }
}
