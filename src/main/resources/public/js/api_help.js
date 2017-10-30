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

/*
 * 长链接
 */
let WebSocketService = function(uri) {
    this.ws = /^ws:\/\//.test(uri)
        ? new WebSocket(uri)
        : new WebSocket("ws://"+window.location.host+uri);
};
WebSocketService.connect = function(uri) {
    return new WebSocketService(uri);
};
WebSocketService.prototype.onOpen = function(callOpen) {
    if (typeof callOpen==="function") {
        this.ws.onopen = callOpen;
    }
    return this;
};
WebSocketService.prototype.onClose = function(callClose) {
    if (typeof callClose==="function") {
        this.ws.onclose = callClose;
    }
    return this;
};
WebSocketService.prototype.onError = function(callError) {
    if (typeof callError==="function") {
        this.ws.onerror = callError;
    }
    return this;
};
WebSocketService.prototype.onMessage = function(callMessage) {
    if (typeof callMessage==="function") {
        this.ws.onmessage = callMessage;
    }
    return this;
};
WebSocketService.prototype.send = function(message) {
    this.ws.send(message);
};
WebSocketService.prototype.close = function() {
    try {
        this.ws.close();
    }
    catch(e) {

    }
};

/*
 * http request
 */
let httpGet = function($http, url, successCall, errorCall, headers)
{
    if (typeof headers!=="object") {
        headers = {};
    }
    $http({
        method : 'GET',
        url : url,
        headers : headers
    }).then(
        function successCallback(res) {
            if (typeof successCall === "function") {
                successCall(res);
            }
        },
        function errorCallback(res) {
            if (typeof errorCall === "function") {
                errorCall(res);
            }
        }
    );
};

let formPost = function($http, url, queryData, successCall, errorCall, headers)
{
    let queryString = "";
    if (typeof queryData==="object") {
        for(let idx in queryData) {
            queryString += (queryString==="") ? "" : "&";
            let key = "" + idx;
            queryString += encodeURIComponent(key) + "=" + encodeURIComponent(queryData[key]);
        }
    }
    else {
        queryString = "" + queryData;
    }
    if (typeof headers==="object") {
        headers["Content-Type"] = "application/x-www-form-urlencoded";
    }
    else {
        headers = {"Content-Type" : "application/x-www-form-urlencoded"}
    }
    $http({
        method : 'POST',
        url : url,
        data : queryString,
        headers : headers
    }).then(
        function successCallback(res) {
            console.log("successCall : \n      >>> " + res);
            if (typeof successCall === "function") {
                successCall(res);
            }
        },
        function errorCallback(res) {
            console.log("errorCall : \n      >>> " + res);
            if (typeof errorCall === "function") {
                errorCall(res);
            }
        }
    );
};

let ApiTestController = function($scope, $http) {

    $scope.registerData = {account:"k0001@doopp.com", password:"123456", nickName:"helloBoy"};
    $scope.loginData = {account:"k0001@doopp.com", password:"123456"};

    $scope.room1 = {
        action : "createRoom",
        accessToken : "",
        createMessage : {action:"createRoom", data:{roomName:"美丽的小屋1"}},
        joinMessage : {action:"joinRoom", data:{roomId:"54613"}},
        ws : null,
        socketStatus : "未连接",
        sendMessage : "",
        receivedMessage : ""
    };

    $scope.room2 = {
        action : "joinRoom",
        accessToken : "",
        createMessage : {action:"createRoom", data:{roomName:"美丽的小屋2"}},
        joinMessage : {action:"joinRoom", data:{roomId:"54614"}},
        ws : null,
        socketStatus : "未连接",
        sendMessage : "",
        receivedMessage : ""
    };

    $scope.room3 = {
        action : "joinRoom",
        accessToken : "",
        createMessage : {action:"createRoom", data:{roomName:"美丽的小屋3"}},
        joinMessage : {action:"joinRoom", data:{roomId:"54615"}},
        ws : null,
        socketStatus : "未连接",
        sendMessage : "",
        receivedMessage : ""
    };

    $scope.apiRequestMessage = [];

    $scope.scrollWindow=function(){
        let _el = document.getElementById('request_history');
        _el.scrollTop = _el.scrollHeight;
    };

    let onSuccess = function(res) {
        let ii = $scope.apiRequestMessage.length;
        $scope.apiRequestMessage[ii] = {
            url : '[' + res.config.method+ '] ' + res.config.url,
            request : res.config.data,
            response : res.data,
            error : null
        };
        $scope.scrollWindow();
    };

    let onError = function(res) {
        let ii = $scope.apiRequestMessage.length;
        $scope.apiRequestMessage[ii] = {
            url : '',
            request : "",
            response : '',
            error : decodeURI(res)
        };
        $scope.scrollWindow();
    };

    let onSocketSuccess = function(e) {
        let ii = $scope.apiRequestMessage.length;
        $scope.apiRequestMessage[ii] = {
            requestUrl : '[GET] websocket',
            request : e.message,
            response : null,
            errorResponse : null
        };
        $scope.scrollWindow();
    };

    let onSocketError = function(e) {
        let ii = $scope.apiRequestMessage.length;
        $scope.apiRequestMessage[ii] = {
            url : '',
            request : "",
            response : '',
            error : decodeURI(res)
        };
        $scope.scrollWindow();
    };

    $scope.apiRegister = function() {
        formPost($http, '/api/v1/register', $scope.registerData, onSuccess, onError);
    };

    $scope.apiLogin = function() {
        formPost($http, '/api/v1/login', $scope.loginData, onSuccess, onError);
    };

    $scope.apiMeInfo = function() {
        httpGet($http, '/api/v1/user/me', onSuccess, onError, {"access-token": $scope.meInfoAccessToken});
    };

    $scope.apiRoomList = function() {
        httpGet($http, '/api/v1/room/list', onSuccess, onError, {"access-token": $scope.roomListAccessToken});
    };

    $scope.disconnectRoom = function(ii) {
        let scopeRoom = eval("$scope.room" + ii);
        if (scopeRoom.ws!==null) {
            scopeRoom.ws.close();
            scopeRoom.ws = null;
        }
    };

    $scope.connectRoom = function(ii) {
        let scopeRoom = eval("$scope.room" + ii);
        if (scopeRoom.ws===null) {
            //
            scopeRoom.ws = WebSocketService
                .connect("/room-socket?access-token=" + scopeRoom.accessToken)
                .onClose(function (e) {
                    scopeRoom.socketStatus = "断开";
                    console.log("Disconnected: " + e.reason);
                    $scope.scrollWindow();
                })
                .onMessage(onSocketSuccess);
            //
            if (scopeRoom.action==="createRoom") {
                scopeRoom.ws.onOpen(function (e) {
                    scopeRoom.socketStatus = "连接";
                    scopeRoom.ws.send(angular.toJson(scopeRoom.createMessage));
                })
            }
            else if (scopeRoom.action==="joinRoom") {
                scopeRoom.ws.onOpen(function (e) {
                    scopeRoom.socketStatus = "连接";
                    scopeRoom.ws.send(angular.toJson(scopeRoom.joinMessage));
                })
            }
        }
    };
};

let apiHelpApp = angular.module('ngApiHelpApp',[]);
apiHelpApp.controller('ApiTestController', ApiTestController);
