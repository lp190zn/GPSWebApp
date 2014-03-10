/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Parser;

import File.FileImpl;
import File.Image.ImageResizer;
import File.Image.ThumbnailException;
import File.TrackPointImpl;
import File.Video.YouTubeAgent;
import Parser.Utilities.ElevationLoader;
import Parser.Utilities.LocationResolver;
import Parser.Utilities.MultimediaSearcher;
import Parser.Utilities.TimezoneLoader;
import Parser.Utilities.TrackDetailResolver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

/**
 *
 * @author matej_000
 */
public class GPXParser {
    
    private String path;
    private String os = System.getProperty("os.name");
    private File gpxFile;
    private File destFolder;
    private boolean isLoadedElevationsFromServer = false;
    private static ArrayList<String> latitude = new ArrayList<String>();
    private static ArrayList<String> longitude = new ArrayList<String>();
    private static ArrayList<String> deviceElevation = new ArrayList<String>();
    private static ArrayList<String> serverElevation = null;
    private ArrayList<FileImpl> files = new ArrayList<FileImpl>();
    private static ArrayList<Date> time = new ArrayList<Date>();
    private ArrayList<TrackPointImpl> track = new ArrayList<TrackPointImpl>();
    private DateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat formEU = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private ArrayList<Double> trackSpeed = null;
    
    private String trackDBName;
    private String trackDBUser;
    private YouTubeAgent youTubeAgent = new YouTubeAgent("skuska.api3@gmail.com", "skuskaapi3");
    
    private String startAddress = "NONE";
    private String endAddress = "NONE";
    
    private String trackDuration = "NONE";
    private String trackMaxElevation = "NONE";
    private String trackMinElevation = "NONE";
    private String trackHeightDiff = "NONE";
    private String trackLengthKm = "NONE";
    
    private boolean isDrawed = false;
    
    public GPXParser(String pathToFiles, String sourceFile, String trackUser, String trackName){
        trackDBUser = trackUser;
        trackDBName = trackName;
        
        gpxFile = new File(pathToFiles + sourceFile);
        
        String destFile = sourceFile.substring(0, sourceFile.indexOf(".gpx"));
        destFolder = new File(pathToFiles + destFile);
        
        //this.readGpx();
    }
    
