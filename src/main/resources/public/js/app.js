"use strict";

let listController = function($scope, $location) {
    $scope.lists = [
        {"id": "1", "name": "张三", "age": 22, "phoneNum": "18298705786"},
        {"id": "2", "name": "王五", "age": 28, "phoneNum": "18456705786"},
        {"id": "3", "name": "张三", "age": 29, "phoneNum": "18291235786"},
        {"id": "4", "name": "蔡雄", "age": 22, "phoneNum": "18298705786"},
        {"id": "5", "name": "张三", "age": 26, "phoneNum": "18759705786"},
        {"id": "6", "name": "张楚", "age": 22, "phoneNum": "18298705786"}
    ];
    $scope.joinChatRoom = function(roomId) {
        $location.path("/login");
    }
};

let loginController = function($scope, $http, $location) {
    $scope.formData = {};
    $scope.responseInfo = "";
    $scope.getUserInfo = function(accessToken) {
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

angular.module('ngRouteChatApp', ['ngRoute'])
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