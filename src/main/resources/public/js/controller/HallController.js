"use strict";

/*
 * Hall Controller
 */
let HallController = function($scope, $http, $location, $cookieStore) {

    $scope.roomList = [];

    $scope.openHall = function(accessToken) {
        $http({
            method: 'GET',
            url: '/api/v1/room/hot-list',
            headers: {'access-token': accessToken}
        }).then(
            function successCallback(res) {
                let roomList = [];
                for (let key in res.data) {
                    roomList[rooms.length] = res.data[key];
                }
                $scope.roomList = roomList;
            },
            function errorCallback(res) {
                console.log(res);
            }
        );
    };

    let tokenService = new TokenService($cookieStore);
    let accessToken = tokenService.getToken();
    tokenService.checkToken(
        function successCallback(res){
            $scope.openHall(accessToken);
        },
        function errorCallback(res){
            $location.path("/login");
        }
    );
};