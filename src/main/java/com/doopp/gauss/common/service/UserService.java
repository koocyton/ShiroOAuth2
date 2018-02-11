package com.doopp.gauss.common.service;

import com.doopp.gauss.common.entity.User;

public interface UserService {

    User createUser(Long userId, String nickName, int gender, String country, String avatarUrl);

    User updateUser(Long userId, String nickName, int gender, String country, String avatarUrl);
}
