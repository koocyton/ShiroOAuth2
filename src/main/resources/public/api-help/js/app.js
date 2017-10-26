"use strict";

let RegisterController = function($scope, $http) {
    $scope.registerData = [];
    $scope.testResponse = null;
    $scope.mRegister = function() {
        formPost($http, '/api/v1/register', $scope.registerData,
            function(res) {
                $scope.testResponse = res.data;
            },
            null);
    };
};

let LoginController = function($scope, $http) {
    $scope.loginData = [];
    $scope.mLogin = function() {
        formPost($http, '/api/v1/login', $scope.loginData,
            function(res) {
                $scope.testResponse = res.data;
            },
            null);
    };
};

let CreateRoomController = function($scope, $http) {
    $scope.createRoomData = [];
    $scope.accessToken = "";
    $scope.mCreateRoom = function() {
        formPost($http, '/api/v1/room/create', $scope.accessToken,  $scope.createRoomData,
            function(res) {
                $scope.testResponse = res.data;
            },
            null);
    };
};

let RoomListController = function($scope, $http) {
    $scope.accessToken = "";
    $scope.mRoomList = function() {
        httpGet($http, '/api/v1/room/list', $scope.accessToken,
            function(res) {
                $scope.testResponse = res.data;
            },
            null);
    };
};

let JoinRoomController = function($scope, $http) {
    $scope.joinRoomData = [];
    $scope.accessToken = "";
    $scope.mJoinRoom = function() {
        formPost($http, '/api/v1/room/join', $scope.accessToken, $scope.joinRoomData,
            function(res) {
                $scope.testResponse = res.data;
            },
            null);
    };
};

let apiHelpApp = angular.module('ngApiHelpApp',[]);

apiHelpApp.controller('LoginController', LoginController);
apiHelpApp.controller('RegisterController', RegisterController);
apiHelpApp.controller('CreateRoomController', CreateRoomController);
apiHelpApp.controller('RoomListController', RoomListController);
apiHelpApp.controller('JoinRoomController', JoinRoomController);
