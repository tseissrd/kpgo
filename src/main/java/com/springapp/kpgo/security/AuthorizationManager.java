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
    
    public Session getSession(User user) {
        Session session = user.newSession();
        session = repository.save(session);
        repository.save(user);
        return session;
    }
    
}
