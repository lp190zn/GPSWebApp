<%-- 
    Document   : DrawTrack
    Created on : 29.1.2014, 13:38:37
    Author     : eLeMeNt
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="Windows-1250">
        <title>Draw your track</title>

        <link rel="stylesheet" href="https://netdna.bootstrapcdn.com/font-awesome/4.0.0/css/font-awesome.css">

        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">

        <link href="HTMLStyle/HomePageStyle/css/bootstrap.min.css" rel="stylesheet">
        <link href="HTMLStyle/HomePageStyle/css/style.css" rel="stylesheet">
        
        <link href="http://vjs.zencdn.net/4.3/video-js.css" rel="stylesheet">
        
        <link type="text/css" rel="stylesheet" href="HTMLStyle/GalleryStyle/themes/classic/galleria.classic.css">

        <script type="text/javascript" src="HTMLStyle/HomePageStyle/js/jquery.min.js"></script>
        <script type="text/javascript" src="HTMLStyle/HomePageStyle/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="HTMLStyle/HomePageStyle/js/scripts.js"></script>
        
        <script src="http://vjs.zencdn.net/4.3/video.js"></script>
        
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.js"></script>
    	<script src="HTMLStyle/GalleryStyle/galleria-1.3.3.min.js"></script>
        <script type="text/javascript" src="HTMLStyle/GalleryStyle/themes/classic/galleria.classic.min.js"></script>
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    

        <style>
                      
            #map_canvas {
                
                display: block;
                width: 100%;
                height: 750px;
            }

        </style>

        <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBH31FxBV_cLA7hdbY2dBTUsJjAaDEE0MI&sensor=true"></script>
        <script>

            var isPolyCr = false;
            var polylineOK = null;
            var list = [];

            function initialize() {
                var mapOptions = {
                    zoom: 17,
                    center: new google.maps.LatLng(48.730861,21.244630),
                    mapTypeId: google.maps.MapTypeId.HYBRID
                    };
                    
                var map = new google.maps.Map(document.getElementById('map_canvas'),mapOptions);


                google.maps.event.addListener(map, 'click', function(e) {

                if (isPolyCr == false) {
                    polylineOK = new google.maps.Polyline({
                         path: list,
                         strokeColor: '#FF0000',
                         geodesic: true,
                         strokeOpacity: 1.0,
                         strokeWeight: 2,
                         editable: true
                         });
                }
                placeMarker(e.latLng, map);
                });
            }

           function placeMarker(position, map) {
//                var marker = new google.maps.Marker({
//                position: position,
//                map: map
//                });
                list = polylineOK.getPath();
                map.panTo(position);
                list.push(position);
                polylineOK.setPath(list);
                polylineOK.setMap(map);
                
            }

       google.maps.event.addDomListener(window, 'load', initialize);
      </script>
      
      
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
                                <li>
                                    <a href="ShowTracks.jsp">My Tracks</a>
                                </li>
                                <li class="dropdown active">
                                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Create track<strong class="caret"></strong></a>
                                    <ul class="dropdown-menu">
                                         <li>
                                            <a href="UploadTrack1.jsp">Upload track</a>
                                        </li>

                                        <li class="divider">
                                        </li>
                                        <li>
                                            <a href="DrawTrack.jsp">Write new track</a>
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
                                    <a href="About.jsp">About</a>
                                </li>
                                <li class="dropdown">
                                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-user"></i>  Account<strong class="caret"></strong></a>
                                    <ul class="dropdown-menu">
                                        <li>
                                            <a href="ShowUserInfo.jsp">View account</a>
                                        </li>
                                        <li>
                                            <a href="EditAccount.jsp">Edit account</a>
                                        </li>
                                        <li>
                                            <a href="DeleteUser.jsp">Delete account</a>
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
                                        					                                            
                                        <h3> Draw your track </h3>
                                        <br>

                                        <div id="map_canvas"></div>
   
			</div>
                    </div>
                </div>
    </body>
</html>
