package com.doopp.gauss.common.service;

import com.doopp.gauss.common.entity.User;

public interface PlatformService {

    User login(String loginData) throws Exception;
}
