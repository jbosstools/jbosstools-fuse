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
    options['success'] = fn;
    if(!options['error']) {
        options['error'] = function (response) {
            console.log("Jolokia request failed: " + response.error);
        };
    }
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
        canonicalNaming: false,
        maxDepth: 2
    }));
}
var Table = (function () {
    function Table() {
        this.columns = {
        };
        this.rows = {
        };
    }
    Table.prototype.values = function (row, columns) {
        var answer = [];
        if(columns) {
            for(name in columns) {
                console.log("Looking up: " + name + " on row ");
                answer.push(row[name]);
            }
        }
        return answer;
    };
    Table.prototype.setRow = function (key, data) {
        var _this = this;
        this.rows[key] = data;
        Object.keys(data).forEach(function (key) {
            var columns = _this.columns;
            if(!columns[key]) {
                columns[key] = {
                    name: key
                };
            }
        });
    };
    return Table;
})();
function DetailController($scope, $routeParams, workspace, $rootScope) {
    $scope.routeParams = $routeParams;
    $scope.workspace = workspace;
    $scope.isTable = function (value) {
        return value instanceof Table;
    };
    $scope.getAttributes = function (value) {
        if(angular.isArray(value) && angular.isObject(value[0])) {
            return value;
        }
        if(angular.isObject(value) && !angular.isArray(value)) {
            return [
                value
            ];
        }
        return null;
    };
    $scope.rowValues = function (row, col) {
        return [
            row[col]
        ];
    };
    var asQuery = function (mbeanName) {
        return {
            type: "READ",
            mbean: mbeanName,
            ignoreErrors: true
        };
    };
    var tidyAttributes = function (attributes) {
        var objectName = attributes['ObjectName'];
        if(objectName) {
            var name = objectName['objectName'];
            if(name) {
                attributes['ObjectName'] = name;
            }
        }
    };
    $scope.$watch('workspace.selection', function () {
        var node = $scope.workspace.selection;
        workspace.closeHandle($scope);
        var mbean = node.objectName;
        var query = null;
        var jolokia = workspace.jolokia;
        var updateValues = function (response) {
            var attributes = response.value;
            if(attributes) {
                tidyAttributes(attributes);
                $scope.attributes = attributes;
                $scope.$apply();
            } else {
                console.log("Failed to get a response! " + response);
            }
        };
        if(mbean) {
            query = asQuery(mbean);
        } else {
            var children = node.children;
            if(children) {
                var mbeans = children.map(function (child) {
                    return child.objectName;
                }).filter(function (mbean) {
                    return mbean;
                });
                if(mbeans) {
                    query = mbeans.map(function (mbean) {
                        return asQuery(mbean);
                    });
                    if(query.length === 1) {
                        query = query[0];
                    } else {
                        if(query.length === 0) {
                            query = null;
                        } else {
                            $scope.attributes = new Table();
                            updateValues = function (response) {
                                var attributes = response.value;
                                if(attributes) {
                                    tidyAttributes(attributes);
                                    var mbean = attributes['ObjectName'];
                                    var request = response.request;
                                    if(!mbean && request) {
                                        mbean = request['mbean'];
                                    }
                                    if(mbean) {
                                        var table = $scope.attributes;
                                        if(!(table instanceof Table)) {
                                            table = new Table();
                                            $scope.attributes = table;
                                        }
                                        table.setRow(mbean, attributes);
                                        $scope.$apply();
                                    } else {
                                        console.log("no ObjectName in attributes " + Object.keys(attributes));
                                    }
                                } else {
                                    console.log("Failed to get a response! " + JSON.stringify(response));
                                }
                            };
                        }
                    }
                }
            }
        }
        if(query) {
            jolokia.request(query, onSuccess(updateValues));
            var callback = onSuccess(updateValues, {
                error: function (response) {
                    updateValues(response);
                }
            });
            if(angular.isArray(query)) {
                if(query.length >= 1) {
                    var args = [
                        callback
                    ].concat(query);
                    var fn = jolokia.register;
                    $scope.jolokiaHandle = fn.apply(jolokia, args);
                }
            } else {
                $scope.jolokiaHandle = jolokia.register(callback, query);
            }
        }
    });
    $scope.$on('$destroy', function () {
        workspace.closeHandle($scope);
    });
}
