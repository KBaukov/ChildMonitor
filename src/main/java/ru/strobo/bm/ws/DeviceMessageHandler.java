/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.strobo.bm.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.strobo.bm.dao.DeviceDao;
import ru.strobo.bm.dao.TrackDao;
import ru.strobo.bm.data.Point;
import ru.strobo.bm.data.TrackData;


/**
 *
 * @author k.baukov
 */
@Component
public class DeviceMessageHandler extends TextWebSocketHandler {
    
    @Autowired
    DeviceSessionsHandler sh;
    
    @Autowired
    DeviceDao dDao;
    
    @Autowired
    TrackDao tDao;
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DeviceMessageHandler.class);
    
    private String message ="";
 
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
         Logger.info("Connection closed: " + status.getReason());
    }
 
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        
        try {
        
        Logger.info("sessionId:" + session.getId().toString());
        
        sh.addSession(session);
//        Map<String, Object> attrs = session.getAttributes();
//        
//        for(Map.Entry<String, Object> entry : attrs.entrySet()) {
//            System.out.println(entry.getKey() + ":" + entry.getValue().toString());
//        }
        
        Logger.info("Connection estabished: " + session.isOpen());
 
        session.sendMessage(new TextMessage("{action:connect, success:true}"));
        
        } catch(Exception ex) {
            Logger.error("Error: " + ex.getMessage(), ex);
        }
    }
 
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        
        String dId = sh.getHeaderValue(session, "deviceid");
        message += textMessage.getPayload(); 
        
        ObjectMapper objectMapper = new ObjectMapper();
        
        Logger.info("Message received from " + dId +":\r\n" + message );
        
        if(message.contains("\"action\": \"connect\"")) {
            System.out.println("##################");
        } else if(message.contains("\"type\":\"trackdata\"")){ // { "type":"trackdata", "device_id":"SoniaPhone", data: [ {"timestamp": "1323123144", "point": "[55.35545,55.554675]"}, {"timestamp": "1323123144", "point": "[55.35545,55.554675]"} ] }
            System.out.println("##################");
            try {
                
                ObjectNode object = objectMapper.readValue(message, ObjectNode.class);
                JsonNode did = object.get("deviceid");
                String devId = did.textValue();
                JsonNode pts = object.get("points");
                Iterator<JsonNode> pp = pts.iterator();
                List<Point> pl = new ArrayList<Point>();
                
                while(pp.hasNext()) {
                    JsonNode p = pp.next(); 
                    String dd = p.get("timestamp").asText();
                    String pt = p.get("point").asText();
                    Point point = new Point( dateFormat.parse(dd), pt);
                    pl.add(point);
                }
                
                TrackData data = new TrackData("trackdata", devId, pl);
                
                tDao.setTrackPointByDevId(data);

                Logger.info("TrackData added into data base: " + data.toJson() );
            } catch(Exception ex) {
                System.out.println("########### Error !!!:##############/n/r"+ex.getMessage());
                ex.printStackTrace();
            }
            
        } else {
            
        }
            
        message = "";
        
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
         Logger.error("error occured at sender " + session + ";  reason: " + throwable.getMessage());
    }

}
