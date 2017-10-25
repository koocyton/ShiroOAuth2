"use strict";

let RegisterController = function($scope, $http, $location, $cookieStore) {
};

let LoginController = function($scope, $http, $location, $cookieStore) {
};

let CreateRoomController = function($scope, $http, $location, $cookieStore) {
};

let HallController = function($scope, $http, $location, $cookieStore) {
};

let RoomController = function($scope, $http, $location, $cookieStore) {
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