    public void readGpx() {
        latitude.clear();
        longitude.clear();
        deviceElevation.clear();
        serverElevation = null;
        time.clear();
        getTrack().clear();

        try {
            DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
            DocumentBuilder DB = DBF.newDocumentBuilder();
            org.w3c.dom.Document DOC = DB.parse(gpxFile);
            DOC.getDocumentElement().normalize();
            Node gpxStartNode = DOC.getElementsByTagName("gpx").item(0);
            Element gpxStartElem = (Element) gpxStartNode;

            NodeList gpxStartNode1 = gpxStartElem.getElementsByTagName("trk");
            
            for (int a = 0; a < gpxStartNode1.getLength(); a++) {
                
                Element gpxStartElem1 = (Element) gpxStartNode1.item(a);
                
                Node gpxStartNode2 = gpxStartElem1.getElementsByTagName("trkseg").item(0);
                Element gpxStartElem2 = (Element) gpxStartNode2;
                
                NodeList gpxStartNode3 = gpxStartElem1.getElementsByTagName("trkpt");
                
                for (int i = 0; i < gpxStartNode3.getLength(); i++) {
                    Element trackPointElement = (Element) gpxStartNode3.item(i);
                    
                    latitude.add(trackPointElement.getAttribute("lat"));
                    longitude.add(trackPointElement.getAttribute("lon"));
                    
                    Node trackPointNode2 = trackPointElement.getElementsByTagName("ele").item(0);
                    Element trackPointElement2 = (Element) trackPointNode2;
                    deviceElevation.add(trackPointElement2.getTextContent());
                    
                    Node trackPointNode3 = trackPointElement.getElementsByTagName("time").item(0);
                    Element trackPointElement3 = (Element) trackPointNode3;
                    String temp = trackPointElement3.getTextContent().replace('T', ' ').substring(0, trackPointElement3.getTextContent().length() - 1);
                    if (trackPointElement3.getTextContent().toUpperCase().endsWith("Z")) {
                        Date tempDate = (Date) form.parse(temp);
                        time.add(tempDate);
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("Error: Cannot read file");
        } finally {
            for (int i = 0; i < latitude.size(); i++) {
                TrackPointImpl tempTP = new TrackPointImpl();
                tempTP.setLatitude(Double.parseDouble(latitude.get(i)));
                tempTP.setLongitude(Double.parseDouble(longitude.get(i)));
                tempTP.setDeviceElevation(Integer.parseInt(deviceElevation.get(i).substring(0, deviceElevation.get(i).indexOf("."))));
                tempTP.setTime(time.get(i));
                getTrack().add(tempTP);
            }
        }
    }
    
    public void parseGpx(String trackType, String trackDescr) {
        if (gpxFile != null && destFolder != null) {
            if (destFolder.getAbsolutePath().toLowerCase().endsWith(".tlv")) {
                path = destFolder.getAbsolutePath();
            }
            if (!destFolder.getAbsolutePath().toLowerCase().endsWith(".tlv")) {
                path = destFolder.getAbsolutePath() + ".tlv";
            }

            File f = new File(path);

            if (f.exists()) {
                try {
                    f.delete();
                    f.createNewFile();
                } catch (IOException ex) {
                    System.out.println("Error: Cannot create *.tlv file!!!");
                }
            }

            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    System.out.println("Error: Cannot create *.tlv file!!!");
                }
            }
            try {
                DocumentBuilderFactory DBF1 = DocumentBuilderFactory.newInstance();
                DocumentBuilder DB1 = DBF1.newDocumentBuilder();
                org.w3c.dom.Document document = DB1.newDocument();
                org.w3c.dom.Element rootElement = document.createElement("TLV");
                document.appendChild(rootElement);
                org.w3c.dom.Element rootElement3 = document.createElement("SYSTEM");
                rootElement3.appendChild(document.createTextNode(os));
                rootElement.appendChild(rootElement3);
                org.w3c.dom.Element rootElement2 = document.createElement("FILES");
                if (getFiles().isEmpty() == true) {
                   rootElement2.appendChild(document.createTextNode("null"));
                }
                rootElement.appendChild(rootElement2);

                for (int i = 0; i < getFiles().size(); i++) {
                    
//                    if(getFiles().get(i).getPath().toLowerCase().endsWith(".avi") || getFiles().get(i).getPath().toLowerCase().endsWith(".mov") || getFiles().get(i).getPath().toLowerCase().endsWith(".mp4") || getFiles().get(i).getPath().toLowerCase().endsWith(".3gp")){
//                        //System.out.println("Mam Video: " + files.get(i).getPath());
//                        String videoID = youTubeAgent.uploadVideo(getFiles().get(i), trackDBUser, trackDBName, String.valueOf(i));
//                        getFiles().get(i).setPath("YTB " + videoID);
//                        //System.out.println("Mam Video: " + videoID);
//                    }
                    
                    org.w3c.dom.Element em = document.createElement("File_entity");
                    rootElement2.appendChild(em);
                    org.w3c.dom.Element em1 = document.createElement("path");
                    em1.appendChild(document.createTextNode(getFiles().get(i).getPath().toString()));
                    em.appendChild(em1);

                    org.w3c.dom.Element em2 = document.createElement("creation_date");
                    em2.appendChild(document.createTextNode(String.valueOf(getFiles().get(i).getDate().getTime())));
                    em.appendChild(em2);

                    org.w3c.dom.Element emr = document.createElement("gps_latitude");
                    if (getFiles().get(i).getLatitude() != null) {
                        emr.appendChild(document.createTextNode(getFiles().get(i).getLatitude()));
                    } else {
                        emr.appendChild(document.createTextNode("null"));
                    }
                    em.appendChild(emr);

                    org.w3c.dom.Element emr1 = document.createElement("gps_longitude");
                    if (getFiles().get(i).getLongitude() != null) {
                        emr1.appendChild(document.createTextNode(getFiles().get(i).getLongitude()));
                    } else {
                        emr1.appendChild(document.createTextNode("null"));
                    }
                    em.appendChild(emr1);

                    org.w3c.dom.Element emr2 = document.createElement("gps_elevation");
                    if (getFiles().get(i).getElevation() != null) {
                        emr2.appendChild(document.createTextNode(getFiles().get(i).getElevation()));
                    } else {
                        emr2.appendChild(document.createTextNode("null"));
                    }
                    em.appendChild(emr2);

                }
                
                ElevationLoader eleLoader = new ElevationLoader();
                serverElevation = eleLoader.reclaimElevation(getTrack());
                if(serverElevation.size() == deviceElevation.size()){
                    isLoadedElevationsFromServer = true;
                }else{
                    isLoadedElevationsFromServer = false;
                }

                org.w3c.dom.Element rootElement1 = document.createElement("COORDINATES");
                rootElement.appendChild(rootElement1);

                org.w3c.dom.Element element2 = document.createElement("Track_Type");
                element2.appendChild(document.createTextNode(trackType));
                rootElement1.appendChild(element2);
                
                org.w3c.dom.Element element2_1 = document.createElement("Track_Description");
                element2_1.appendChild(document.createTextNode(trackDescr));
                rootElement1.appendChild(element2_1);
                
                //
                LocationResolver resolver = new LocationResolver();
                ArrayList<String> addresses =  resolver.getStartEndAddressFromTrack(getTrack());
                //
                
                org.w3c.dom.Element element2_2 = document.createElement("Track_Start_Address");
                if(addresses.size() <= 0){
                    
                }else{
                    startAddress = addresses.get(0);
                }
                element2_2.appendChild(document.createTextNode(startAddress));
                rootElement1.appendChild(element2_2);
                
                org.w3c.dom.Element element2_3 = document.createElement("Track_End_Address");
                if(addresses.size() <= 0){
                    
                }else{
                    endAddress = addresses.get(1);
                }
                element2_3.appendChild(document.createTextNode(endAddress));
                rootElement1.appendChild(element2_3);
                
                // Pridane 19.2.2014 Detaily o trase
                TrackDetailResolver trackDetail = new TrackDetailResolver(getTrack(), trackType, serverElevation, isDrawed);
                trackSpeed = trackDetail.resolveTrackSpeed();
                
                org.w3c.dom.Element element2_4 = document.createElement("Track_Length_Km");
                String tempLength = String.valueOf(trackDetail.resolveTrackLength());
                trackLengthKm = tempLength.substring(0, tempLength.lastIndexOf(".") + 4);
                element2_4.appendChild(document.createTextNode(trackLengthKm));
                rootElement1.appendChild(element2_4);
                
                org.w3c.dom.Element element2_5 = document.createElement("Track_Max_Elevation");
                trackMaxElevation = String.valueOf(trackDetail.resolveMaxElevation());
                element2_5.appendChild(document.createTextNode(trackMaxElevation));
                rootElement1.appendChild(element2_5);
                
                org.w3c.dom.Element element2_6 = document.createElement("Track_Min_Elevation");
                trackMinElevation = String.valueOf(trackDetail.resolveMinElevation());
                element2_6.appendChild(document.createTextNode(trackMinElevation));
                rootElement1.appendChild(element2_6);
                
                org.w3c.dom.Element element2_6_1 = document.createElement("Track_Height_Difference");
                trackHeightDiff = String.valueOf(trackDetail.resolveTrackHeightDiff());
                element2_6_1.appendChild(document.createTextNode(getTrackHeightDiff()));
                rootElement1.appendChild(element2_6_1);
                
                org.w3c.dom.Element element2_7 = document.createElement("Track_Duration");
                trackDuration = trackDetail.resolveTrackDuration();
                element2_7.appendChild(document.createTextNode(trackDuration));
                rootElement1.appendChild(element2_7);
                //

                org.w3c.dom.Element element3 = document.createElement("Elevations_type");
                if (isLoadedElevationsFromServer == true) {
                    element3.appendChild(document.createTextNode("INTERNET"));
                } else {
                    element3.appendChild(document.createTextNode("DEVICE"));
                }
                rootElement1.appendChild(element3);

                for (int i = 0; i < latitude.size(); i++) {
                    org.w3c.dom.Element em = document.createElement("TrackPoint");
                    rootElement1.appendChild(em);
                    org.w3c.dom.Element em1 = document.createElement("Latitude");
                    em1.appendChild(document.createTextNode(latitude.get(i)));
                    em.appendChild(em1);

                    org.w3c.dom.Element em2 = document.createElement("Longitude");
                    em2.appendChild(document.createTextNode(longitude.get(i)));
                    em.appendChild(em2);

                    org.w3c.dom.Element em3 = document.createElement("Device_Elevation");
                    em3.appendChild(document.createTextNode(deviceElevation.get(i)));
                    em.appendChild(em3);

                    if (isLoadedElevationsFromServer == true) {
                        org.w3c.dom.Element em3aPol = document.createElement("Internet_Elevation");
                        em3aPol.appendChild(document.createTextNode(serverElevation.get(i)));
                        em.appendChild(em3aPol);
                    }

                    org.w3c.dom.Element em4 = document.createElement("Time");
                    StringBuilder str = new StringBuilder();
                    str = str.append(time.get(i).getTime());
                    String tempStr = str.toString();
                    em4.appendChild(document.createTextNode(tempStr));
                    em.appendChild(em4);
                    
                    //Pridane 19.2.2014 Detaily o trase
                    org.w3c.dom.Element em5 = document.createElement("Speed");
                    String tempSpeed = String.valueOf(trackSpeed.get(i));
                    tempSpeed = tempSpeed.substring(0, tempSpeed.lastIndexOf(".") + 2);
                    em5.appendChild(document.createTextNode(tempSpeed));
                    em.appendChild(em5);
                    //
                }
                TransformerFactory TF = TransformerFactory.newInstance();
                Transformer T = TF.newTransformer();
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(f);
                T.transform(source, result);
                
                DB1.reset();

            } catch (Exception ex) {
                System.out.println("Error: Cannot create *.tlv file!!!");
                ex.printStackTrace();
            }
        }
    }
    
    public void readFromTrackPoints(String pathToTemp, String trackType, String trackDescr){
        latitude.clear();
        longitude.clear();
        deviceElevation.clear();
        serverElevation = null;
        time.clear();
        getTrack().clear();
        isDrawed = true;
        
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(pathToTemp)));
            String latLngStr = reader.readLine();
            
