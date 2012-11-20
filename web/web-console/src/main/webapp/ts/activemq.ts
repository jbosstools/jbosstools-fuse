function QueueController($scope, workspace) {
  $scope.workspace = workspace;
  $scope.messages = [];

  var populateTable = function (response) {
    var data = response.value;
    $scope.messages = data;
    $scope.$apply();

    $('#grid').dataTable({
      bPaginate: false,
      sDom: 'Rlfrtip',
      bDestroy: true,
      aaData: data,
      aoColumns: [
        { "mDataProp": "JMSMessageID" },
        {
          "sDefaultContent": "",
          "mData": null,
          "mDataProp": "Text"
        },
        { "mDataProp": "JMSCorrelationID" },
        { "mDataProp": "JMSTimestamp" },
        { "mDataProp": "JMSDeliveryMode" },
        { "mDataProp": "JMSReplyTo" },
        { "mDataProp": "JMSRedelivered" },
        { "mDataProp": "JMSPriority" },
        { "mDataProp": "JMSXGroupSeq" },
        { "mDataProp": "JMSExpiration" },
        { "mDataProp": "JMSType" },
        { "mDataProp": "JMSDestination" }
      ]
    });
  };

  $scope.$watch('workspace.selection', function () {
    // TODO could we refactor the get mbean thingy??
    var selection = workspace.selection;
    if (selection) {
      var mbean = selection.objectName;
      if (mbean) {
        var jolokia = workspace.jolokia;

        jolokia.request(
                {type: 'exec', mbean: mbean, operation: 'browse()'},
                onSuccess(populateTable));
      }
    }
  });

  var sendWorked = () => {
    console.log("Sent message!");
  };

  $scope.sendMessage = (body) => {
    var selection = workspace.selection;
    if (selection) {
      var mbean = selection.objectName;
      if (mbean) {
        var jolokia = workspace.jolokia;
        console.log("Sending message to destination " + mbean);
        jolokia.execute(mbean, "sendTextMessage(java.lang.String)", body, onSuccess(sendWorked));
      }
    }
  };
}

function SubscriberGraphController($scope, workspace) {
  $scope.workspace = workspace;
  $scope.nodes = [];
  $scope.links = [];
  $scope.queues = {};
  $scope.topics = {};
  $scope.subscriptions = {};

  function getOrCreate(container, key, defaultObject) {
    var value = container[key];
    var id;
    if (!value) {
      container[key] = defaultObject;
      id = $scope.nodes.length;
      defaultObject["id"] = id;
      $scope.nodes.push(defaultObject);
    } else {
      id = value["id"];
    }
    return id;
  }

  var populateGraph = function (response) {
    var data = response.value;
    for (var key in data) {
      var subscription = data[key];
      var destinationNameText = subscription["DestinationName"];
      if (destinationNameText) {
        var subscriptionKey = subscription["SubcriptionId"];
        subscription["label"] = subscriptionKey;
        subscription["imageUrl"] = "/img/activemq/listener.gif";
        var subscriptionId = getOrCreate($scope.subscriptions, subscriptionKey, subscription);

        var destinationNames = destinationNameText.split(",");
        destinationNames.forEach((destinationName) => {
            var id = null;
            if (subscription["DestinationTopic"]) {
              id = getOrCreate($scope.topics, destinationName, {
                label: destinationName, imageUrl: "/img/activemq/topic.png" });
            } else {
              id = getOrCreate($scope.queues, destinationName, {
                label: destinationName, imageUrl: "/img/activemq/queue.png" });
            }

            $scope.links.push({ source: id, target: subscriptionId });
            // TODO add connections...?
        });
      }
    }
    d3ForceGraph($scope, $scope.nodes, $scope.links);
    $scope.$apply();
  };

  $scope.$watch('workspace.selection', function () {
    var isQueue = true;
    var jolokia = $scope.workspace.jolokia;
    if (jolokia) {
      var selection = $scope.workspace.selection;
      if (selection) {
        if (selection.entries) {
          isQueue = selection.entries["Type"] !== "Topic";
        } else if (selection.folderNames) {
          isQueue = selection.folderNames.last() !== "Topic";
        }
      }
      // TODO detect if we're looking at topics
      var typeName;
      if (isQueue) {
        typeName = "Queue";
      } else {
        typeName = "Topic";
      }
      jolokia.request(
              {type: 'read', mbean: "org.apache.activemq:Type=Subscription,destinationType=" + typeName + ",*" },
              onSuccess(populateGraph));
    }
  });
}