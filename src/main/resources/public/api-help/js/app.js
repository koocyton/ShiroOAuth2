"use strict";

let RegisterController = function($scope, $http) {
    $scope.registerData = [];
    $scope.mRegister = function() {
        formPost($http, '/api/v1/register', $scope.registerData, null, null);
    };
};

let LoginController = function($scope, $http) {
    $scope.loginData = [];
    $scope.mLogin = function() {
        formPost($http, '/api/v1/login', $scope.loginData, null, null);
    };
};

let CreateRoomController = function($scope, $http) {
    $scope.createRoomData = [];
    $scope.mCreateRoom = function() {
        formPost($http, '/api/v1/room/create', $scope.createRoomData, null, null);
    };
};

let RoomListController = function($scope, $http) {
    $scope.mRoomList = function() {
        httpGet($http, '/api/v1/room/list', null, null);
    };
};

let JoinRoomController = function($scope, $http) {
    $scope.joinRoomData = [];
    $scope.mJoinRoom = function() {
        formPost($http, '/api/v1/room/join', $scope.joinRoomData, null, null);
    };
};

let apiHelpApp = angular.module('ngApiHelpApp',[]);

apiHelpApp.controller('LoginController', LoginController);
apiHelpApp.controller('RegisterController', RegisterController);
apiHelpApp.controller('CreateRoomController', CreateRoomController);
apiHelpApp.controller('RoomListController', RoomListController);
apiHelpApp.controller('JoinRoomController', JoinRoomController);
