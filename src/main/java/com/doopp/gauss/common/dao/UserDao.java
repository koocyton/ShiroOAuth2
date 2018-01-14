package com.doopp.gauss.common.dao;

import com.doopp.gauss.common.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/*
 * Created by henry on 2017/7/4.
 */
public interface UserDao {

    void create(User userEntity);

    void update(User userEntity);

    void delete(int id);

    Long count(String where);

    Long count();

    User fetchById(long id);

    List<User> fetchUserFriends(Long id);

    List<User> fetchListByIds(@Param("ids") String ids, @Param("offset") int offset, @Param("limit") int limit);

    User fetchByAccount(String account);

    List<User> get(@Param("offset") int offset, @Param("limit") int limit);

    List<User> get(@Param("where") String where, @Param("offset") int offset, @Param("limit") int limit);
}
