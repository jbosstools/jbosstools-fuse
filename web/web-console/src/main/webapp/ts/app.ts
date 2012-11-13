angular.module('FuseIDE', ['ngResource']).
        config(($routeProvider) => {
          $routeProvider.
                  when('/preferences', {templateUrl: 'partials/preferences.html'}).
                  when('/attributes', {templateUrl: 'partials/attributes.html', controller: DetailController}).
                  when('/logs', {templateUrl: 'partials/logs.html', controller: LogController}).
                  when('/debug', {templateUrl: 'partials/debug.html', controller: DetailController}).
                  when('/about', {templateUrl: 'partials/about.html', controller: DetailController}).
                  otherwise({redirectTo: '/attributes'});
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
//when('/phones/:phoneId', {templateUrl: 'partials/phone-attributes.html', controller: PhoneDetailCtrl}).

var logQueryMBean = 'org.fusesource.insight:type=LogQuery';

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

function asQuery(mbeanName) {
  return { type: "READ", mbean: mbeanName, ignoreErrors: true};
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


  getLocalStorage(key:string) {
    if (supportsLocalStorage()) {
      return localStorage[key];
    }
    return this.dummyStorage[key];
  }

  setLocalStorage(key:string, value:any) {
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

function NavBarController($scope, $location, workspace) {
  $scope.workspace = workspace;

  $scope.navClass = (page) => {
      var currentRoute = $location.path().substring(1) || 'home';
      return page === currentRoute ? 'active' : '';
  };

  $scope.hasMBean = (objectName) => {
    var workspace = $scope.workspace;
    if (workspace) {
      var tree = workspace.tree;
      if (tree) {
        var folder = tree[objectName];
        if (folder) return true;
      }
    }
    return false
  }
}

function PreferencesController($scope, workspace) {
  $scope.workspace = workspace;
  $scope.updateRate = workspace.getUpdateRate();

  $scope.$watch('updateRate', () => {
    $scope.workspace.setUpdateRate($scope.updateRate);
  });
}

function MBeansController($scope, $location, workspace) {
  $scope.workspace = workspace;
  $scope.tree = new Folder('MBeans');

  $scope.select = (node) => {
    $scope.workspace.selection = node;

    // TODO we may want to choose different views based on the kind of selection
    var mbean = node['objectName']
    if (mbean && mbean === logQueryMBean) {
      $location.path('/logs');
    } else {
      $location.path('/attributes');
    }
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
    var workspace2 = $scope.workspace;
    if (workspace2) {
      workspace2.tree = tree;
    }
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

class Table {
  public columns = {};
  public rows = {};

  public values(row, columns) {
    var answer = [];
    if (columns) {
      for (name in columns) {
        console.log("Looking up: " + name + " on row ");
        answer.push(row[name]);
      }
/*
      Object.keys(columns).forEach((name) => {
        answer.push(row[name]);
      });
*/
    }
    return answer;
  }

  public setRow(key, data) {
    this.rows[key] = data;
    Object.keys(data).forEach((key) => {
      // could store type info...
      var columns = this.columns;
      if (!columns[key]) {
        columns[key] = {name: key};
      }
    });
  }
}

function DetailController($scope, $routeParams, workspace, $rootScope) {
  $scope.routeParams = $routeParams;
  $scope.workspace = workspace;

  $scope.isTable = (value) => {
    return value instanceof Table;
  };

  $scope.getAttributes = (value) => {
    if (angular.isArray(value) && angular.isObject(value[0])) return value;
    if (angular.isObject(value) && !angular.isArray(value)) return [value];
    return null;
  };

  $scope.rowValues = (row, col) => {
    return [row[col]];
  };

  var tidyAttributes = function (attributes) {
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
    var updateValues:any = function (response) {
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
        //console.log("Found mbeans: " + mbeans);
        if (mbeans) {
          query = mbeans.map((mbean) => asQuery(mbean));
          if (query.length === 1) {
            query = query[0];
          } else if (query.length === 0) {
            query = null;
          } else {
            // now lets create an update function for each row which are all invoked async
            $scope.attributes = new Table();
            updateValues = function (response) {
              var attributes = response.value;
              if (attributes) {
                tidyAttributes(attributes);
                var mbean = attributes['ObjectName'];
                var request = response.request;
                if (!mbean && request) {
                  mbean = request['mbean'];
                }
                if (mbean) {
                  var table = $scope.attributes;
                  if (!(table instanceof Table)) {
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
    if (query) {
      // lets get the values immediately
      jolokia.request(query, onSuccess(updateValues));
      var callback = onSuccess(updateValues,
              {
                error: (response) => {
                  updateValues(response);
                }
              });

      // listen for updates
      if (angular.isArray(query)) {
        if (query.length >= 1) {
          var args = [callback].concat(query);
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

function LogController($scope, $location, workspace) {
  $scope.workspace = workspace;
  $scope.logs = [];

  $scope.logClass = (log) => {
    var level = log['level'];
    if (level) {
      var lower = level.toLowerCase();
      if (lower.startsWith("warn")) {
        return "warning"
      } else if (lower.startsWith("err")) {
        return "error";
      } else if (lower.startsWith("debug")) {
        return "info";
      }
    }
    return "";
  };

  var updateValues = function (response) {
    var logs = response.events;
    var toTime = response.toTimestamp;
    if (toTime) {
      $scope.toTime = toTime;
    }
    if (logs) {
      for (var idx in logs) {
        var log = logs[idx];
        if (log) {
          var seq = logs['seq'] || idx;
          //console.log("Found log: " + JSON.stringify(log));
          $scope.logs[seq] = log;
        }
      }
      $scope.$apply();
    } else {
      console.log("Failed to get a response! " + response);
    }
  };

  var query = { type: "READ", mbean: logQueryMBean, ignoreErrors: true};

  var jolokia = workspace.jolokia;
  jolokia.execute(logQueryMBean, "allLogResults", onSuccess(updateValues));

  // TODO IMPLEMENT!!!
  // listen for updates adding the since
}

