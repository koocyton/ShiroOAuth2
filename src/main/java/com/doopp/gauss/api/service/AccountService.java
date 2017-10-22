package com.doopp.gauss.api.service;

import com.doopp.gauss.api.entity.UserEntity;

public interface AccountService {


    /**
     * 通过账号，密码获取用户信息
     *
     * @param account 用户账号
     * @param password 用户密码
     * @return 用户信息
     * @throws Exception 登录异常
     */
    UserEntity getUserOnLogin(String account, String password) throws Exception;

    /**
     * 注册成功，返回用户信息
     *
     * @param account 用户账号
     * @param password 用户密码
     * @return 用户信息
     * @throws Exception 注册异常，账号或密码不合格
     */
    UserEntity getUserOnRegister(String account, String password) throws Exception;

    /**
     * 通过 access-token 获取用户信息
     *
     * @param accessToken token
     * @return 用户信息
     */
    UserEntity getUserByToken(String accessToken);


    /**
     * 将密码 Hash
     *
     * @param user 用户实体
     * @param newPassword 用户密码
     * @return hash 后的密码
     */
    String hashPassword(UserEntity user, String newPassword);


    /**
     * 将用户注册登录，返回 accessToken
     *
     * @param user 用户
     * @return accessToken
     * @throws Exception 异常
     */
    String registerSession(UserEntity user) throws Exception;
}
