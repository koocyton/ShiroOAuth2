package com.doopp.gauss.common.service.impl;

import com.doopp.gauss.common.dao.AccountDao;
import com.doopp.gauss.common.dao.UserDao;
import com.doopp.gauss.common.defined.Platform;
import com.doopp.gauss.common.entity.Account;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.service.AccountService;
import com.doopp.gauss.common.service.PlatformService;
import com.doopp.gauss.common.service.UserService;
import com.doopp.gauss.common.utils.IdWorker;
import com.doopp.gauss.server.redis.CustomShadedJedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("accountService")
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountDao accountDao;

    @Resource
    private UserDao userDao;

    @Autowired
    private PlatformService wxPlatformService;

    @Autowired
    private UserService userService;

    @Autowired
    private IdWorker userIdWorker;

    @Autowired
    private CustomShadedJedis sessionRedis;

    @Override
    public void setCacheUser(User user, String token) {
        sessionRedis.set(token.getBytes(), user.getId());
        sessionRedis.set(String.valueOf(user.getId()).getBytes(), user);
    }

    @Override
    public User getCacheUser(String token) {
        Long userId = (Long) sessionRedis.get(token.getBytes());
        return (User) sessionRedis.get(String.valueOf(userId).getBytes());
    }

    @Override
    public User getCacheUser(Long userId) {
        return (User) sessionRedis.get(String.valueOf(userId).getBytes());
    }

    /**
     * 通过账号，密码获取用户信息
     *
     * @param email 用户账号
     * @param password 用户密码
     * @return 用户信息
     * @throws Exception 登录异常
     */
    @Override
    public User getUserOnAccountLogin(String email, String password) throws Exception {
        return this.getUserOnAccountRegister(email, password);
    }

    /**
     * 通过账号，密码获取用户信息
     *
     * @param email 用户账号
     * @param password 用户密码
     * @return 用户信息
     * @throws Exception 登录异常
     */
    @Override
    public User getUserOnAccountRegister(String email, String password) throws Exception {
        Account account = accountDao.fetchByAccount(email);
        if (account==null) {
            Long userId = userIdWorker.nextId();
            account = this.createAccount(email, password, Platform.LC, Platform.LC + "_" + userId);
        }
        User user = userDao.fetchById(account.getId());
        if (user==null) {
            user = userService.createUser(account.getId(), "", 1, "China", "");
        }
        return user;
    }

    /**
     * 第三方登陆
     *
     * @param platform 平台
     * @param data 平台返回的信息
     * @return User
     */
    @Override
    public User getUserOnPlatformLogin(String platform, String data) throws Exception {
        switch (platform) {
            case Platform.WX:
                return wxPlatformService.login(data);
        }
        return null;
    }

    @Override
    public Account getAccountByPlatform(String platform, String platformUid) {
        return accountDao.fetchByPlatform(platform, platformUid);
    }

    /**
     * 注册成功，返回用户信息
     *
     * @param platform 平台
     * @param platformUid 平台 openid
     * @param nickName 昵称
     * @param gender 性别
     * @param avatarUrl 头像
     * @return 用户信息
     * @throws Exception 注册异常，账号或密码不合格
     */
    @Override
    public User registerPlatformAccount(String platform, String platformUid, String nickName, String country, int gender, String avatarUrl) throws Exception
    {
        Account account = this.createAccount(platformUid, platformUid, platform, platformUid);
        // 不能创建新账号
        if (account==null) {
            return null;
        }
        // 创建新用户
        return userService.createUser(account.getId(), nickName, gender, country, avatarUrl);
    }

    /**
     * 更新成功，返回用户信息
     *
     * @param platform 平台
     * @param platformUid 平台 openid
     * @param nickName 昵称
     * @param gender 性别
     * @param avatarUrl 头像
     * @return 用户信息
     * @throws Exception 注册异常，账号或密码不合格
     */
    @Override
    public User updatePlatformAccount(String platform, String platformUid, String nickName, String country, int gender, String avatarUrl) throws Exception
    {
        Account account = this.updateAccount(platformUid, platformUid, platform, platformUid);
        // 不能创建新账号
        if (account==null) {
            return null;
        }
        // 创建新用户
        return userService.updateUser(account.getId(), nickName, gender, country, avatarUrl);
    }

    private Account createAccount(String email, String password, String platform, String platform_uid) {
        Account account = new Account();
        account.setId(userIdWorker.nextId());
        account.setPlatform(platform);
        account.setPlatform_uid(platform_uid);
        account.setAccount(email);
        account.setPassword(platform.equals(Platform.LC) ? account.encryptPassword(password) : "");
        account.setCreated_at("CURRENT_TIMESTAMP");
        // local 的账号，默认邮箱没有验证通过
        account.setVerified(platform.equals(Platform.LC) ? 0 : 1);
        accountDao.create(account);
        return account;
    }



    private Account updateAccount(String email, String password, String platform, String platformUid) {
        return accountDao.fetchByPlatform(platform, platformUid);
    }
}
