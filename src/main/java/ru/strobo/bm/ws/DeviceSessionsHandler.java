/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.strobo.bm.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author Strobo
 */
@Component
public class DeviceSessionsHandler {
    private Map<String, WebSocketSession> sesssions = new HashMap<String, WebSocketSession>();
    
    private String kotelControllerId;
    
    public void addSession(WebSocketSession sess) {
        String deviceId = getHeaderValue(sess, "deviceid");
        sesssions.put(deviceId, sess);
        System.out.println("Add session for device: " + deviceId); 
    }
    
    public WebSocketSession getSession(String id) {
        return sesssions.get(id);
    }
    
    public String getHeaderValue(WebSocketSession sess, String header) {
        String dId = "";
        HttpHeaders hh = sess.getHandshakeHeaders();
        
        for(Map.Entry<String, List<String>> entry : hh.entrySet()) {      
            if(entry.getKey().equalsIgnoreCase(header)) {
                dId = entry.getValue().get(0);
            }
        }
        
        return dId;
    }

    public String getKotelControllerId() {
        return kotelControllerId;
    }

    public void setKotelControllerId(String kotelControllerId) {
        this.kotelControllerId = kotelControllerId;
    }
    
    
}
