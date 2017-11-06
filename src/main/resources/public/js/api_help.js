"use strict";

let ApiTestController = function($scope, $http) {

    $scope.registerData = {account:"k0001@doopp.com", password:"123456", nickName:"helloBoy"};
    $scope.loginData = {account:"k0001@doopp.com", password:"123456"};

    $scope.room1 = {
        action : "createRoom",
        accessToken : "",
        createMessage : {action:"createRoom", roomName:"美丽的小屋1"},
        joinMessage : {action:"joinRoom", roomId:"54613"},
        ws : null,
        socketStatus : "未连接",
        sendMessage : "",
        receivedMessage : ""
    };

    $scope.room2 = {
        action : "joinRoom",
        accessToken : "",
        createMessage : {action:"createRoom", roomName:"美丽的小屋2"},
        joinMessage : {action:"joinRoom", roomId:"54613"},
        ws : null,
        socketStatus : "未连接",
        sendMessage : "",
        receivedMessage : ""
    };

    $scope.room3 = {
        action : "joinRoom",
        accessToken : "",
        createMessage : {action:"createRoom", roomName:"美丽的小屋3"},
        joinMessage : {action:"joinRoom", roomId:"54613"},
        ws : null,
        socketStatus : "未连接",
        sendMessage : "",
        receivedMessage : ""
    };

    $scope.apiRequestMessage = [];

    $scope.scrollWindow=function() {
        setTimeout(function() {
            let _el = document.getElementById('request_history');
            _el.scrollTop = _el.scrollHeight;
        }, 1);
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

    $scope.meInfoAccessToken = "";
    $scope.apiMeInfo = function() {
        httpGet($http, '/api/v1/user/me', onSuccess, onError, {"access-token": $scope.meInfoAccessToken});
    };

    $scope.roomListAccessToken = "";
    $scope.apiRoomList = function() {
        httpGet($http, '/api/v1/room/list', onSuccess, onError, {"access-token": $scope.roomListAccessToken});
    };

    $scope.disconnectRoom = function(roomName) {
        let scopeRoom = eval("$scope." + roomName);
        if (scopeRoom.ws!==null) {
            scopeRoom.ws.close();
            scopeRoom.ws = null;
        }
    };

    $scope.sendMessage = function(roomName, message) {
        let scopeRoom = eval("$scope." + roomName);
        if (scopeRoom.ws!==null) {
            scopeRoom.ws.send(message);
        }
    };

    $scope.callPlayer = function(roomName, gameType) {
        let scopeRoom = eval("$scope." + roomName);
        scopeRoom.ws.send(angular.toJson({action:"callPlayer", gameType:1 * gameType}));
    };

    $scope.joinGame = function(roomName) {
        let scopeRoom = eval("$scope." + roomName);
        scopeRoom.ws.send(angular.toJson({action:"joinGame"}));
    };

    $scope.leaveGame = function(roomName) {
        let scopeRoom = eval("$scope." + roomName);
        scopeRoom.ws.send(angular.toJson({action:"leaveGame"}));
    };

    $scope.connectRoom = function(roomName) {
        let scopeRoom = eval("$scope." + roomName);
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
