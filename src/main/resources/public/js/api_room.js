"use strict";

let ApiRoomController = function($scope, $http) {

    $scope.clients = [];

    $scope.ws = [];

    for(let ii=0; ii<12; ii++) {
        $scope.clients[ii] = {account:"kton" + ii + "@gmail.com", password:"123456"}
        $scope.ws[ii] = null;
    }

    let showMessage = function(ii, messageObject) {
        // $scope.ws[ii].send(angular.toJson(messageObject));
    };

    let createRoom = function(ii) {
        sendMessage(ii, {action: "createRoom", roomName: "西屋独居"})
    };

    let joinRoom = function(ii) {
        sendMessage(ii, {action: "joinRoom", roomId: 54613})
    };

    let sendMessage = function(ii, messageObject) {
        $scope.ws[ii].send(angular.toJson(messageObject));
    };

    let socketConnect = function(ii, accessToken) {

        if ($scope.ws[ii]===null) {
            //
            $scope.ws[ii] = WebSocketService
                .connect("/room-socket?access-token=" + accessToken)
                .onClose(function (e) {

                })
                .onMessage(function(e) {

                })
                .onOpen(function(e){
                    if (ii===0) {
                        createRoom(ii);
                    }
                    else {
                        setTimeout(function(){
                            joinRoom(ii);
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
