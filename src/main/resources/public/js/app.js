angular.module('ngRouteChatApp', ['ngRoute'])
    .controller("listCtl", function($scope) {
        $scope.lists = [
            {"id": "1", "name": "张三", "age": 22, "phoneNum": "18298705786"},
            {"id": "2", "name": "王五", "age": 28, "phoneNum": "18456705786"},
            {"id": "3", "name": "张三", "age": 29, "phoneNum": "18291235786"},
            {"id": "4", "name": "蔡雄", "age": 22, "phoneNum": "18298705786"},
            {"id": "5", "name": "张三", "age": 26, "phoneNum": "18759705786"},
            {"id": "6", "name": "张楚", "age": 22, "phoneNum": "18298705786"}
        ]
    })
    .config([
        '$routeProvider',
        '$locationProvider',
        function ($routeProvider, $locationProvider)
        {
            $locationProvider.hashPrefix('');
            $routeProvider
                .when('/login', {templateUrl: '/demo/chat-room/login'})
                .when('/list', {templateUrl: '/demo/chat-room/list', controller:"listCtl"})
                .otherwise({redirectTo: '/login'});
        }
    ]
);