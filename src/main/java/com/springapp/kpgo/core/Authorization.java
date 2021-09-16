/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.core;

import com.springapp.kpgo.repositories.UserRepository;
import com.springapp.kpgo.models.User;
import com.springapp.kpgo.models.Password;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Sovereign
 */
public class Authorization {
    
    private static Authorization singleton;
    
    @Autowired
    private UserRepository userRepository;
    
    private Authorization() {
        System.out.println("created new authorization mgr");
    }
    
    public static Authorization getMgr() {
        if (singleton == null)
            singleton = new Authorization();
        return singleton;
    }
    
    private User getUser(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User authorize(String username, Password password) {
        User user = getUser(username);
        System.out.println(user);
        System.out.println(password.digest);
        System.out.println(user.getPassword().digest);
        if ((user != null) && (user.getPassword().equals(password)))
            return user;
        else
            return null;
    }
    
}
