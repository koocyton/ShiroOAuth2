package com.doopp.gauss.api.service.impl;

import com.doopp.gauss.api.dao.UserDao;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.AccountService;
import com.doopp.gauss.api.utils.EncryHelper;
import com.doopp.gauss.api.utils.IdWorker;
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

    private final static IdWorker idWorker = new IdWorker(1, 1);

    /**
     * 通过账号，密码获取用户信息
     *
     * @param account 用户账号
     * @param password 用户密码
     * @return 用户信息
     * @throws Exception 登录异常
     */
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

    /**
     * 注册成功，返回用户信息
     *
     * @param account 用户账号
     * @param password 用户密码
     * @return 用户信息
     * @throws Exception 注册异常，账号或密码不合格
     */
    @Override
    public UserEntity registerThenGetUser(String account, String password) throws Exception
    {
        // 当前时间
        int currentTime = (int)(System.currentTimeMillis() / 1000);
        // 密码混淆的值
        String salt = EncryHelper.md5(String.valueOf(currentTime));
        // 用户实体
        UserEntity user = new UserEntity();
        user.setId(idWorker.nextId());
        user.setAccount(account);
        user.setSalt(salt);
        user.setPassword(this.hashPassword(user, password));
        user.setCreated_at(currentTime);
        user.setNickname("");
        user.setPortrait("");
        user.setFriends("");
        // 保存用户
        userDao.create(user);
        //
        return user;
    }

    /**
     * 将密码 Hash
     *
     * @param user 用户实体
     * @param newPassword 用户密码
     * @return hash 后的密码
     */
    @Override
    public String hashPassword(UserEntity user, String newPassword) {
        return EncryHelper.md5(user.getAccount() + " " + newPassword + " " + user.getSalt());
    }

    /**
     * 将用户注册登录，返回 accessToken
     *
     * @param user 用户
     * @return accessToken
     * @throws Exception 异常
     */
    @Override
    public String registerSession(UserEntity user) throws Exception {
        // create access token
        OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
        String accessToken = oauthIssuerImpl.accessToken();
        // clean last token cache
        String lastToken = sessionRedis.get(user.getId().toString());
        if (lastToken!=null) {
            sessionRedis.del(lastToken);
        }
        // cache access token
        sessionRedis.set(accessToken, user.getId().toString());
        sessionRedis.set(user.getId().toString(), accessToken);
        return accessToken;
    }

    /**
     * 通过 access-token 获取用户信息
     *
     * @param accessToken token
     * @return 用户信息
     */
    @Override
    // @Cacheable(cacheNames = "session", key = "#accessToken" )
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