            String tempStr[] = latLngStr.split("[)],");
            int i = 0;
            for (String tempStr1 : tempStr) {
                i++;
                TrackPointImpl point = new TrackPointImpl();
                String temp0 = tempStr1.replaceAll("[)]", "");
                String temp = temp0.replaceAll("[(]", "");
                
                //String temp = tempStr1.substring(1);
                String ttt[] = temp.split(",");
                
                point.setLatitude(Double.parseDouble(ttt[0]));
                latitude.add(ttt[0]);
                point.setLongitude(Double.parseDouble(ttt[1]));
                longitude.add(ttt[1]);
                
                DateFormat dateForm = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                Date date = dateForm.parse("2014:01:01 01:00:00");
                int iii = date.getSeconds();
                
                date.setSeconds(iii + i);
                point.setTime(date);
                time.add(date);
                System.out.println(date);
                deviceElevation.add("0");
                //System.out.println("TOTO TU JE: " + point.getLatitude());
                //System.out.println("TOTO TU JE: " + point.getLongitude());
                //System.out.println("TOTO TU JE: " + point.getTime());
                track.add(point);
            }   
            System.out.println("TOTO TU JE: " + track.size());
            
        } catch (Exception ex) {
            Logger.getLogger(GPXParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(GPXParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void parseFromTrackPoints(String trackType, String trackDescr){
         if (gpxFile != null && destFolder != null) {
            if (destFolder.getAbsolutePath().toLowerCase().endsWith(".tlv")) {
                path = destFolder.getAbsolutePath();
            }
            if (!destFolder.getAbsolutePath().toLowerCase().endsWith(".tlv")) {
                path = destFolder.getAbsolutePath() + ".tlv";
            }

            File f = new File(path);

            if (f.exists()) {
                try {
                    f.delete();
                    f.createNewFile();
                } catch (IOException ex) {
                    System.out.println("Error: Cannot create *.tlv file!!!");
                }
            }

            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    System.out.println("Error: Cannot create *.tlv file!!!");
                }
            }
            try {
                DocumentBuilderFactory DBF1 = DocumentBuilderFactory.newInstance();
                DocumentBuilder DB1 = DBF1.newDocumentBuilder();
                org.w3c.dom.Document document = DB1.newDocument();
                org.w3c.dom.Element rootElement = document.createElement("TLV");
                document.appendChild(rootElement);
                org.w3c.dom.Element rootElement3 = document.createElement("SYSTEM");
                rootElement3.appendChild(document.createTextNode(os));
                rootElement.appendChild(rootElement3);
                org.w3c.dom.Element rootElement2 = document.createElement("FILES");
                if (getFiles().isEmpty() == true) {
                   rootElement2.appendChild(document.createTextNode("null"));
                }
                rootElement.appendChild(rootElement2);

                for (int i = 0; i < getFiles().size(); i++) {
                    
//                    if(getFiles().get(i).getPath().toLowerCase().endsWith(".avi") || getFiles().get(i).getPath().toLowerCase().endsWith(".mov") || getFiles().get(i).getPath().toLowerCase().endsWith(".mp4") || getFiles().get(i).getPath().toLowerCase().endsWith(".3gp")){
//                        //System.out.println("Mam Video: " + files.get(i).getPath());
//                        String videoID = youTubeAgent.uploadVideo(getFiles().get(i), trackDBUser, trackDBName, String.valueOf(i));
//                        getFiles().get(i).setPath("YTB " + videoID);
//                        //System.out.println("Mam Video: " + videoID);
//                    }
                    
                    org.w3c.dom.Element em = document.createElement("File_entity");
                    rootElement2.appendChild(em);
                    org.w3c.dom.Element em1 = document.createElement("path");
                    em1.appendChild(document.createTextNode(getFiles().get(i).getPath().toString()));
                    em.appendChild(em1);

                    org.w3c.dom.Element em2 = document.createElement("creation_date");
                    em2.appendChild(document.createTextNode(String.valueOf(getFiles().get(i).getDate().getTime())));
                    em.appendChild(em2);

                    org.w3c.dom.Element emr = document.createElement("gps_latitude");
                    if (getFiles().get(i).getLatitude() != null) {
                        emr.appendChild(document.createTextNode(getFiles().get(i).getLatitude()));
                    } else {
                        emr.appendChild(document.createTextNode("null"));
                    }
                    em.appendChild(emr);

                    org.w3c.dom.Element emr1 = document.createElement("gps_longitude");
                    if (getFiles().get(i).getLongitude() != null) {
                        emr1.appendChild(document.createTextNode(getFiles().get(i).getLongitude()));
                    } else {
                        emr1.appendChild(document.createTextNode("null"));
                    }
                    em.appendChild(emr1);

                    org.w3c.dom.Element emr2 = document.createElement("gps_elevation");
                    if (getFiles().get(i).getElevation() != null) {
                        emr2.appendChild(document.createTextNode(getFiles().get(i).getElevation()));
                    } else {
                        emr2.appendChild(document.createTextNode("null"));
                    }
                    em.appendChild(emr2);

                }
                
                System.out.println("Kukneme: " + deviceElevation + " " + serverElevation);
                
                ElevationLoader eleLoader = new ElevationLoader();
                serverElevation = eleLoader.reclaimElevation(getTrack());
                if(serverElevation.size() == deviceElevation.size()){
                    isLoadedElevationsFromServer = true;
                }else{
                    isLoadedElevationsFromServer = false;
                }
                //deviceElevation = serverElevation;
                
                System.out.println("Kukneme: " + deviceElevation + " " + serverElevation);

                org.w3c.dom.Element rootElement1 = document.createElement("COORDINATES");
                rootElement.appendChild(rootElement1);

                org.w3c.dom.Element element2 = document.createElement("Track_Type");
                element2.appendChild(document.createTextNode(trackType));
                rootElement1.appendChild(element2);
                
                org.w3c.dom.Element element2_1 = document.createElement("Track_Description");
                element2_1.appendChild(document.createTextNode(trackDescr));
                rootElement1.appendChild(element2_1);
                
                //
                LocationResolver resolver = new LocationResolver();
                ArrayList<String> addresses =  resolver.getStartEndAddressFromTrack(getTrack());
                //
                
                org.w3c.dom.Element element2_2 = document.createElement("Track_Start_Address");
                if(addresses.size() <= 0){
                    
                }else{
                    startAddress = addresses.get(0);
                }
                element2_2.appendChild(document.createTextNode(startAddress));
                rootElement1.appendChild(element2_2);
                
                org.w3c.dom.Element element2_3 = document.createElement("Track_End_Address");
                if(addresses.size() <= 0){
                    
                }else{
                    endAddress = addresses.get(1);
                }
                element2_3.appendChild(document.createTextNode(endAddress));
                rootElement1.appendChild(element2_3);
                
                // Pridane 19.2.2014 Detaily o trase
                TrackDetailResolver trackDetail = new TrackDetailResolver(getTrack(), trackType, serverElevation, isDrawed);
                trackSpeed = trackDetail.resolveTrackSpeed();
                
                org.w3c.dom.Element element2_4 = document.createElement("Track_Length_Km");
                String tempLength = String.valueOf(trackDetail.resolveTrackLength());
                trackLengthKm = tempLength.substring(0, tempLength.lastIndexOf(".") + 4);
                element2_4.appendChild(document.createTextNode(trackLengthKm));
                rootElement1.appendChild(element2_4);
                
                org.w3c.dom.Element element2_5 = document.createElement("Track_Max_Elevation");
                trackMaxElevation = String.valueOf(trackDetail.resolveMaxElevation());
                element2_5.appendChild(document.createTextNode(trackMaxElevation));
                rootElement1.appendChild(element2_5);
                
                org.w3c.dom.Element element2_6 = document.createElement("Track_Min_Elevation");
                trackMinElevation = String.valueOf(trackDetail.resolveMinElevation());
                element2_6.appendChild(document.createTextNode(trackMinElevation));
                rootElement1.appendChild(element2_6);
                
                org.w3c.dom.Element element2_6_1 = document.createElement("Track_Height_Difference");
                trackHeightDiff = String.valueOf(trackDetail.resolveTrackHeightDiff());
                element2_6_1.appendChild(document.createTextNode(getTrackHeightDiff()));
                rootElement1.appendChild(element2_6_1);
                
                org.w3c.dom.Element element2_7 = document.createElement("Track_Duration");
                trackDuration = trackDetail.resolveTrackDuration();
                element2_7.appendChild(document.createTextNode(trackDuration));
                rootElement1.appendChild(element2_7);
                //

                org.w3c.dom.Element element3 = document.createElement("Elevations_type");
                if (isLoadedElevationsFromServer == true) {
                    element3.appendChild(document.createTextNode("INTERNET"));
                } else {
                    element3.appendChild(document.createTextNode("DEVICE"));
                }
                rootElement1.appendChild(element3);

                for (int i = 0; i < track.size(); i++) {
                    org.w3c.dom.Element em = document.createElement("TrackPoint");
                    rootElement1.appendChild(em);
                    org.w3c.dom.Element em1 = document.createElement("Latitude");
                    em1.appendChild(document.createTextNode(Double.toString(track.get(i).getLatitude())));
                    em.appendChild(em1);

                    org.w3c.dom.Element em2 = document.createElement("Longitude");
                    em2.appendChild(document.createTextNode(Double.toString(track.get(i).getLongitude())));
                    em.appendChild(em2);

                    org.w3c.dom.Element em3 = document.createElement("Device_Elevation");
                    em3.appendChild(document.createTextNode(deviceElevation.get(i)));
                    em.appendChild(em3);

                    if (isLoadedElevationsFromServer == true) {
                        org.w3c.dom.Element em3aPol = document.createElement("Internet_Elevation");
                        em3aPol.appendChild(document.createTextNode(serverElevation.get(i)));
                        em.appendChild(em3aPol);
                    }

                    org.w3c.dom.Element em4 = document.createElement("Time");
                    StringBuilder str = new StringBuilder();
                    str = str.append(time.get(i).getTime());
                    String tempStr = str.toString();
                    em4.appendChild(document.createTextNode(tempStr));
                    em.appendChild(em4);
                    
                    //Pridane 19.2.2014 Detaily o trase
                    org.w3c.dom.Element em5 = document.createElement("Speed");
                    String tempSpeed = String.valueOf(trackSpeed.get(i));
                    tempSpeed = tempSpeed.substring(0, tempSpeed.lastIndexOf(".") + 2);
                    em5.appendChild(document.createTextNode(tempSpeed));
                    em.appendChild(em5);
                    //
                }
                TransformerFactory TF = TransformerFactory.newInstance();
                Transformer T = TF.newTransformer();
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(f);
                T.transform(source, result);
                
                DB1.reset();

            } catch (Exception ex) {
                System.out.println("Error: Cannot create *.tlv file!!!");
                ex.printStackTrace();
            }
        }
    }
    
    public void searchForMultimediaFiles(String searchFolder){
        TimezoneLoader gmt = new TimezoneLoader(getTrack());
        gmt.correctTimeZone();
        MultimediaSearcher searcher = new MultimediaSearcher(destFolder.getPath(), searchFolder, getTrack());
        files = searcher.startSearchWithBadFiles();
    }
    
    public void searchForMultimediaFilesWithoutCorrection(String searchFolder){
        MultimediaSearcher searcher = new MultimediaSearcher(destFolder.getPath(), searchFolder, getTrack());
        files = searcher.startSearchWithBadFiles();
    }
    
    public List getStartAndEndDate() {
        ArrayList<Date> times = new ArrayList<Date>();
        times.add(time.get(0));
        times.add(time.get(time.size()-1));
        return times;
    }

    /**
     * @return the startAddress
     */
    public String getStartAddress() {
                return startAddress;
    }

    /**
     * @return the endAddress
     */
    public String getEndAddress() {
        return endAddress;
    }

    /**
     * @return the trackDuration
     */
    public String getTrackDuration() {
        return trackDuration;
    }

    /**
     * @return the trackMaxElevation
     */
    public String getTrackMaxElevation() {
        return trackMaxElevation;
    }

    /**
     * @return the trackMinElevation
     */
    public String getTrackMinElevation() {
        return trackMinElevation;
    }

    /**
     * @return the trackLengthKm
     */
    public String getTrackLengthKm() {
        return trackLengthKm;
    }

    /**
     * @return the trackHeightDiff
     */
    public String getTrackHeightDiff() {
        return trackHeightDiff;
    }

    /**
     * @return the files
     */
    public ArrayList<FileImpl> getFiles() {
        return files;
    }

    /**
     * @return the track
     */
    public ArrayList<TrackPointImpl> getTrack() {
        return track;
    }

    /**
     * @param track the track to set
     */
    public void setTrack(ArrayList<TrackPointImpl> track) {
        this.track = track;
    }

    /**
     * @return the isDrawed
     */
    public boolean isDrawed() {
        return isDrawed;
    }
}
