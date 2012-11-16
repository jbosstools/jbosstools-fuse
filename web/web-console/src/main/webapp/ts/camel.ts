

function CamelController($scope, workspace) {
  $scope.workspace = workspace;
  $scope.routes = [];

  $scope.$watch('workspace.selection', function () {
    // TODO could we refactor the get mbean thingy??
    var selection = workspace.selection;
    if (selection) {
      var mbean = selection.objectName;
      if (mbean) {
        var jolokia = workspace.jolokia;
        jolokia.request(
                {type: 'exec', mbean: mbean, operation: 'dumpRoutesAsXml()'},
                onSuccess(populateTable));
      }
    }
  });

  var populateTable = function (response) {
    var data = response.value;
    $scope.routes = data;
    var nodes = [];
    var links = [];
    if (data) {
      var doc = $.parseXML(data);
      var allRoutes = $(doc).find("route");

      var canvasDiv = $('#canvas');
      var width = canvasDiv.width();
      var height = canvasDiv.height();
      if (height < 300) {
        console.log("browse thinks the height is only " + height + " so calculating offset from doc height");
        height = $(document).height() - canvasDiv.offset()['top'] - 5;
      }

      console.log("Using width " + width + " and height " + height);

      var delta = 150;
      function addChildren(parent, parentId, parentX, parentY) {
        var x = parentX;
        var y = parentY + delta;
        $(parent).children().each((idx, route) => {
          var id = nodes.length;
          // from acts as a parent even though its a previous sibling :)
          if (route.nodeName === "from" && !parentId) {
            parentId = id;
          }
          var name = route.nodeName;
          var uri = route.getAttribute("uri");
          if (uri) {
            name += " " + uri;
          }
          var imageName = route.nodeName;

          var endpointNames = ["from", "to", "route"];
          var genericNames = ["xpath", "when", "otherwise"];

          //if (imageName === "from" || imageName === "to" || imageName === "route") {
          if (endpointNames.indexOf(imageName) >= 0) {
            imageName = "endpoint";
          } else if (genericNames.indexOf(imageName) >= 0) {
            // TODO have better mapping here generated from existing image names!
            imageName = "generic";
          }
          var imageUrl = "/img/camel/" + imageName + "24.png";
          console.log("Image URL is " + imageUrl);
          nodes.push({ "name": name, "group": 1, "x": x, "y:": y, "imageUrl": imageUrl });
          if (parentId !== null) {
            console.log(parent.nodeName + "(" + parentId + " @" + parentX + "," + parentY+ ")" + " -> " + route.nodeName + "(" + id + " @" + x + "," + y + ")");
            links.push({"source": parentId, "target": id, "value": 1});
          }
          addChildren(route, id, x, y);
          x += delta;
        });
      }

      var routeDelta = width / allRoutes.length;
      var rowX = 0;
      allRoutes.each((idx, route) => {
        addChildren(route, null, rowX, 0);
        rowX += routeDelta;
      });

      /*
       //var layouter = new Graph.Layout.Spring(g);
       var layouter = new Graph.Layout.Spring(g);
       var renderer = new Graph.Renderer.Raphael('canvas', g, width, height);
       layouter.layout();
       renderer.draw();
       */

      var svg = d3.select("#canvas").append("svg")
              .attr("width", width)
              .attr("height", height);

      var force = d3.layout.force()
              //.gravity(.05)
              .distance(100)
              .charge(-120 * 10)
              .linkDistance(50)
              .size([width, height]);

      /*
       var force = d3.layout.force()
       .gravity(.05)
       .distance(100)
       .charge(-100)
       .size([width, height]);
       */

      // prepare the arrows
      svg.append("svg:defs").selectAll("marker")
              .data(["from"])
              .enter().append("svg:marker")
              .attr("id", String)
              .attr("viewBox", "0 -5 10 10")
              .attr("refX", 25)
              .attr("refY", -1.5)
              .attr("markerWidth", 6)
              .attr("markerHeight", 6)
              .attr("orient", "auto")
              .append("svg:path")
              .attr("d", "M0,-5L10,0L0,5");


      force.nodes(nodes)
              .links(links)
              .start();

      var link = svg.selectAll(".link")
              .data(links)
              .enter().append("line")
              .attr("class", "link");

      // draw the arrow
      link.attr("class", "link from");

      // end marker
      link.attr("marker-end", "url(#from)");

      var node = svg.selectAll(".node")
              .data(nodes)
              .enter().append("g")
              .attr("class", "node")
              .call(force.drag);

      node.append("image")
              .attr("xlink:href", function(d) {
                return d.imageUrl;
              })
              .attr("x", -15)
              .attr("y", -15)
              .attr("width", 30)
              .attr("height", 30);

      node.append("text")
              .attr("dx", 20)
              .attr("dy", ".35em")
              .text(function (d) {
                return d.name
              });

      force.on("tick", function () {
        link.attr("x1", function (d) {
          return d.source.x;
        })
                .attr("y1", function (d) {
                  return d.source.y;
                })
                .attr("x2", function (d) {
                  return d.target.x;
                })
                .attr("y2", function (d) {
                  return d.target.y;
                });

        node.attr("transform", function (d) {
          return "translate(" + d.x + "," + d.y + ")";
        });
      });
    }
    $scope.$apply();
  };
}
