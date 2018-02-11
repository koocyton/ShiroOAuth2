"use strict";

let ApiRoomController = function($scope, $http) {

    $scope.roomId = roomId;
    $scope.namePrefix = namePrefix;

    $scope.clients = [];

    $scope.ws = [];

    for(let ii=0; ii<12; ii++) {
        $scope.clients[ii] = {
            account  : $scope.namePrefix + "_" + ii + "@gmail.com",
            password : "123456",
            roomName : "西屋独居",
            talkMessage : "你是一头铁狼",
            messageList : [],
            nickName: $scope.namePrefix + "_" + ii
        };
        $scope.ws[ii] = null;
    }

    $scope.callPlayer = function(ii) {
        sendMessage(ii, {action: "callPlayer", gameType: 1})
    };

    $scope.joinGame = function(ii) {
        sendMessage(ii, {action: "joinGame"})
    };

    $scope.leaveGame = function(ii) {
        sendMessage(ii, {action: "leaveGame"})
    };

    $scope.publicTalk = function(ii) {
        sendMessage(ii, {action: "publicTalk", roomName: $scope.clients[ii].talkMessage})
    };

    $scope.autoCreateRoom = function(ii) {
        sendMessage(ii, {action: "createRoom", roomName: $scope.clients[ii].roomName})
    };

    $scope.autoJoinRoom = function(ii) {
        sendMessage(ii, {action: "joinRoom", roomId: $scope.roomId})
    };

    let scrollWindow=function(ii) {
        setTimeout(function() {
            let _el = document.getElementById('message_' + ii);
            _el.scrollTop = _el.scrollHeight;
        }, 1);
    };

    let sendMessage = function(ii, messageObject) {
        let nn = $scope.clients[ii].messageList.length;
        $scope.clients[ii].messageList[nn] = " >>> " + angular.toJson(messageObject);
        $scope.ws[ii].send(angular.toJson(messageObject));
        $scope.$apply();
        scrollWindow(ii);
    };

    let socketConnect = function(ii, accessToken) {
        if ($scope.ws[ii]===null) {
            $scope.ws[ii] = WebSocketService
                .connect("/game-socket?session-token=" + accessToken + "&ii=" + ii)
                .onClose(function (e) {
                })
                .onMessage(function(e) {
                    let nn = $scope.clients[ii].messageList.length;
                    $scope.clients[ii].messageList[nn] = " <<< " + e.data;
                    $scope.$apply();
                    scrollWindow(ii);
                })
                .onOpen(function(e){
                    if (ii===0) {
                        $scope.autoCreateRoom(ii);
                    }
                    else {
                        setTimeout(function(){
                            $scope.autoJoinRoom(ii);
                        }, 1000);
                    }
                });
        }
    };

    for(let ii=0; ii<12; ii++) {
        formPost($http, '/api/login', $scope.clients[ii],
            function(res){
                let accessToken = res.data.session_token;
                socketConnect(ii, accessToken);
            },
            function(res){
                console.log(" login error !");
            });
    }
};

let apiRoomApp = angular.module('ngApiRoomApp',[]);
apiRoomApp.controller('ApiRoomController', ApiRoomController);
