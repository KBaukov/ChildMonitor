/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.strobo.bm.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ru.strobo.bm.dao.UserDao;

/**
 *
 * @author k.baukov
 */
@Component
public class CheckSessionInterseptor extends HandlerInterceptorAdapter {
    
    @Autowired
    UserDao uDao;
    
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(CheckSessionInterseptor.class);

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception)
    throws Exception {
    // TODO Auto-generated method stub

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
    throws Exception {
    // TODO Auto-generated method stub

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        HttpSession sess =  request.getSession();
        String sessId = (String) sess.getAttribute("SessID");
        
        Logger.debug("Check session: sessionID="+sessId);
        
        if(uDao.checkSession(sessId)) {
            return true;
        } else {
            sess.removeAttribute("Auth");
            sess.removeAttribute("User");
            sess.removeAttribute("SessID");
            response.setStatus(401);
            return false;
        }
    }


}