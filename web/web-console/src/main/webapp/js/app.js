angular.module('FuseIDE', [
    'ngResource'
]).config(function ($routeProvider) {
    $routeProvider.when('/about', {
        templateUrl: 'partials/about.html'
    }).when('/detail', {
        templateUrl: 'partials/detail.html',
        controller: DetailController
    }).when('/logs', {
        templateUrl: 'partials/logs.html',
        controller: LogController
    }).otherwise({
        redirectTo: '/about'
    });
}).factory('workspace', function ($rootScope) {
    var jolokia = new Jolokia("/jolokia");
    jolokia.start(2000);
    var sharedService = {
        selection: [],
        jolokia: jolokia,
        closeHandle: function (scope, key) {
            if (typeof key === "undefined") { key = 'jolokiaHandle'; }
            var handle = scope[key];
            if(handle) {
                jolokia.unregister(handle);
                handle[key] = null;
            }
        }
    };
    return sharedService;
});
function LogController($scope, $resource, $location) {
    var resourceUrl = $location.path() + 'jolokia/exec/org.fusesource.insight:type=LogQuery/getLogEvents/0?mimeType=application/json';
    var Model = $resource(resourceUrl);
    var results = Model.get();
    var value = results.value;
    $scope.logs = value;
}
function getOrElse(value, key, defaultValue) {
    if (typeof defaultValue === "undefined") { defaultValue = {
    }; }
    var answer = value[key];
    if(!answer) {
        answer = defaultValue;
        value[key] = answer;
    }
    return answer;
}
var Folder = (function () {
    function Folder() { }
    Folder.prototype.children = function () {
        return this;
    };
    return Folder;
})();
function MBeansController($scope, $resource, $location, workspace) {
    $scope.tree = {
    };
    var resourceUrl = "/jolokia/list?canonicalProperties=false&amp;maxDepth=2";
    console.log("Using url: " + resourceUrl);
    var Model = $resource(resourceUrl);
    console.log("About to load results from model");
    var results = Model.get(function () {
        console.log("Loaded results!");
        var tree = new Folder();
        var domains = results.value;
        for(var domain in domains) {
            var mbeans = domains[domain];
            for(var path in mbeans) {
                var entries = {
                };
                var folder = getOrElse(tree, domain, new Folder());
                var items = path.split(',');
                var paths = [];
                items.forEach(function (item) {
                    var kv = item.split('=');
                    var key = kv[0];
                    var value = kv[1] || key;
                    entries[key] = value;
                    paths.push(value);
                });
                var lastPath = paths.pop();
                paths.forEach(function (value) {
                    folder = getOrElse(folder, value, new Folder());
                });
                var mbeanInfo = {
                    domain: domain,
                    path: path,
                    paths: paths,
                    objectName: domain + ":" + path,
                    entries: entries
                };
                folder[lastPath] = mbeanInfo;
            }
        }
        $scope.results = results;
        $scope.tree = tree;
    });
    $scope.select = function (node) {
        workspace.selection = node;
        $location.path('/detail');
    };
}
function DetailController($scope, $routeParams, workspace, $rootScope) {
    $scope.routeParams = $routeParams;
    $scope.workspace = workspace;
    $scope.$watch('workspace.selection', function () {
        var node = $scope.workspace.selection;
        workspace.closeHandle($scope);
        var mbean = node.objectName;
        if(mbean) {
            var jolokia = workspace.jolokia;
            var query = {
                type: "READ",
                mbean: mbean
            };
            jolokia.request(query, {
                success: function (response) {
                    $scope.attributes = response.value;
                    $scope.$apply();
                },
                error: function (response) {
                    alert("Jolokia request failed: " + response.error);
                }
            });
            $scope.jolokiaHandle = jolokia.register(function (response) {
                $scope.attributes = response.value;
                $scope.$apply();
            }, query);
        }
    });
    $scope.$on('$destroy', function () {
        workspace.closeHandle($scope);
    });
}
