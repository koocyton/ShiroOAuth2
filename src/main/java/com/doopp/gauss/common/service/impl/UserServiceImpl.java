package com.doopp.gauss.common.service.impl;

import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.service.UserService;
import com.doopp.gauss.common.utils.RSAEncrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service("userService")
public class UserServiceImpl implements UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private static Properties applicationProperties;

    @Override
    public User getUserBySessionToken(String sessionToken) throws Exception {
        String publicKey = applicationProperties.getProperty("session.rsa.publicKey");
        byte[] decryptData = RSAEncrypt.decryptByPublicKey(publicKey, sessionToken.getBytes());
        if (decryptData==null) {
            throw new Exception("session token is failed");
        }
        String[] decryptInfo = new String(decryptData).split(" ");
        logger.info(" >>> " + decryptInfo);
        return new User();
    }
}
