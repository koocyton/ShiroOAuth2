package com.doopp.gauss.common.service;

import com.doopp.gauss.common.entity.Account;
import com.doopp.gauss.common.entity.User;

public interface AccountService {

    void setCacheUser(User User, String token);
    User getCacheUser(String token);
    User getCacheUser(Long userId);

    /**
     * 通过账号，密码获取用户信息
     *
     * @param account 用户账号
     * @param password 用户密码
     * @return 用户信息
     * @throws Exception 登录异常
     */
    User getUserOnAccountLogin(String account, String password) throws Exception;

    /**
     * 通过账号，密码获取用户信息
     *
     * @param account 用户账号
     * @param password 用户密码
     * @return 用户信息
     * @throws Exception 登录异常
     */
    User getUserOnAccountRegister(String account, String password) throws Exception;

    /**
     * 第三方登陆
     */
    User getUserOnPlatformLogin(String platform, String data) throws Exception;


    /**
     * 从平台信息获取账号信息
     *
     * @param platform 平台
     * @param platformUid 平台的 ID
     * @return 返回账号
     */
    Account getAccountByPlatform(String platform, String platformUid);

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
    User registerPlatformAccount(String platform, String platformUid, String nickName, String country, int gender, String avatarUrl) throws Exception;

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
    User updatePlatformAccount(String platform, String platformUid, String nickName, String country, int gender, String avatarUrl) throws Exception;
}
