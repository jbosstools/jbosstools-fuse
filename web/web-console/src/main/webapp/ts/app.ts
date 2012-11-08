angular.module('FuseIDE', ['ngResource']).
        config(($routeProvider) => {
            $routeProvider.
                    when('/about', {templateUrl:'partials/about.html'}).
                    when('/detail', {templateUrl:'partials/detail.html', controller:DetailController}).
                    when('/logs', {templateUrl:'partials/logs.html', controller:LogController}).
                    otherwise({redirectTo:'/about'});
        }).
        factory('workspace', function ($rootScope) {
            var jolokia = new Jolokia("/jolokia");
            jolokia.start(2000);

            var sharedService = {
                selection:[],
                jolokia:jolokia,

                /**
                 * Closes the given handle object if its defined
                 */
                closeHandle:function (scope:any, key:string = 'jolokiaHandle'):void {
                    var handle = scope[key];
                    if (handle) {
                        console.log('Closing the handle ' + handle);
                        jolokia.unregister(handle);
                        handle[key] = null;
                    }
                }
            };

            return sharedService;
        });
//when('/phones/:phoneId', {templateUrl: 'partials/phone-detail.html', controller: PhoneDetailCtrl}).


function LogController($scope, $resource, $location) {
    // 		http://localhost:8181/jolokia/exec/org.fusesource.insight:type=LogQuery/getLogEvents/0?mimeType=application/json

    var resourceUrl = $location.path()
            + 'jolokia/exec/org.fusesource.insight:type=LogQuery/getLogEvents/0?mimeType=application/json';
    var Model = $resource(resourceUrl);
    var results = Model.get();
    var value = results.value;
    $scope.logs = value;
}

function getOrElse(value:any, key:string, defaultValue:any = {}):any {
    var answer = value[key];
    if (!answer) {
        answer = defaultValue;
        value[key] = answer;
    }
    return answer;
}

class Folder {
    children() {
        return this;
    }
}

function MBeansController($scope, $resource, $location, workspace) {
    $scope.tree = {};
    //var resourceUrl = $location.protocol() + '://' + $location.host() + ":" + $location.port() + '/jolokia/list?maxDepth=2';
    var resourceUrl = '/jolokia/list?maxDepth=2';
    console.log("Using url: " + resourceUrl);
    var Model = $resource(resourceUrl);
    console.log("About to load results from model");
    var results = Model.get(function () {
        console.log("Loaded results!");
        var tree = new Folder();
        var domains = results.value;
        for (var domain in domains) {
            var mbeans = domains[domain];
            for (var path in mbeans) {
                var entries = {};
                var items = path.split(',');
                items.forEach(item => {
                    var kv = item.split('=');
                    entries[kv[0]] = kv[1] || kv[0];
                });

                // lets update the tree model
                var typeName = entries['type'];
                var name = entries['name'];

                var mbeanInfo = {
                    domain:domain,
                    name:name,
                    typeName:typeName,
                    path:path,
                    objectName:domain + ":" + path,
                    entries:entries
                };
                if (typeName) {
                    // TODO map domain names to something other than JMX?
                    var domainTree = getOrElse(tree, domain, new Folder());
                    if (name) {
                        var typeMap = getOrElse(domainTree, typeName, new Folder());
                        typeMap[name] = mbeanInfo;
                    } else {
                        domainTree[typeName] = mbeanInfo;
                    }
                } else {
                    console.log("WARNING: domain " + domain + " has mbean " + path + " with no type "
                            + JSON.stringify(entries));
                }
            }
        }
        $scope.results = results;
        $scope.tree = tree;
    });

    $scope.select = (node) => {
        workspace.selection = node;

        // TODO we may want to choose different views based on the kind of selection
        $location.path('/detail');
    }
}

function DetailController($scope, $routeParams, workspace, $rootScope) {
    $scope.routeParams = $routeParams;
    $scope.workspace = workspace;

    $scope.$watch('workspace.selection', function () {
        var node = $scope.workspace.selection;
        workspace.closeHandle($scope);
        var mbean = node.objectName;
        if (mbean) {
            var jolokia = workspace.jolokia;
            // lets get the values immediately
            var query = { type:"READ", mbean:mbean};
            jolokia.request(
                    query,
                    { success:function (response) {
                        $scope.attributes = response.value;
                        $scope.$apply();
                    },
                        error:function (response) {
                            alert("Jolokia request failed: " + response.error);
                        }
                    }
            );
            // listen for updates
            $scope.jolokiaHandle = jolokia.register(function (response) {
                        $scope.attributes = response.value;
                        $scope.$apply();
                    },
                    query);
        }
    });

    $scope.$on('$destroy', function () {
        workspace.closeHandle($scope);
    });
}