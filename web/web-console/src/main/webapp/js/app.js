angular.module('FuseIDE', [
    'ngResource'
]).config(function ($routeProvider) {
    $routeProvider.when('/preferences', {
        templateUrl: 'partials/preferences.html'
    }).when('/attributes', {
        templateUrl: 'partials/attributes.html',
        controller: DetailController
    }).when('/logs', {
        templateUrl: 'partials/logs.html',
        controller: LogController
    }).when('/debug', {
        templateUrl: 'partials/debug.html',
        controller: DetailController
    }).when('/about', {
        templateUrl: 'partials/about.html',
        controller: DetailController
    }).otherwise({
        redirectTo: '/attributes'
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
var logQueryMBean = 'org.fusesource.insight:type=LogQuery';
function scopeStoreJolokiaHandle($scope, jolokia, jolokiaHandle) {
    if(jolokiaHandle) {
        $scope.$on('$destroy', function () {
            closeHandle($scope, jolokia);
        });
        $scope.jolokiaHandle = jolokiaHandle;
    }
}
function closeHandle($scope, jolokia) {
    var jolokiaHandle = $scope.jolokiaHandle;
    if(jolokiaHandle) {
        console.log('Closing the handle ' + jolokiaHandle);
        jolokia.unregister(jolokiaHandle);
        $scope.jolokiaHandle = null;
    }
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
function NavBarController($scope, $location, workspace) {
    $scope.workspace = workspace;
    $scope.navClass = function (page) {
        var currentRoute = $location.path().substring(1) || 'home';
        return page === currentRoute ? 'active' : '';
    };
    $scope.hasMBean = function (objectName) {
        var workspace = $scope.workspace;
        if(workspace) {
            var tree = workspace.tree;
            if(tree) {
                var folder = tree[objectName];
                if(folder) {
                    return true;
                }
            }
        }
        return false;
    };
}
function PreferencesController($scope, workspace) {
    $scope.workspace = workspace;
    $scope.updateRate = workspace.getUpdateRate();
    $scope.$watch('updateRate', function () {
        $scope.workspace.setUpdateRate($scope.updateRate);
    });
}
function MBeansController($scope, $location, workspace) {
    $scope.workspace = workspace;
    $scope.tree = new Folder('MBeans');
    $scope.select = function (node) {
        $scope.workspace.selection = node;
        var mbean = node['objectName'];
        if(mbean && mbean === logQueryMBean) {
            $location.path('/logs');
        } else {
            $location.path('/attributes');
        }
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
        var workspace2 = $scope.workspace;
        if(workspace2) {
            workspace2.tree = tree;
        }
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
        closeHandle($scope, $scope.workspace.jolokia);
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
                    scopeStoreJolokiaHandle($scope, jolokia, fn.apply(jolokia, args));
                }
            } else {
                scopeStoreJolokiaHandle($scope, jolokia, jolokia.register(callback, query));
            }
        }
    });
}
function LogController($scope, $location, workspace) {
    $scope.workspace = workspace;
    $scope.logs = {
    };
    $scope.toTime = 0;
    $scope.queryJSON = {
        type: "EXEC",
        mbean: logQueryMBean,
        operation: "logResultsSince",
        arguments: [
            $scope.toTime
        ],
        ignoreErrors: true
    };
    $scope.filterLogs = function (logs, query) {
        var filtered = [];
        var queryRegExp = null;
        if(query) {
            queryRegExp = RegExp(query.escapeRegExp(), 'i');
        }
        angular.forEach(logs, function (log) {
            if(!query || Object.values(log).any(function (value) {
                return value && value.toString().has(queryRegExp);
            })) {
                filtered.push(log);
            }
        });
        return filtered;
    };
    $scope.logClass = function (log) {
        var level = log['level'];
        if(level) {
            var lower = level.toLowerCase();
            if(lower.startsWith("warn")) {
                return "warning";
            } else {
                if(lower.startsWith("err")) {
                    return "error";
                } else {
                    if(lower.startsWith("debug")) {
                        return "info";
                    }
                }
            }
        }
        return "";
    };
    var updateValues = function (response) {
        var logs = response.events;
        var toTime = response.toTimestamp;
        if(toTime) {
            $scope.toTime = toTime;
            $scope.queryJSON.arguments = [
                toTime
            ];
        }
        if(logs) {
            var seq = 0;
            for(var idx in logs) {
                var log = logs[idx];
                if(log) {
                    seq = log['seq'] || idx;
                    $scope.logs[seq] = log;
                }
            }
            console.log("Got results " + logs.length + " last seq: " + seq);
            $scope.$apply();
        } else {
            console.log("Failed to get a response! " + response);
        }
    };
    var jolokia = workspace.jolokia;
    jolokia.execute(logQueryMBean, "allLogResults", onSuccess(updateValues));
    var asyncUpdateValues = function (response) {
        var value = response.value;
        if(value) {
            updateValues(value);
        } else {
            console.log("Failed to get a response! " + response);
        }
    };
    var callback = onSuccess(asyncUpdateValues, {
        error: function (response) {
            asyncUpdateValues(response);
        }
    });
    scopeStoreJolokiaHandle($scope, jolokia, jolokia.register(callback, $scope.queryJSON));
}
