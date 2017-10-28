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
        formPost($http, '/api/v1/room/create', $scope.createRoomData,
            function(res) {
                $scope.testResponse = res.data;
            },
            null,
            {"access-token": $scope.accessToken});
    };
};

let RoomListController = function($scope, $http) {
    $scope.accessToken = "";
    $scope.mRoomList = function() {
        httpGet($http, '/api/v1/room/list',
            function(res) {
                $scope.testResponse = res.data;
            },
            null,
            {"access-token": $scope.accessToken});
    };
};

let UserInfoController = function($scope, $http) {
    $scope.accessToken = "";
    $scope.mUserInfo = function() {
        httpGet($http, '/api/v1/user/me',
            function(res) {
                $scope.testResponse = res.data;
            },
            null,
            {"access-token": $scope.accessToken});
    };
};

let JoinRoomController = function($scope, $http) {
    $scope.joinRoomData = [];
    $scope.accessToken = "";
    $scope.mJoinRoom = function() {
        formPost($http, '/api/v1/room/join', $scope.joinRoomData,
            function(res) {
                $scope.testResponse = res.data;
            },
            null,
            {"access-token": $scope.accessToken});
    };
};

let CurrentRoomController = function($scope, $http) {

};

let LeaveRoomController = function($scope, $http) {

};

let ChatRoomController = function($scope, $http) {
    $scope.accessToken = "";
    $scope.roomId = 0;
    $scope.roomName = "美丽的小屋";
    $scope.ws = null;

    $scope.createSocket = function() {
        $scope.ws = WebSocketService
            .connect("/live-socket?access-token=" + $scope.accessToken)
            .onOpen(function(e){})
            .onMessage(function(e){});
    };

    $scope.createRoom = function() {
        let createRoomMessage = {action:"createRoom", data:{roomName:$scope.roomName}};
        $scope.ws.send(angular.toJson(createRoomMessage));
    };

    $scope.joinRoom = function() {
        let joinRoomMessage = {action:"joinRoom", data:{roomId:$scope.roomId}};
        $scope.ws.send(angular.toJson(joinRoomMessage));
    };

    $scope.listRoom = function() {
        httpGet($http, '/api/v1/room/list',
            function(res) {
                $scope.testResponse = res.data;
            },
            null,
            {"access-token": $scope.accessToken});
    };
};
