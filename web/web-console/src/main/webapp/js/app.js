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
    jolokia.start(5000);
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
function onSuccess(fn, options) {
    if (typeof options === "undefined") { options = {
    }; }
    options['ignoreErrors'] = true;
    options['mimeType'] = 'application/json';
    if(!options['error']) {
        options['error'] = function (response) {
            console.log("Jolokia request failed: " + response.error);
        };
    }
    options['success'] = function (response) {
        fn(response);
    };
    return options;
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
function MBeansController($scope, $location, workspace) {
    $scope.tree = new Folder();
    $scope.select = function (node) {
        workspace.selection = node;
        $location.path('/detail');
    };
    function populateTree(response) {
        var tree = new Folder();
        var domains = response.value;
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
        $scope.tree = tree;
        $scope.$apply();
    }
    var jolokia = workspace.jolokia;
    jolokia.request({
        type: 'list'
    }, onSuccess(populateTree, {
        canonicalProperties: false,
        maxDepth: 2
    }));
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
            var updateValues = function (response) {
                if(response.value) {
                    $scope.attributes = response.value;
                    $scope.$apply();
                } else {
                    console.log("Failed to get a response! " + response);
                }
            };
            var query = {
                type: "READ",
                mbean: mbean,
                ignoreErrors: true
            };
            jolokia.request(query, onSuccess(updateValues));
            $scope.jolokiaHandle = jolokia.register(onSuccess(updateValues, {
                error: function (response) {
                    updateValues(response);
                }
            }), query);
        }
    });
    $scope.$on('$destroy', function () {
        workspace.closeHandle($scope);
    });
}
