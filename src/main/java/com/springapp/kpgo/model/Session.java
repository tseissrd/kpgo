/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.model;

import java.security.SecureRandom;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Base64;
import java.util.Calendar;

/**
 *
 * @author Anna
 */
@Entity
public class Session {
    
    @Id
    public final String id;
    
    public Calendar expires;
    
    private static final SecureRandom random = new SecureRandom();
    
    public Session() {
        byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
        try {
            this.id = new String(Base64.getEncoder().encode(randomBytes), "UTF-8");
        } catch (Exception err) {
            throw new Error(err);
        }
        expires = Calendar.getInstance();
    }
    
    public boolean equals(Session session) {
        return this.id.equals(session.id);
    }
    
    @Override
    public String toString() {
        return this.id;
    }
    
}
