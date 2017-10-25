"use strict";

/*
 * token 的操作，校验
 */
let TokenService = function($cookieStore) {
    this.cookieStore = $cookieStore;
};
TokenService.prototype.cacheToken = function(token) {
    this.cookieStore.put("access-token", token);
};
TokenService.prototype.removeToken = function() {
    this.cookieStore.remove("access-token");
};
TokenService.prototype.getToken = function() {
    this.cookieStore.get("access-token");
};
TokenService.prototype.checkToken = function(successCall, errorCall) {
    let accessToken = this.cookieStore.get("access-token");
    if (typeof accessToken!=="string" || accessToken.length<32) {
        errorCall(null);
        return;
    }
    $http({
        method: 'GET',
        url: '/api/v1/user/me',
        headers: {'access-token': accessToken}
    }).then(
        function successCallback(res) {
            successCall(res)
        },
        function errorCallback(res) {
            errorCall(res);
        }
    );
};

let WebSocketService = function(uri) {
    this.wss = /^ws:\/\//.test(uri) ? new WebSocket(uri) : new WebSocket("ws://"+window.location.host+uri);
};
WebSocketService.connect = function(uri) {
    return new WebSocketService(uri);
};
WebSocketService.prototype.onOpen = function(callOpen) {
    if (typeof callOpen==="function") {
        this.wss.onopen = callOpen;
    }
    return this;
};
WebSocketService.prototype.onClose = function(callClose) {
    if (typeof callClose==="function") {
        this.wss.onclose = callClose;
    }
    return this;
};
WebSocketService.prototype.onError = function(callError) {
    if (typeof callError==="function") {
        this.wss.onerror = callError;
    }
    return this;
};
WebSocketService.prototype.onMessage = function(callMessage) {
    if (typeof callMessage==="function") {
        this.wss.onmessage = callMessage;
    }
    return this;
};