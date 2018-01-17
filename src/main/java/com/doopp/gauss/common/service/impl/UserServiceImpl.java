package com.doopp.gauss.common.service.impl;

import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.service.UserService;
import com.doopp.gauss.common.utils.RSAEncrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Properties;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private Properties applicationProperties;

    @Override
    public User getUserBySessionToken(String sessionToken) throws Exception {
        String publicKey = this.applicationProperties.getProperty("session.rsa.publicKey");
        byte[] decryptData = RSAEncrypt.decryptByPublicKey(publicKey, Base64.getDecoder().decode(sessionToken));
        if (decryptData==null) {
            throw new Exception("session token is failed");
        }
        String[] decryptInfo = (new String(decryptData)).split(" ");
        if (decryptInfo.length==0) {
            throw new Exception("session token is failed");
        }
        return new User() {{
            setId(Long.valueOf(decryptInfo[0]));
            setNickName(decryptInfo[1].equals("") ? "" : decryptInfo[1]);
            setGender(1);
            setCountry(decryptInfo[3].equals("") ? "" : decryptInfo[3]);
            setLoginTime(Long.valueOf(decryptInfo[4]));
        }};
    }
}
