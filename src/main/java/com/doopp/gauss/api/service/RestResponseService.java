package com.doopp.gauss.api.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 返回的 JSON Service 处理
 *
 * Created by Henry on 2017/8/19.
 */
public interface RestResponseService {

    JSONObject error(HttpServletResponse response, int errorCode, String errorMessage);

    JSONObject error(int errorCode, String errorMessage);

    JSONObject helper(String message, String docUrl);

    JSONObject success();

    JSONObject data(Object data);

    JSONObject loginSuccess(String accessToken);
}
