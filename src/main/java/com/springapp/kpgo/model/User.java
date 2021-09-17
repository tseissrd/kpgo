/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.model;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

import com.springapp.kpgo.model.Password;
import com.springapp.kpgo.model.Session;

/**
 *
 * @author Sovereign
 */
@Entity
public class User {
    
    public User() {
    }
    
    public User(String name, Password password) {
        this.username = name;
        this.password = password;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotEmpty
    private String username;
    
    @OneToOne
    private Password password;
    
    @OneToMany
    private Set<Session> sessions;
    
    @Override
    public String toString() {
        return this.username + ":***";
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public Password getPassword() {
        return this.password;
    }
    
    public Session newSession() {
        Session session = new Session();
        this.sessions.add(session);
        return session;
    }
    
    public boolean checkSession(Session session) {
        return this.sessions.contains(session);
    }
    
}
