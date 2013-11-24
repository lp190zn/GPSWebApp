<%@page import="Database.DBTrackFinder"%>
<%@page import="Parser.TLVLoader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<%
    TLVLoader loader = new TLVLoader();
    String system = System.getProperty("os.name");
    int trkID = Integer.parseInt(request.getParameter("trkID"));
    DBTrackFinder trackFinder = new DBTrackFinder();
    String path = trackFinder.getTrackFilePath(trkID);
    String file = trackFinder.getTrackFileName(trkID);
    if (system.startsWith("Windows")) {
        path = path.replaceAll("/", "\\\\"); // vymazat pri pouziti na serveri LINUX!!!
    }
    loader.readTLVFile(path, file);
%>
<html lang="en">
    <head>
        <meta charset="Windows-1250">
        <title>Example track</title>

        <link rel="stylesheet" href="https://netdna.bootstrapcdn.com/font-awesome/4.0.0/css/font-awesome.css">

        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">

        <link href="HTMLStyle/HomePageStyle/css/bootstrap.min.css" rel="stylesheet">
        <link href="HTMLStyle/HomePageStyle/css/style.css" rel="stylesheet">

        <script type="text/javascript" src="HTMLStyle/HomePageStyle/js/jquery.min.js"></script>
        <script type="text/javascript" src="HTMLStyle/HomePageStyle/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="HTMLStyle/HomePageStyle/js/scripts.js"></script>

        <style>

            #map_canvas {

                width: 800px;
                height: 670px;
                margin-left: auto ;
                margin-right: auto ;
            }

        </style>

        <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBH31FxBV_cLA7hdbY2dBTUsJjAaDEE0MI&sensor=true"></script>
        <script>

                    var map_options = {
                    center: <% out.print("new google.maps.LatLng(" + loader.getTrackPoints().get(0).getLatitude() + ", " + loader.getTrackPoints().get(0).getLongitude() + "),"); %>
                    mapTypeId: google.maps.MapTypeId.HYBRID
                    };
                    
                    var map;
                    var polylineOK = null;
                    var isEnd = false;
                    var infowindow;
                    var contentString;
                    var marker;
                    
            <%
                out.print("var polylineCoordinatesList = [\n");
                for (int i = 0; i < loader.getTrackPoints().size(); i++) {
                    out.print("new google.maps.LatLng(" + loader.getTrackPoints().get(i).getLatitude() + ", " + loader.getTrackPoints().get(i).getLongitude() + ")");
                    if (i != loader.getTrackPoints().size() - 1) {
                        out.println(",");
                    }
                }
                out.print("\n];");
            %>

            function initialize() {

            var bounds = new google.maps.LatLngBounds();
            
            for (var i = 0; i < polylineCoordinatesList.length; i++) {
                bounds.extend(polylineCoordinatesList[i]);
            }

            map_canvas = document.getElementById('map_canvas');
            map = new google.maps.Map(map_canvas, map_options);
            

                    polylineOK = new google.maps.Polyline({
                    path: polylineCoordinatesList,
                            strokeColor: '#3300FF',
                            geodesic: true,
                            strokeOpacity: 1.0,
                            strokeWeight: 2,
                            editable: false
                    });
                    
                    polylineOK.setMap(map);
                    map.fitBounds(bounds);               
  
            }

                
            function draw() {

                    isEnd = false;
                    polylineOK.setPath([]);
                    polylineOK.setMap(null);
                    
                    var a = 0;
                    var polylineCoordinatesListFinal = [];
                    i = 0;
                    
                    polylineOK = new google.maps.Polyline({
                         path: polylineCoordinatesListFinal,
                         strokeColor: '#FF0000',
                         geodesic: true,
                         strokeOpacity: 1.0,
                         strokeWeight: 2,
                         editable: false
                         });
                         
                    function drawingMap() {
                                            
                            polylineCoordinatesListFinal.push(polylineCoordinatesList[a]);
                            polylineOK.setPath(polylineCoordinatesListFinal);
                            polylineOK.setMap(map);
                            
                            
                            
                            setTimeout(function() { a++; if (a < polylineCoordinatesList.length) { if (isEnd != true) drawingMap(); } }, 50);
                    };
                    drawingMap();
            }

            function clearmap() {
            isEnd = true;    
            initialize();
                
            }

            google.maps.event.addDomListener(window, 'load', initialize);        </script>
    </head>

    <body>
        <div class="container">
            <div class="row clearfix">
                <div class="col-md-12 column">
                    <nav class="navbar navbar-default" role="navigation">
                        <div class="navbar-header">
                            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1"> <span class="sr-only">Toggle navigation</span><span class="icon-bar"></span><span class="icon-bar"></span><span class="icon-bar"></span></button> <a class="navbar-brand" href="HomePage.jsp"><i class="fa fa-globe"></i>&nbsp;  GPSWebApp</a>
                        </div>

                        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                            <ul class="nav navbar-nav">
                                <li>
                                    <a href="HomePage.jsp">Home</a>
                                </li>
                                <li class="active">
                                    <a href="ShowTracks.jsp">My Tracks</a>
                                </li>
                                <li class="dropdown">
                                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Upload track<strong class="caret"></strong></a>
                                    <ul class="dropdown-menu">
                                        <li>
                                            <a href="UploadFile.jsp">Upload track only</a>
                                        </li>
                                        <li>
                                            <a href="UploadFile.jsp">Upload track with multimedia files</a>
                                        </li>

                                        <li class="divider">
                                        </li>
                                        <li>
                                            <a href="#">Write new track</a>
                                        </li>                                      
                                    </ul>
                                </li>
                            </ul>
                            <form class="navbar-form navbar-left" role="search">
                                <div class="form-group">
                                    <input type="text" class="form-control home-search">
                                </div> <button type="submit" class="btn btn-default">Find</button>
                            </form>
                            <ul class="nav navbar-nav navbar-right">
                                <li>
                                    <a href="#">About</a>
                                </li>
                                <li class="dropdown">
                                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-user"></i>  Account<strong class="caret"></strong></a>
                                    <ul class="dropdown-menu">
                                        <li>
                                            <a href="#">View account</a>
                                        </li>
                                        <li>
                                            <a href="#">Edit account</a>
                                        </li>
                                        <li>
                                            <a href="#">Delete account</a>
                                        </li>
                                        <li class="divider">
                                        </li>
                                        <li>
                                            <a href="../Logout.jsp">Logout</a>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </div>

                    </nav>
                    
                    <div class="tabbable" id="tabs-747520">
				<ul class="nav nav-tabs">
					<li class="active">
						<a href="#panel-740839" data-toggle="tab">Track on map</a>
					</li>
					<li>
						<a href="#panel-536799" data-toggle="tab">Track information</a>
					</li>
				</ul>
				<div class="tab-content">
					<div class="tab-pane active" id="panel-740839">
						                                            
                                        <h3> <% out.print(file); %></h3>

                                        <br>

                                        <div id="map_canvas"></div>

                                        <br>
                                        <br>

                                        <p style="line-height: 20px; text-align: center;"><button type="button" class="btn btn-sm btn-sucess" onclick="draw();">Play</button>    <button type="button" class="btn btn-sm btn-danger" onclick="clearmap();">Clear</button></p>

					</div>
					<div class="tab-pane" id="panel-536799">
                                        
                                        <h3>Info and multimedia files</h3>

                                        <br>

                                        <div class="col-md-4">

                                        <label for="TrackDesc">Track description</label>
                                        <h5> <% out.println(loader.getTrackDescription()); %> </h5>

                                        <label for="TrackActivity">Track activity</label>
                                        <h5> <% out.println(loader.getTrackType());%> </h5>
                                        </div>

                                        <div class="col-md-8">

                                        
					</div>
				</div>
			</div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
