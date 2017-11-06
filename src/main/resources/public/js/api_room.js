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
            roomTalk : "我要说 ..."
        };
        $scope.ws[ii] = null;
    }

    $scope.roomTalk = function(ii) {
        sendMessage(ii, {action: "publicTalk", roomName: $scope.clients[ii].roomTalk})
    };

    $scope.createRoom = function(ii) {
        sendMessage(ii, {action: "createRoom", roomName: $scope.clients[ii].roomName})
    };

    $scope.joinRoom = function(ii) {
        sendMessage(ii, {action: "joinRoom", roomId: $scope.clients[ii].roomId})
    };

    let sendMessage = function(ii, messageObject) {
        $scope.ws[ii].send(angular.toJson(messageObject));
    };

    let socketConnect = function(ii, accessToken) {

        if ($scope.ws[ii]===null) {
            //
            $scope.ws[ii] = WebSocketService
                .connect("/room-socket?access-token=" + accessToken + "&ii=" + ii)
                .onClose(function (e) {
                })
                .onMessage(function(e) {
                })
                .onOpen(function(e){
                    if (ii===0) {
                        $scope.createRoom(ii);
                    }
                    else {
                        setTimeout(function(){
                            $scope.joinRoom(ii);
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
