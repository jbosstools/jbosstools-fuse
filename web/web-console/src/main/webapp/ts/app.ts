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
                //console.log('Closing the handle ' + handle);
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

function onSuccess(fn, options = {}) {
  options['error'] = function (response) {
    alert("Jolokia request failed: " + response.error);
  };
  options['success'] = function (response) {
    fn(response);
  };
  return options;
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

function MBeansController($scope, $location, workspace) {
  $scope.tree = new Folder();

  $scope.select = (node) => {
    workspace.selection = node;

    // TODO we may want to choose different views based on the kind of selection
    $location.path('/detail');
  }

  function populateTree(response) {
    var tree = new Folder();
    var domains = response.value;
    for (var domain in domains) {
      var mbeans = domains[domain];
      for (var path in mbeans) {
        var entries = {};
        var folder = getOrElse(tree, domain, new Folder());
        var items = path.split(',');
        var paths = [];
        items.forEach(item => {
          var kv = item.split('=');
          var key = kv[0];
          var value = kv[1] || key;
          entries[key] = value;
          paths.push(value);
        });

        var lastPath = paths.pop();
        paths.forEach(value => {
          folder = getOrElse(folder, value, new Folder());
        });
        var mbeanInfo = {
          domain:domain,
          path:path,
          paths:paths,
          objectName:domain + ":" + path,
          entries:entries
        };
        folder[lastPath] = mbeanInfo;
      }
    }
    // TODO we should do a merge across...
    // so we only insert or delete things!
    $scope.tree = tree;
    $scope.$apply();
  }

  var jolokia = workspace.jolokia;
  jolokia.request(
          {type:'list'},
          onSuccess(populateTree, {canonicalProperties:false, maxDepth:2}));

  // TODO auto-refresh the tree...
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
              onSuccess(function (response) {
                $scope.attributes = response.value;
                $scope.$apply();
              })
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
