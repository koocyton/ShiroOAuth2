"use strict";

/*
 * Room Controller
 */
let RoomController = function($scope, $http, $location, $cookieStore) {

    let tokenService = new TokenService($cookieStore);
    let accessToken = tokenService.getToken();

    WebSocketService
        .connect("/game-socket?access-token=" + accessToken)
        .onMessage(function(e){
            console.log(e);
        })
        .onClose(function(e){
            console.log(e);
        });
};