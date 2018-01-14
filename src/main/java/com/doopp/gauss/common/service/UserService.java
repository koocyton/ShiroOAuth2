package com.doopp.gauss.common.service;

import com.doopp.gauss.common.entity.User;

public interface UserService {

    User getUserBySessionToken(String sessionToken) throws Exception;
}
