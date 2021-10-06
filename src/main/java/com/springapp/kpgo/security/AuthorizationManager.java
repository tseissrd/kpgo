/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.security;

import com.springapp.kpgo.repository.DataRepository;
import com.springapp.kpgo.model.User;
import com.springapp.kpgo.model.Password;
import com.springapp.kpgo.model.Session;
import java.util.Calendar;
import java.util.NoSuchElementException;
import javax.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Sovereign
 */
@Component
public class AuthorizationManager {
    
    @Autowired
    private DataRepository repository;
    
    private AuthorizationManager() {
      System.out.println("created new authorization mgr");
    }
    
    private User getUser(String username) {
      return repository.users().findByUsername(username);
    }
    
    public User authorize(String username, Password password) {
      User user = getUser(username);
      if ((user != null) && (user.getPassword().equals(password))) {
          System.out.println("authorization success");
          return user;
      } else
          return null;
    }
    
    public User authorize(String username, Session session) {
      User user = getUser(username);
      if ((user != null) && (user.checkSession(session))) {
          System.out.println("authorization success");
          return user;
      } else
          return null;
    }
    
    public User authorize(Cookie[] cookies) {
      if (cookies == null)
        return null;
      String username = null;
      Session session = null;
      if (cookies.length > 0) {
        for (Cookie cookie: cookies) {
          if (cookie.getName().equals("session")) {
            try {
              String[] sessionParts = cookie.getValue().split("#");
              if (sessionParts.length != 2)
                return null;
              username = sessionParts[0];
              session = repository.sessions().findById(sessionParts[1]).get();
              break;
            } catch (NoSuchElementException err) {
              return null;
            }
          }
        }
      }
      if ((username == null) || (session == null))
        return null;
      else {
        User user = authorize(username, session);
        if (user != null)
          refreshSession(session);
        return user;
      }
    }
    
    public Session getSession(User user) {
      Session session = user.newSession();
      session = repository.save(session);
      repository.save(user);
      return session;
    }
    
    public boolean refreshSession(Session session) {
      try {
        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.DATE, 1);
        session.expires = expires;
        repository.save(session);
        return true;
      } catch (Exception err) {
        System.err.println(err);
        return false;
      }
    }
    
    public boolean deleteSession(Session session) {
      return repository.delete(session);
    }
    
}
