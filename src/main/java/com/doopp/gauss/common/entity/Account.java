package com.doopp.gauss.common.entity;

import com.doopp.gauss.common.utils.EncryHelper;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * user entity
 */
@Data
public class Account {

    // 编号
    private Long id;

    // 平台
    private String platform;

    // 平台用户 ID
    private String platform_uid;

    // 账号
    private String account;

    // 密码
    private String password;

    // 密码加盐
    private int verified;

    // 创建时间
    private String created_at;

    // 加密密码
    public String encryptPassword(String password) {
        return EncryHelper.md5(this.account + " " + password);
    }
}
