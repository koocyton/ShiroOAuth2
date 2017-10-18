package com.doopp.gauss.api.service.impl;

import com.doopp.gauss.api.dao.UserDao;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.UserService;
import com.doopp.gauss.server.redis.CustomShadedJedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by henry on 2017/7/11.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    private final CustomShadedJedis sessionRedis;

    @Autowired
    public UserServiceImpl(UserDao userDao, CustomShadedJedis sessionRedis) {
        this.userDao = userDao;
        this.sessionRedis = sessionRedis;
    }

    @Override
    public UserEntity getUserByToken(String accessToken){
        return (UserEntity)sessionRedis.get(accessToken.getBytes());
        // logger.info(">>>" + accessToken);
        // return redisSessionHelper.getUserByToken(accessToken);
    }

    @Override
    public UserEntity getUserInfo(Long userId) {
        return userDao.fetchById(userId);
    }

    @Override
    public UserEntity getUserInfo(String account) {
        return userDao.fetchByAccount(account);
    }

    @Override
    public List<UserEntity> getUserFriendList(Long userId) {
        UserEntity userEntity = userDao.fetchById(userId);
        if (userEntity!=null) {
            return userDao.fetchListByIds(userEntity.getFriends(), 0, 30);
        }
        return null;
    }

    @Override
    public boolean applyFriend(UserEntity userEntity, Long userId) {
        return false;
    }

    @Override
    public boolean acceptFriend(UserEntity userEntity, Long userId) {
        userEntity.addFriend(userId);
        userDao.update(userEntity);
        return true;
    }

    @Override
    public boolean rejectFriend(UserEntity userEntity, Long userId) {
        return false;
    }

    @Override
    public boolean cancelFriend(UserEntity userEntity, Long userId) {
        userEntity.delFriend(userId);
        userDao.update(userEntity);
        return true;
    }
}
