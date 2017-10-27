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

let crOnOpen = function(e) {
    console.log("WebSocket crOnOpen !");
};

let crOnMessage = function(e) {
    console.log("WebSocket onMessage !");
};

let ChatRoomController = function($scope) {
    $scope.accessToken = "-";
    $scope.mListRoom = function() {
        // websocket connect
        WebSocketService
            .connect("/room-socket?access-token=" + $scope.accessToken)
            .onOpen(crOnOpen)
            .onMessage(crOnMessage);

        /*
        httpGet($http, '/api/v1/room/list', $scope.registerData,
            function(res) {
                $scope.testResponse = res.data;
            },
            null,
            {"access-token": $scope.accessToken});
        */
    };
};