angular.module('FuseIDE', [
    'ngResource'
]).config(function ($routeProvider) {
    $routeProvider.when('/preferences', {
        templateUrl: 'partials/preferences.html'
    }).when('/detail', {
        templateUrl: 'partials/detail.html',
        controller: DetailController
    }).when('/debug', {
        templateUrl: 'partials/debug.html',
        controller: DetailController
    }).when('/logs', {
        templateUrl: 'partials/logs.html',
        controller: LogController
    }).otherwise({
        redirectTo: '/detail'
    });
}).factory('workspace', function ($rootScope) {
    return new Workspace();
}).filter('humanize', function () {
    return function (value) {
        if(value) {
            var text = value.toString();
            return text.underscore().humanize();
        }
        return value;
    }
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
function supportsLocalStorage() {
    try  {
        return 'localStorage' in window && window['localStorage'] !== null;
    } catch (e) {
        return false;
    }
}
var Workspace = (function () {
    function Workspace() {
        this.jolokia = new Jolokia("/jolokia");
        this.updateRate = 0;
        this.selection = [];
        this.dummyStorage = {
        };
        var rate = this.getUpdateRate();
        this.setUpdateRate(rate);
    }
    Workspace.prototype.getLocalStorage = function (key) {
        if(supportsLocalStorage()) {
            return localStorage[key];
        }
        return this.dummyStorage[key];
    };
    Workspace.prototype.setLocalStorage = function (key, value) {
        if(supportsLocalStorage()) {
            localStorage[key] = value;
        } else {
            this.dummyStorage[key] = value;
        }
    };
    Workspace.prototype.closeHandle = function (scope, key) {
        if (typeof key === "undefined") { key = 'jolokiaHandle'; }
        var handle = scope[key];
        if(handle) {
            this.jolokia.unregister(handle);
            handle[key] = null;
        }
    };
    Workspace.prototype.getUpdateRate = function () {
        return this.getLocalStorage('updateRate') || 5000;
    };
    Workspace.prototype.setUpdateRate = function (value) {
        this.jolokia.stop();
        this.setLocalStorage('updateRate', value);
        if(value > 0) {
            this.jolokia.start(value);
        }
        console.log("Set update rate to: " + value);
    };
    return Workspace;
})();
var Folder = (function () {
    function Folder(title) {
        this.title = title;
        this.isFolder = true;
        this.children = [];
        this.map = {
        };
    }
    Folder.prototype.getOrElse = function (key, defaultValue) {
        if (typeof defaultValue === "undefined") { defaultValue = new Folder(key); }
        var answer = this.map[key];
        if(!answer) {
            answer = defaultValue;
            this.map[key] = answer;
            this.children.push(answer);
        }
        return answer;
    };
    return Folder;
})();
function NavBarController($scope, workspace) {
}
function PreferencesController($scope, workspace) {
    $scope.workspace = workspace;
    $scope.updateRate = workspace.getUpdateRate();
    $scope.$watch('updateRate', function () {
        $scope.workspace.setUpdateRate($scope.updateRate);
    });
}
function MBeansController($scope, $location, workspace) {
    $scope.tree = new Folder('MBeans');
    $scope.select = function (node) {
        workspace.selection = node;
        $location.path('/detail');
        $scope.$apply();
    };
    function populateTree(response) {
        var tree = new Folder('MBeans');
        var domains = response.value;
        for(var domain in domains) {
            var mbeans = domains[domain];
            for(var path in mbeans) {
                var entries = {
                };
                var folder = tree.getOrElse(domain);
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
                    folder = folder.getOrElse(value);
                });
                var mbeanInfo = {
                    title: lastPath,
                    domain: domain,
                    path: path,
                    paths: paths,
                    objectName: domain + ":" + path,
                    entries: entries
                };
                folder.getOrElse(lastPath, mbeanInfo);
            }
        }
        $scope.tree = tree;
        $scope.$apply();
        $("#jmxtree").dynatree({
            onActivate: function (node) {
                var data = node.data;
                $scope.select(data);
            },
            persist: false,
            debugLevel: 0,
            children: tree.children
        });
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
    $scope.getAttributes = function (value) {
        if(angular.isObject(value) && !angular.isArray(value)) {
            return [
                value
            ];
        }
        return null;
    };
    $scope.$watch('workspace.selection', function () {
        var node = $scope.workspace.selection;
        workspace.closeHandle($scope);
        var mbean = node.objectName;
        if(mbean) {
            var jolokia = workspace.jolokia;
            var updateValues = function (response) {
                var attributes = response.value;
                if(attributes) {
                    var objectName = attributes['ObjectName'];
                    if(objectName) {
                        var name = objectName['objectName'];
                        if(name) {
                            attributes['ObjectName'] = name;
                        }
                    }
                    $scope.attributes = attributes;
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
