package com.doopp.gauss.common.dao;

import com.doopp.gauss.common.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * Created by henry on 2017/7/4.
 */
@Repository("userDao")
public interface UserDao {

    @Select("SELECT * FROM `user` WHERE account=#{account} LIMIT 1")
    User fetchByAccount(String account);

    @Select("SELECT * FROM `user` WHERE id=#{id,jdbcType=BIGINT} LIMIT 1")
    User fetchById(long id);

    // User fetchByPlatform(@Param("platform") String platform, @Param("platform_uid") String platform_uid);

    @Select("SELECT count(*) FROM `user` LIMIT 1")
    Long count();

    @Select("SELECT * FROM `user` ORDER BY id DESC LIMIT #{offset,jdbcType=BIGINT}, #{limit,jdbcType=INTEGER}")
    List<User> fetchList(@Param("offset") Long offset, @Param("limit") int limit);

    @Insert("INSERT INTO `user` (`id`, `nickname`, `gender`, `avatar_url`, `country`, `friends`, `score`, `ranking`, `gold`) VALUES (${id}, #{nickname}, ${gender},  #{avatar_url}, #{country}, #{friends}, ${score}, ${ranking}, ${gold})")
    void create(User user);

    @Delete("DELETE FROM `user` WHERE id=${id,jdbcType=BIGINT}")
    void delete(int id);

    @Update("UPDATE `user` SET `nickname`=#{nickname}, `country`=#{country}, `gender`=${gender}, `avatar_url`=#{avatar_url}, `friends`=#{friends}, `score`=${score}, `ranking`=${ranking}, `gold`=${gold} WHERE `id`=#{id,jdbcType=BIGINT}")
    void update(User user);
}

