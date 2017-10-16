package com.doopp.gauss.api.service.impl;

import com.doopp.gauss.api.dao.UserDao;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.utils.RedisSessionHelper;
import com.doopp.gauss.api.service.LoginService;
import com.doopp.gauss.api.utils.EncryHelper;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.UserSessionRegistryAdapter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * Created by henry on 2017/7/11.
 */
@Service("loginService")
public class LoginServiceImpl implements LoginService {

    private final UserDao userDao;

    @Autowired
    public LoginServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean checkAccountPassword(String account, String password) {
        UserEntity userEntity = userDao.fetchByAccount(account);
        Object
    }

    @Override
    public String createSessionToken(UserEntity userEntity) {
        try {
            OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
            String accessToken = oauthIssuerImpl.accessToken();

            UserEntity currentUser = userDao.fetchByAccount(account);
            redisSessionHelper.setUserByToken(accessToken, currentUser);
            // 断开这个用户的长链接
            messageService.disconnectSocket(currentUser.getId());
            messageService.sendStringToUser(currentUser.getAccount() + " 重登录，连接被重置", currentUser.getId());
            return accessToken;
        }
        catch (Exception e) {
            return null;
        }
    }
}
