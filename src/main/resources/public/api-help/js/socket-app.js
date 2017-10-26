"use strict";

let crOnOpen = function(e) {
    console.log("WebSocket crOnOpen !");
};

let crOnMessage = function(e) {
    console.log("WebSocket onMessage !");
};

let ChatRoomController = function($scope) {
    $scope.accessToken = "-";
    $scope.mListRoom = function() {
        // websocket connect
        WebSocketService
            .connect("/game-socket?access-token=" + $scope.accessToken)
            .onOpen(crOnOpen)
            .onMessage(crOnMessage);

        /*
        httpGet($http, '/api/v1/room/list', $scope.registerData,
            function(res) {
                $scope.testResponse = res.data;
            },
            null,
            {"access-token": $scope.accessToken});
        */
    };
};

let apiHelpApp = angular.module('ngSocketApp',[]);
apiHelpApp.controller('ChatRoomController', ChatRoomController);
