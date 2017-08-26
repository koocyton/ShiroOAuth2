package com.doopp.gauss.api.utils;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 自定义的 filter
 * Created by henry on 2017/7/6.
 */
public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

    private static final Logger LOG = LoggerFactory.getLogger(CustomFormAuthenticationFilter.class);

    @Override
    protected boolean isLoginSubmission(ServletRequest request, ServletResponse response) {
        LOG.info(" >>>>> isLoginSubmission");
        return (request instanceof HttpServletRequest) && WebUtils.toHttp(request).getMethod().equalsIgnoreCase(POST_METHOD);
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
                                     ServletRequest request, ServletResponse response) throws Exception {
        LOG.info(" >>>>> onLoginSuccess");
        issueSuccessRedirect(request, response);
        //we handled the success redirect directly, prevent the chain from continuing:
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        LOG.info(" >>>>> onAccessDenied");
        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                if (LOG.isTraceEnabled()) {
                    LOG.info(" >>>>> Login submission detected");
                    LOG.trace("Login submission detected.  Attempting to execute login.");
                }
                return executeLogin(request, response);
            } else {
                if (LOG.isTraceEnabled()) {
                    LOG.info(" >>>>> Login page view");
                    LOG.trace("Login page view.");
                }
                //allow them to see the login page ;)
                return true;
            }
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.info(" >>>>> Attempting to access a path");
                LOG.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
                        "Authentication url [" + getLoginUrl() + "]");
            }

            LOG.info(" >>>>> ...");
            saveRequestAndRedirectToLogin(request, response);
            return false;
        }
    }
}
