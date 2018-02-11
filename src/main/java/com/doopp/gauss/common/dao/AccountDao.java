package com.doopp.gauss.common.dao;

import com.doopp.gauss.common.entity.Account;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * Created by henry on 2017/7/4.
 */
@Repository("accountDao")
public interface AccountDao {

    @Select("SELECT * FROM `account` WHERE account=#{account} LIMIT 1")
    Account fetchByAccount(String account);

    @Select("SELECT * FROM `account` WHERE id=#{id,jdbcType=BIGINT} LIMIT 1")
    Account fetchById(long id);

    @Select("SELECT * FROM `account` WHERE session_token=#{sessionToken} LIMIT 1")
    Account fetchBySessionToken(String sessionToken);

    @Select("SELECT * FROM `account` WHERE `platform`=#{platform,jdbcType=VARCHAR} AND `platform_uid`=#{platform_uid,jdbcType=VARCHAR} LIMIT 1")
    Account fetchByPlatform(@Param("platform") String platform, @Param("platform_uid") String platform_uid);

    @Select("SELECT count(*) FROM `account` LIMIT 1")
    Long count();

    @Select("SELECT * FROM `account` ORDER BY id DESC LIMIT #{offset,jdbcType=BIGINT}, #{limit,jdbcType=INTEGER}")
    List<Account> fetchList(@Param("offset") Long offset, @Param("limit") int limit);

    @Insert("INSERT INTO `account` (`id`, `platform`, `platform_uid`, `verified`, `account`, `password`, `created_at`) VALUES (${id}, #{platform}, #{platform_uid},  ${verified}, #{account}, #{password}, ${created_at})")
    void create(Account account);

    @Delete("DELETE FROM `account` WHERE id=${id,jdbcType=BIGINT}")
    void delete(int id);

    @Update("UPDATE `account` SET `platform`=#{platform}, `platform_uid`=#{platform_uid}, `verified`=${verified}, `account`=#{account}, `password`=#{password}, `created_at`=#{created_at} WHERE `id`=#{id,jdbcType=BIGINT}")
    void update(Account account);
}

