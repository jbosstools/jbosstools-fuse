angular.module('FuseIDE', ['ngResource']).
        config(($routeProvider) => {
          $routeProvider.
                  when('/preferences', {templateUrl: 'partials/preferences.html'}).
                  when('/detail', {templateUrl: 'partials/detail.html', controller: DetailController}).
                  when('/debug', {templateUrl: 'partials/debug.html', controller: DetailController}).
                  when('/logs', {templateUrl: 'partials/logs.html', controller: LogController}).
                  otherwise({redirectTo: '/detail'});
        }).
        factory('workspace',function ($rootScope) {
          return new Workspace();
        }).
        filter('humanize', function () {
          return function (value) {
            if (value) {
              var text = value.toString();
              return text.underscore().humanize();
            }
            return value;
          };
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
  options['success'] = fn;
  if (!options['error']) {
    options['error'] = function (response) {
      //alert("Jolokia request failed: " + response.error);
      console.log("Jolokia request failed: " + response.error);
    };
  }
  return options;
}


function supportsLocalStorage() {
  try {
    return 'localStorage' in window && window['localStorage'] !== null;
  } catch (e) {
    return false;
  }
}

class Workspace {
  public jolokia = new Jolokia("/jolokia");
  public updateRate = 0;
  public selection = [];
  dummyStorage = {};

  constructor() {
    var rate = this.getUpdateRate();
    this.setUpdateRate(rate);
  }


  getLocalStorage(key: string) {
    if (supportsLocalStorage()) {
      return localStorage[key];
    }
    return this.dummyStorage[key];
  }

  setLocalStorage(key: string, value: any) {
    if (supportsLocalStorage()) {
      localStorage[key] = value;
    } else {
      this.dummyStorage[key] = value;
    }
  }

  /**
   * Closes the given handle object if its defined
   */
          closeHandle(scope:any, key:string = 'jolokiaHandle') {
    var handle = scope[key];
    if (handle) {
      //console.log('Closing the handle ' + handle);
      this.jolokia.unregister(handle);
      handle[key] = null;
    }
  }

  getUpdateRate() {
    return this.getLocalStorage('updateRate') || 5000;
  }

  /**
   * sets the update rate
   */
          setUpdateRate(value) {
    this.jolokia.stop();
    this.setLocalStorage('updateRate', value)
    if (value > 0) {
      this.jolokia.start(value);
    }
    console.log("Set update rate to: " + value);
  }
}

class Folder {
  constructor(public title:string) {
  }

  isFolder = true;
  children = [];
  map = {};

  getOrElse(key:string, defaultValue:any = new Folder(key)):Folder {
    var answer = this.map[key];
    if (!answer) {
      answer = defaultValue;
      this.map[key] = answer;
      this.children.push(answer)
    }
    return answer;
  }
}

function NavBarController($scope, workspace) {
}

function PreferencesController($scope, workspace) {
  $scope.workspace = workspace;
  $scope.updateRate = workspace.getUpdateRate();

  $scope.$watch('updateRate', () => {
    $scope.workspace.setUpdateRate($scope.updateRate);
  });
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
    // TODO we should do a merge across...
    // so we only insert or delete things!
    $scope.tree = tree;
    $scope.$apply();

    $("#jmxtree").dynatree({
      onActivate: function (node) {
        var data = node.data;
        //console.log("You activated " + data.title + " : " + JSON.stringify(data));
        $scope.select(data);
      },
      persist: false,
      debugLevel: 0,
      children: tree.children
    });
  }

  var jolokia = workspace.jolokia;
  jolokia.request(
          {type: 'list'},
          onSuccess(populateTree, {canonicalNaming: false, maxDepth: 2}));

  // TODO auto-refresh the tree...

}

function DetailController($scope, $routeParams, workspace, $rootScope) {
  $scope.routeParams = $routeParams;
  $scope.workspace = workspace;

  $scope.getAttributes = (value) => {
    if (angular.isArray(value) && angular.isObject(value[0])) return value;
    if (angular.isObject(value) && !angular.isArray(value)) return [value];
    return null;
  };

  var asQuery = function(mbeanName) {
    return { type: "READ", mbean: mbeanName, ignoreErrors: true};
  };

  var tidyAttributes = function(attributes) {
    var objectName = attributes['ObjectName'];
    if (objectName) {
      var name = objectName['objectName'];
      if (name) {
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
    var updateValues: any = function (response) {
      var attributes = response.value;
      if (attributes) {
        tidyAttributes(attributes);
        $scope.attributes = attributes;
        $scope.$apply();
      } else {
        console.log("Failed to get a response! " + response);
      }
    };

    if (mbean) {
      query = asQuery(mbean)
    } else {
      // lets query each child's details
      var children = node.children;
      if (children) {
        var mbeans = children.map((child) => child.objectName).filter((mbean) => mbean);
        console.log("Found mbeans: " + mbeans);
        if (mbeans) {
          query = mbeans.map((mbean) => asQuery(mbean));
          if (query.length === 1) {
            query = query[0];
          } else {
            updateValues = [];
            $scope.attributes = [];

            // now lets create an update function for each row which are all invoked async
            mbeans.each((mbean, index) => {
              updateValues[index] = function (response) {
                    var attributes = response.value;
                    if (attributes) {
                      tidyAttributes(attributes);
                      $scope.attributes[index] = attributes;
                      $scope.$apply();
                    } else {
                      console.log("Failed to get a response! " + response);
                    }
              };
            });
          }
        }
      }
    }
    if (query) {
      // lets get the values immediately
      jolokia.request(query, onSuccess(updateValues));

      // listen for updates
      $scope.jolokiaHandle = jolokia.register(onSuccess(updateValues,
              {
                error: function (response) {
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
