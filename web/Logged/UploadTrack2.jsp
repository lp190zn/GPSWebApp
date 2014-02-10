<%@page import="org.apache.tomcat.util.codec.binary.StringUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="en">
    <head>
        <meta charset="Windows-1250">
        <title>Fill info about track</title>

        <link rel="stylesheet" href="https://netdna.bootstrapcdn.com/font-awesome/4.0.0/css/font-awesome.css">

        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">

        <link href="HTMLStyle/HomePageStyle/css/bootstrap.min.css" rel="stylesheet">
        <link href="HTMLStyle/HomePageStyle/css/style.css" rel="stylesheet">

        <script type="text/javascript" src="HTMLStyle/HomePageStyle/js/jquery.min.js"></script>
        <script type="text/javascript" src="HTMLStyle/HomePageStyle/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="HTMLStyle/HomePageStyle/js/scripts.js"></script>
        <script type="text/javascript" src="HTMLStyle/civem-0.0.7.min.js"></script>
        <script type="text/javascript" src="HTMLStyle/civem-0.0.7.js"></script>
        
        

        <script language="javascript" type="text/javascript">

    
        $(document).ready(function() {
            if(document.getElementById('trackName').validity.patternMismatch)
            {
                message = document.getElementById('trackName').dataset.patternError;
            }
            });
        
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
                    <div class="tabbable" id="tabs-883724">
                        <ul class="nav nav-tabs">
                            <li class="active">
                                <a href="#panel-234896" data-toggle="tab">Track upload</a>
                            </li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane active" id="panel-234896">

                                <h3>
                                    Fill track info (Step 2)
                                </h3>
                                <br>

                                <div class="container">
                                    <div class="row clearfix">
                                        <div class="col-md-3 column"></div>
                                        <div class="col-md-4 column">
                                            <form action="SaveTrackInfo" method="post" enctype="multipart/form-data">
                                                <div class="form-group">
                                                    
                                                    <label for="TrackName">Track name</label><input id="trackName" name="trkName" type="text" value="<%out.print(session.getAttribute("trackFilename").toString().substring(0, 
                                                            session.getAttribute("trackFilename").toString().lastIndexOf(".gpx")));%>" required="required" class="form-control" pattern="[A-Za-z0-9_\-+() ]*" data-errormessage-pattern-mismatch="Only alphanumeric characters, whitespace and _ ( ) - + symbols is supported!!!)" />
                                                 
                                                </div>
                                                    <% if(session.getAttribute("trackNameExist").toString().equals("True")){
                                                        out.print("<script> alert(\"This trackname has already been used!\"); </script>");
                                                    }
                                                    %> 
                                                    
                                                <div class="form-group">
                                                    <label for="TrackDesc">Track description</label><textarea id="desc" class="form-control" name="descr" rows="3" onkeyup="this.value = this.value.replace(/[^a-z|0-9|A-Z|_| |+|\-|(|)]/, '')" onkeypress="this.value = this.value.replace(/[^a-z|0-9|A-Z|_| |+|\-|(|)]/, '')"></textarea>
                                                    
                                                </div>
                                                <div class="form-group">
                                                    <label for="TrackActivity">Activity</label> 
                                                    <select name="Activity" class="form-control">
                                                    <option value="Hiking">Hiking</option>
                                                    <option value="Climbing">Climbing</option>
                                                    <option value="Moto cycling">Moto cycling</option>
                                                    <option value="Road tripping">Road tripping</option>
                                                    <option value="Road cycling">Road cycling</option>
                                                    <option value="Mountain biking">Mountain biking</option>
                                                    <option value="Sailing">Sailing</option>
                                                    <option value="Canoeing">Canoeing</option>
                                                    <option value="Windsurfing">Windsurfing</option>
                                                    <option value="Kiteboarding">Kiteboarding</option>
                                                    <option value="Paragliding">Paragliding</option>
                                                    <option value="Flying">Flying</option>
                                                    </select>                                                    
                                                </div>
                                                    
                                                <div class="form-group">
                                                    <label for="TrackAccess">Access</label> 
                                                    <select name="Activity" class="form-control">
                                                    <option value="Private">Private</option>
                                                    <option value="Public">Public</option>
                                                   
                                                    </select>                                                    
                                                </div>
                                                    <br>
                                                    <p style="line-height: 20px; text-align: center;"> <button type="submit" class="btn btn-default btn-success ">Final step</button></p>
                                            </form>
                                        </div>
                                        <div class="col-md-4 column"></div>
                                    </div>
                                </div>
                            </div>
                           

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>


