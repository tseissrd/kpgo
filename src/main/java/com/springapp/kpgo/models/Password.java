/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.models;

import java.util.Base64;
import java.security.MessageDigest;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author Sovereign
 */

@Entity
public class Password {
    
    @Id
    public final String digest;
    
    public Password() {
        this.digest = "";
    }
    
    public Password(String password) {
        try {
            this.digest = new String(Base64.getEncoder().encode(MessageDigest.getInstance("SHA-256").digest(password.getBytes("UTF-8"))), "UTF-8");
        } catch (Exception err) {
            throw new Error(err);
        }
    }
    
    public boolean equals(Password password) {
        if (password.digest == this.digest)
            return true;
        else
            return false;
    }
    
}
