"use strict";

let RegisterController = function($scope, $http, $location, $cookieStore) {
    $scope.registerData = [];
    $scope.requestRegister = function() {
        formPost($http, '/api/v1/register', $scope.registerData, null, null);
    };
};

let LoginController = function($scope, $http, $location, $cookieStore) {
    $scope.loginData = [];
    $scope.requestLogin = function() {
        formPost($http, '/api/v1/login', $scope.loginData, null, null);
    };
};

let CreateRoomController = function($scope, $http, $location, $cookieStore) {
    $scope.createRoomData = [];
    $scope.requestCreateRoom = function() {
        formPost($http, '/api/v1/room/create', $scope.createRoomData, null, null);
    };
};

let HallController = function($scope, $http, $location, $cookieStore) {

    $scope.requestLogin = function() {
        $http({
            method: 'GET',
            url: '/api/v1/room/list',
            data    : "roomName=" + $scope.formData.roomName,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(
            function successCallback(res) {
                console.log(res);
            },
            function errorCallback(res) {
                console.log(res);
            }
        );
    };
};

let RoomController = function($scope, $http, $location, $cookieStore) {
    $scope.joinRoomData = [];
    $scope.requestJoinRoom = function() {
        formPost($http, '/api/v1/room/join', $scope.joinRoomData, null, null);
    };
};

angular.module('ngRouteChatApp', ['ngRoute', 'ngCookies'])
    .controller("registerCtrl", RegisterController)
    .controller("loginCtrl", LoginController)
    .controller("hallCtrl", HallController)
    .controller("roomCtrl", RoomController)
    .controller("createRoomCtrl", CreateRoomController)
    .config([
        '$routeProvider',
        '$locationProvider',
        function ($routeProvider, $locationProvider)
        {
            $locationProvider.hashPrefix('');
            $routeProvider
                .when('/login', {templateUrl: 'page/login.html', controller:"loginCtrl"})
                .when('/register', {templateUrl: 'page/register.html', controller:"registerCtrl"})
                .when('/hall', {templateUrl: 'page/hall.html', controller:"hallCtrl"})
                .when('/room', {templateUrl: 'page/room.html', controller:"roomCtrl"})
                .when('/create-room', {templateUrl: 'page/create-room.html', controller:"createRoomCtrl"})
                .otherwise({redirectTo: '/login'});
        }
    ]
);