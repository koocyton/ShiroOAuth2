"use strict";

let listController = function($scope, $http, $location, $cookieStore) {
    $scope.lists = [];

    let accessToken = $cookieStore.get("access-token");

    if (typeof accessToken==="undefined" || accessToken==="") {
        $location.path("/login")
    }
    else {

        $http({
            method: 'GET',
            url: '/api/v1/room/hot-list',
            headers: {'access-token': accessToken}
        }).then(
            function successCallback(res) {
                let rooms = [];
                for (let key in res.data) {
                    rooms[rooms.length] = res.data[key];
                }
                $scope.lists = rooms;
            },
            function errorCallback(res) {
                console.log(res);
            }
        );
    }
};

let loginController = function($scope, $http, $location, $cookieStore) {
    $scope.formData = {};
    $scope.responseInfo = "";
    $scope.accessToken = "";

    $scope.getUserInfo = function(accessToken) {
        $cookieStore.put("access-token", accessToken);
        $http({
            method  : 'GET',
            url     : '/api/v1/user/me',
            headers  : {'access-token' : accessToken}
        }).then(
            function successCallback(res)
            {
                console.log(res.data);
                $location.path("/list")
            },
            function errorCallback(res){}
        );
    };

    $scope.requestLogin = function() {
        if (typeof $scope.formData.account==="undefined") {
            alert("请输入账号");
            return;
        }
        if (typeof $scope.formData.password==="undefined" || $scope.formData.password.length<6) {
            alert("密码最短有 6 位");
            return;
        }
        $http({
            method  : 'POST',
            url     : '/api/v1/login',
            data    : "account=" + $scope.formData.account + "&password=" + $scope.formData.password,
            headers : { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).then(
            function successCallback(res)
            {
                if (typeof res.data==="object" && typeof res.data.accessToken==="string") {
                    $scope.responseInfo = "Access Token : " + res.data.accessToken;
                    $scope.getUserInfo(res.data.accessToken);
                    // $scope.showRoomList(res.data.accessToken);
                }
            },
            function errorCallback(res)
            {
                if (typeof res.data==="object" && typeof res.data.errmsg==="string") {
                    $scope.responseInfo = res.data.errcode + "\n" + res.data.errmsg;
                }
            }
        );
    }
};

angular.module('ngRouteChatApp', ['ngRoute', 'ngCookies'])
    .controller("listCtl", listController)
    .controller("loginCtl", loginController)
    .config([
        '$routeProvider',
        '$locationProvider',
        function ($routeProvider, $locationProvider)
        {
            $locationProvider.hashPrefix('');
            $routeProvider
                .when('/login', {templateUrl: '/demo/chat-room/login', controller:"loginCtl"})
                .when('/list', {templateUrl: '/demo/chat-room/list', controller:"listCtl"})
                .otherwise({redirectTo: '/login'});
        }
    ]
);