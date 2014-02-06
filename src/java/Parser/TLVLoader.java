/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Parser;

import File.FileImpl;
import File.TrackPointImpl;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author matej_000
 */
public class TLVLoader {
    
    private ArrayList<TrackPointImpl> track = new ArrayList<TrackPointImpl>();
    private ArrayList<FileImpl> multimediaFiles = new ArrayList<FileImpl>();
    private boolean isFiles[];
    private String trackType;
    private String elevationsType;
    private String trackDescr;
    
    
    public void readTLVFile(String path, String file){
            track.clear();
            multimediaFiles.clear();

            try {
                File f = new File(path + file + ".tlv");
                DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
                DocumentBuilder DocB = DBF.newDocumentBuilder();
                org.w3c.dom.Document doc = DocB.parse(f);
                doc.getDocumentElement().normalize();
                NodeList list = doc.getElementsByTagName("TLV");
                Node gpxFileeee = list.item(0);
                Element elem = (Element) gpxFileeee;

                NodeList systemNodeList = elem.getElementsByTagName("SYSTEM");
                String system = systemNodeList.item(0).getTextContent();

                NodeList coordinatesNodeList = elem.getElementsByTagName("COORDINATES");
                Node coordinatesNode = coordinatesNodeList.item(0);
                Element coordinatesElement =(Element) coordinatesNode;

                NodeList trackTypeList = coordinatesElement.getElementsByTagName("Track_Type");
                Node trackTypeNode = trackTypeList.item(0);
                trackType = trackTypeNode.getTextContent();
                
                NodeList trackDescriptionList = coordinatesElement.getElementsByTagName("Track_Description");
                Node trackDescriptionNode = trackDescriptionList.item(0);
                trackDescr = trackDescriptionNode.getTextContent();

                NodeList elevationsTypeList = coordinatesElement.getElementsByTagName("Elevations_type");
                Node elevationsTypeNode = elevationsTypeList.item(0);
                elevationsType = elevationsTypeNode.getTextContent();

                NodeList trackPointNodeList = coordinatesElement.getElementsByTagName("TrackPoint");
                for(int i = 0; i < trackPointNodeList.getLength(); i++){
                    Node trackPointNode = trackPointNodeList.item(i);
                    Element trackPointElement = (Element) trackPointNode;
                    NodeList latitude = trackPointElement.getElementsByTagName("Latitude");
                    NodeList longitude = trackPointElement.getElementsByTagName("Longitude");
                    NodeList deviceElevation = trackPointElement.getElementsByTagName("Device_Elevation");
                    NodeList internetElevation = null;
                    if(elevationsType.equals("INTERNET")){
                        internetElevation = trackPointElement.getElementsByTagName("Internet_Elevation");
                    }
                    NodeList time = trackPointElement.getElementsByTagName("Time");

                    TrackPointImpl tempTP = new TrackPointImpl();
                    tempTP.setLatitude(Double.parseDouble(latitude.item(0).getTextContent()));
                    tempTP.setLongitude(Double.parseDouble(longitude.item(0).getTextContent()));
                    double tempD = Double.parseDouble(deviceElevation.item(0).getTextContent());
                    int tempInt = (int) tempD;
                    tempTP.setDeviceElevation(tempInt);

                    if(elevationsType.equals("INTERNET")){
                        double tempIE = Double.parseDouble(internetElevation.item(0).getTextContent());
                        int tempIntIE = (int) tempIE;
                        tempTP.setInternetElevation(tempIntIE);
                    }

                    if(!time.item(0).getTextContent().equalsIgnoreCase("null")){
                        tempTP.setTime(new Date(Long.parseLong(time.item(0).getTextContent())));
                    }
                    else{
                        tempTP.setTime(new Date(Long.parseLong("0")));
                    }
                    track.add(tempTP);
                }
                //gpxFile = new File(gpxNode.item(0).getTextContent());
                //System.out.println(gpxFile.getAbsolutePath());

                NodeList filesNodeList = elem.getElementsByTagName("FILES");
                Node filesNode = filesNodeList.item(0);
                Element filesElement = (Element) filesNode;
                NodeList fileEntityNode = filesElement.getElementsByTagName("File_entity");
                for(int i = 0; i < fileEntityNode.getLength(); i++){
                    FileImpl tempFile = new FileImpl();
                    Node fileNode = fileEntityNode.item(i);
                    Element fileElement = (Element) fileNode;
                    NodeList pathNode = fileElement.getElementsByTagName("path");
                    tempFile.setPath(pathNode.item(0).getTextContent());
                   
                    NodeList dateNode = fileElement.getElementsByTagName("creation_date");
                    Date tempDate = new Date(Long.parseLong(dateNode.item(0).getTextContent()));
                    tempFile.setDate(tempDate);
                    NodeList fileLatitudeNode = fileElement.getElementsByTagName("gps_latitude");
                    if(!fileLatitudeNode.item(0).getTextContent().equals("null")){
                        tempFile.setLatitude(fileLatitudeNode.item(0).getTextContent());
                    }
                    NodeList fileLongitudeNode = fileElement.getElementsByTagName("gps_longitude");
                    if(!fileLongitudeNode.item(0).getTextContent().equals("null")){
                        tempFile.setLongitude(fileLongitudeNode.item(0).getTextContent());
                    }
                    NodeList fileElevationNode = fileElement.getElementsByTagName("gps_elevation");
                    if(!fileElevationNode.item(0).getTextContent().equals("null")){
                        tempFile.setElevation(fileElevationNode.item(0).getTextContent());
                    }
                    multimediaFiles.add(tempFile);
                }

                    isFiles = new boolean[track.size()];
                    for(int i = 0 ; i < track.size() ; i++){
                        isFiles[i] = false;
                    }
                    for (int i = 0; i < multimediaFiles.size(); i++){
                        //System.out.println("File number " + i);
                        //System.out.println("Path: " + multimediaFiles.get(i).getPath());
                        //System.out.println("Date: " + multimediaFiles.get(i).getDate());
                        //System.out.println("GPS: " + multimediaFiles.get(i).getLatitude() + " "+ multimediaFiles.get(i).getLongitude() + " " + multimediaFiles.get(i).getElevation());
                        //System.out.println();
                        Date fileDate = multimediaFiles.get(i).getDate();
                        for(int j = 1; j < track.size(); j++){
                            Date prevTrackPointDate = track.get(j-1).getTime();
                            prevTrackPointDate.setSeconds(track.get(j-1).getTime().getSeconds()-1);
                            Date nextTrackPointDate = track.get(j).getTime();
                            nextTrackPointDate.setSeconds(track.get(j).getTime().getSeconds()+1);
                            if (multimediaFiles.get(i).getLongitude() != null && multimediaFiles.get(i).getLatitude() != null) {
                                if ((fileDate.after(prevTrackPointDate) && fileDate.before(nextTrackPointDate)) || (fileDate.equals(prevTrackPointDate) || (fileDate.equals(nextTrackPointDate)))) {
                                    double deltaLat1 = Math.abs(Double.parseDouble(multimediaFiles.get(i).getLatitude()) - track.get(j - 1).getLatitude());
                                    double deltaLon1 = Math.abs(Double.parseDouble(multimediaFiles.get(i).getLongitude()) - track.get(j - 1).getLongitude());
                                    double deltaLat2 = Math.abs(Double.parseDouble(multimediaFiles.get(i).getLatitude()) - track.get(j).getLatitude());
                                    double deltaLon2 = Math.abs(Double.parseDouble(multimediaFiles.get(i).getLongitude()) - track.get(j).getLongitude());
                                    
                                    if ((deltaLat1 <= 0.0009 && deltaLon1 <= 0.0009) || (deltaLat2 <= 0.0009 && deltaLon2 <= 0.0009)) {
                                        //System.out.println(i + ". Obrazok ma dobru GPS, k bodu " + (j - 1) + "!!!");
                                         multimediaFiles.get(i).setTrackPointIndex(j - 1);
                                         isFiles[j - 1] = true;
                                         break;
                                    }
                                }
                            } else {
                                // nechat toto tu prosim // || (fileDate.equals(prevTrackPointDate)) || (fileDate.equals(nextTrackPointDate))
                                if ((fileDate.after(prevTrackPointDate) && fileDate.before(nextTrackPointDate))) {
                                    //System.out.println(i + ". " + (j - 1));
                                    multimediaFiles.get(i).setTrackPointIndex(j - 1);
                                    isFiles[j - 1] = true;
                                    break;
                                }
                            }
                        }
                    }
            } catch (ParserConfigurationException ex) {
                
            } catch (SAXException ex) {
                
            } catch (IOException ex) {
                
            }
    }
     /**
     * @return the track
     */
    public ArrayList<TrackPointImpl> getTrackPoints() {
        return track;
    }

    /**
     * @return the multimediaFiles
     */
    public ArrayList<FileImpl> getMultimediaFiles() {
        return multimediaFiles;
    }

    /**
     * @return the isFiles
     */
    public boolean[] getIsFiles() {
        return isFiles;
    }

    /**
     * @return the trackType
     */
    public String getTrackType() {
        return trackType;
    }

    /**
     * @return the elevationsType
     */
    public String getElevationsType() {
        return elevationsType;
    }
    
    /**
     * @return the elevationsType
     */
    public String getTrackDescription() {
        return trackDescr;
    }
}
