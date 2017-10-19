package com.doopp.gauss.api.service;

import com.doopp.gauss.api.entity.UserEntity;

public interface AccountService {

    UserEntity getUserOnLogin(String account, String password) throws Exception;

    UserEntity getUserByToken(String accessToken);

    String hashPassword(UserEntity user, String newPassword);

    String registerSession(UserEntity user) throws Exception;
}
