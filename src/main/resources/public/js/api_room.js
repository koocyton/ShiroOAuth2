"use strict";

let ApiRoomController = function($scope, $http) {

    $scope.clients = [];

    $scope.ws = [];

    for(let ii=0; ii<12; ii++) {
        $scope.clients[ii] = {
            account  : "kton" + ii + "@gmail.com",
            password : "123456",
            roomName : "西屋独居",
            roomId : 54613,
            talkMessage : "我要说 ...",
            messageList : []
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
        sendMessage(ii, {action: "joinRoom", roomId: $scope.clients[ii].roomId})
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
        scrollWindow(ii);
    };

    let socketConnect = function(ii, accessToken) {
        if ($scope.ws[ii]===null) {
            $scope.ws[ii] = WebSocketService
                .connect("/room-socket?access-token=" + accessToken + "&ii=" + ii)
                .onClose(function (e) {
                })
                .onMessage(function(e) {
                    let nn = $scope.clients[ii].messageList.length;
                    $scope.clients[ii].messageList[nn] = " <<< " + e.data;
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
        formPost($http, '/api/v1/login', $scope.clients[ii],
            function(res){
                let accessToken = res.data.accessToken;
                socketConnect(ii, accessToken);
            },
            function(res){
                console.log(" login error !");
            });
    }
};

let apiRoomApp = angular.module('ngApiRoomApp',[]);
apiRoomApp.controller('ApiRoomController', ApiRoomController);
