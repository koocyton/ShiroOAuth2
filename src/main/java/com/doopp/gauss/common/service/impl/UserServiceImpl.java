package com.doopp.gauss.common.service.impl;

import com.doopp.gauss.common.dao.UserDao;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User updateUser(Long userId, String nickName, int gender, String country, String avatarUrl) {
        User user = userDao.fetchById(userId);
        if (user!=null) {
            user.setCountry("CN");
            user.setNickname(nickName);
            user.setGender(gender);
            user.setAvatar_url(avatarUrl);
            userDao.update(user);
            return user;
        }
        else {
            return this.createUser(userId, nickName, gender, country, avatarUrl);
        }
    }

    @Override
    public User createUser(Long userId, String nickName, int gender, String country, String avatarUrl) {
        User user = new User();
        user.setId(userId);
        user.setCountry("CN");
        user.setNickname(nickName);
        user.setGender(gender);
        user.setAvatar_url(avatarUrl);
        user.setFriends("");
        user.setGold(0);
        user.setRanking(0);
        user.setScore(0);
        userDao.create(user);
        return user;
    }
}
