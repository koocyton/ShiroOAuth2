"use strict";

/*
 * Create Room Controller
 */
let CreateRoomController = function($scope, $http, $location, $cookieStore) {
    let tokenService = new TokenService($cookieStore);
    tokenService.checkToken(
        function successCallback(res){
        },
        function errorCallback(res){
            $location.path("/login");
        }
    );
};