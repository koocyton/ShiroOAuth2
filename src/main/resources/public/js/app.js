"use strict";

angular.module('ngRouteChatApp', ['ngRoute', 'ngCookies'])
    .controller("loginCtrl", LoginController)
    .controller("hallCtrl", HallController)
    .controller("registerCtrl", RegisterController)
    .controller("roomCtrl", RoomController)
    .controller("createRoomCtrl", CreateRoomController)
    .controller("apiDocCtrl", ApiDocController)
    .config([
        '$routeProvider',
        '$locationProvider',
        function ($routeProvider, $locationProvider)
        {
            $locationProvider.hashPrefix('');
            $routeProvider
                .when('/login', {templateUrl: '/demo/login', controller:"loginCtrl"})
                .when('/register', {templateUrl: '/demo/register', controller:"registerCtrl"})
                .when('/hall', {templateUrl: '/demo/hall', controller:"hallCtrl"})
                .when('/room', {templateUrl: '/demo/room', controller:"roomCtrl"})
                .when('/create-room', {templateUrl: '/demo/create-room', controller:"createRoomCtrl"})
                .when('/api-doc', {templateUrl: '/demo/api-doc', controller:"apiDocCtrl"})
                .otherwise({redirectTo: '/login'});
        }
    ]
);