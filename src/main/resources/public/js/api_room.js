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
            playerSpeakInput : "你是一头铁狼",
            messageList : [],
            nickName: $scope.namePrefix + "_" + ii,
            playerVoteInput: 0,
            witchHelpInput:0,
            witchKillInput:0,
            seerChoiceInput:0,
            hunterChoiceInput:0,
            wolfChoiceInput:0
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

    $scope.readyPlayer = function(ii) {
        sendMessage(ii, {action: "player-ready"})
    };

    $scope.playerSpeak = function(ii) {
        sendMessage(ii, {action: "player-speak", data: {message: $scope.clients[ii].playerSpeakInput}});
    };

    $scope.playerVote = function(ii) {
        sendMessage(ii, {action: "player-vote", data: {"target-player":$scope.clients[ii].playerVoteInput}})
    };

    $scope.wolfChoice = function(ii) {
        sendMessage(ii, {action: "wolf-choice", data: {"target-player":$scope.clients[ii].wolfChoiceInput}})
    };

    $scope.witchHelp = function(ii) {
        sendMessage(ii, {action: "witch-choice", data: {"target-player":$scope.clients[ii].witchHelpInput}})
    };

    $scope.witchKill = function(ii) {
        sendMessage(ii, {action: "witch-choice", data: {"target-player":$scope.clients[ii].witchKillInput}})
    };

    $scope.seerChoice = function(ii) {
        sendMessage(ii, {action: "seer-choice", data: {"target-player":$scope.clients[ii].seerChoiceInput}})
    };

    $scope.hunterChoice = function(ii) {
        sendMessage(ii, {action: "hunter-choice", data: {"target-player":$scope.clients[ii].hunterChoiceInput}})
    };

    // $scope.autoCreateRoom = function(ii) {
    //     sendMessage(ii, {action: "createRoom", data: $scope.clients[ii].roomName})
    // };
    //
    // $scope.autoJoinRoom = function(ii) {
    //     sendMessage(ii, {action: "joinRoom", data: $scope.roomId})
    // };

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
        // $scope.apply();
        scrollWindow(ii);
    };

    let socketConnect = function(ii, accessToken) {
        if ($scope.ws[ii]===null) {
            $scope.ws[ii] = WebSocketService
                .connect("/game-socket?session-token=" + accessToken + "&ii=" + ii)
                .onClose(function (e) {
                })
                .onMessage(function(e) {
                    let dataObj = null;
                    try { dataObj = JSON.parse(e.data); } catch(e) {}
                    let nn = $scope.clients[ii].messageList.length;
                    if (dataObj===null) {
                        $scope.clients[ii].messageList[nn] = " <<< " + e.data;
                    }
                    else if (dataObj.action!=="player-join" && dataObj.action!=="player-leave" && dataObj.action!=="player-ready") {
                        $scope.clients[ii].messageList[nn] = " <<< " + e.data;
                    }
                    $scope.$apply();
                    scrollWindow(ii);
                });
                // .onOpen(function(e){
                //     if (ii===0) {
                //         $scope.autoCreateRoom(ii);
                //     }
                //     else {
                //         setTimeout(function(){
                //             $scope.autoJoinRoom(ii);
                //         }, 1000);
                //     }
                // });
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
