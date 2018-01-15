package com.doopp.gauss.common.service.impl;

import com.doopp.gauss.common.dao.UserDao;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.service.UserService;
import com.doopp.gauss.common.utils.RSAEncrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Base64;
import java.util.Properties;

@Service("userService")
public class UserServiceImpl implements UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private Properties applicationProperties;

    @Override
    public User getUserBySessionToken(String sessionToken) throws Exception {
        // String publicKey = this.applicationProperties.getProperty("session.rsa.publicKey");
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMknZckLzXEOXIyBIUxpwW25C5cugliBa8aCWtkRt8aU4Wmlo3y76YtOsuhrXdIItV/pyXukANiIiKTXryAmHjotsgUaXCoZ1ELTM5fTafsFABoT6m9SdRbDI9S3ylT1Mspv2tKbXzxzgz+b70x/aLmcvJK6BxtzIjUnIUx8Uc7wIDAQAB";
        sessionToken = "AGiQKqdq0/pmQpP0ca2tWLq6Bp7omVcnOAFCw/f48VZsxwZHh8b6S6IjjM7ts7ztGnK8xGRE0UfT43IjF9s6a9BTnBMLkVtKrWEFZ+r5hghvQWRixSG85MJrYFqauYM+MBiUzLIsGoSvTK6r3Tzq/3Z6iF3j26hIXDkVXLCpEAQ=";
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
