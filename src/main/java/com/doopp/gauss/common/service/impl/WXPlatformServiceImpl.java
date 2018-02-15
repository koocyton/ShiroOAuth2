package com.doopp.gauss.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.defined.Platform;
import com.doopp.gauss.common.entity.Account;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.service.AccountService;
import com.doopp.gauss.common.service.PlatformService;
import com.doopp.gauss.common.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service("wxPlatformService")
public class WXPlatformServiceImpl implements PlatformService {

    private final static Logger logger = LoggerFactory.getLogger(WXPlatformServiceImpl.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private Properties applicationProperties;

    @Override
    public User login(String loginData) throws Exception {
        // 解 json
        JSONObject wxUserInfo = JSONObject.parseObject(loginData);
        if (wxUserInfo==null || wxUserInfo.getString("code")==null) {
            return null;
        }

        // 请求的 url
        String wxLoginUrl = applicationProperties.getProperty("platform.wx.loginUrl") + wxUserInfo.getString("code");
        String responseBody = CommonUtils.simpleHttpGet(wxLoginUrl);
        JSONObject responseObject = JSONObject.parseObject(responseBody);
        String openid = responseObject.getString("openid");
        if (openid==null) {
            throw new Exception("can not get WeiXin openid");
        }

        String platform_uid = Platform.WX + "_" + openid;
        int gender = wxUserInfo.getInteger("gender");
        String avatarUrl = wxUserInfo.getString("avatarUrl");
        String nickName = wxUserInfo.getString("nickName");
        String country = wxUserInfo.getString("country");

        // 查询账号
        Account account = accountService.getAccountByPlatform(Platform.WX, platform_uid);

        // 返回 User
        return (account==null)
            ? accountService.registerPlatformAccount(Platform.WX, platform_uid, nickName, country, gender, avatarUrl)
            : accountService.updatePlatformAccount(Platform.WX, platform_uid, nickName, country, gender, avatarUrl);


//        AsyncHttpClient client = new AsyncHttpClient();
//        // logger.info(" >> \n" + wxUserInfo + "\n" + wxLoginUrl + "\n" + loginData);
//
//        // 微信登陆
//        Future<User> f = client.prepareGet(wxLoginUrl).execute(new AsyncCompletionHandler<User>() {
//
//            @Override
//            public void onThrowable(Throwable t) {
//                logger.info(" <<< " + t.toString());
//                super.onThrowable(t);
//            }
//
//            @Override
//            public User onCompleted(Response response) throws Exception {
//                JSONObject responseObject = JSONObject.parseObject(response.getResponseBody());
//                String openid = responseObject.getString("openid");
//                if (openid==null) {
//                    throw new Exception("can not get WeiXin openid");
//                }
//
//                String platform_uid = Platform.WX + "_" + openid;
//                int gender = wxUserInfo.getInteger("gender");
//                String avatarUrl = wxUserInfo.getString("avatarUrl");
//                String nickName = wxUserInfo.getString("nickName");
//                String country = wxUserInfo.getString("country");
//
//                // 查询账号
//                Account account = accountService.getAccountByPlatform(Platform.WX, platform_uid);
//
//                // 返回 User
//                return (account==null)
//                    ? accountService.registerPlatformAccount(Platform.WX, platform_uid, nickName, country, gender, avatarUrl)
//                    : accountService.updatePlatformAccount(Platform.WX, platform_uid, nickName, country, gender, avatarUrl);
//            }
//        });
//        try {
//            return f.get();
//        }
//        catch(Exception e) {
//            return null;
//        }
    }
}
