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
          jolokia.start(5000);

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
  options['ignoreErrors'] = true;
  options['mimeType'] = 'application/json';
  if (!options['error']) {
    options['error'] = function (response) {
      //alert("Jolokia request failed: " + response.error);
      console.log("Jolokia request failed: " + response.error);
    };
  }
  options['success'] = function (response) {
    fn(response);
  };
  return options;
}

class Folder {
  constructor(public title: string) {
  }
  isFolder = true;
  children = [];
  map = {};

  getOrElse(key: string, defaultValue: any = new Folder(key)): Folder {
    var answer = this.map[key];
    if (!answer) {
      answer = defaultValue;
      this.map[key] = answer;
      this.children.push(answer)
    }
    return answer;
  }
}

function MBeansController($scope, $location, workspace) {
  $scope.tree = new Folder('MBeans');

  $scope.select = (node) => {
    workspace.selection = node;

    // TODO we may want to choose different views based on the kind of selection
    $location.path('/detail');
    $scope.$apply();
  };

  function populateTree(response) {
    var tree = new Folder('MBeans');
    var domains = response.value;
    for (var domain in domains) {
      var mbeans = domains[domain];
      for (var path in mbeans) {
        var entries = {};
        var folder = tree.getOrElse(domain);
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
          folder = folder.getOrElse(value);
        });
        var mbeanInfo = {
          title:lastPath,
          domain:domain,
          path:path,
          paths:paths,
          objectName:domain + ":" + path,
          entries:entries
        };
        folder.getOrElse(lastPath, mbeanInfo);
      }
    }
    // TODO we should do a merge across...
    // so we only insert or delete things!
    $scope.tree = tree;
    $scope.$apply();

    $("#jmxtree").dynatree({
                onActivate: function(node) {
                  var data = node.data;
                  console.log("You activated " + data.title + " : " + JSON.stringify(data));
                  $scope.select(data);
                },
                persist: true,
                debugLevel: 0,
                children: tree.children
            });
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
      var updateValues = function (response) {
        if (response.value) {
          $scope.attributes = response.value;
          $scope.$apply();
        } else {
          console.log("Failed to get a response! " + response);
        }
      };

      // lets get the values immediately
      var query = { type:"READ", mbean:mbean, ignoreErrors:true};
      jolokia.request(query, onSuccess(updateValues));

      // listen for updates
      $scope.jolokiaHandle = jolokia.register(onSuccess(updateValues,
              {
                error: function(response) {
                  //console.log("Custom error handling on response " + JSON.stringify(response));
                  updateValues(response);
                }
              }),
              query);
    }
  });

  $scope.$on('$destroy', function () {
    workspace.closeHandle($scope);
  });
}
