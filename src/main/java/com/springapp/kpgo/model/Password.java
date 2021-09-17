/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.model;

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
    
    public Password(String username, String password) {
        try {
            MessageDigest digestObj = MessageDigest.getInstance("SHA-256");
            digestObj.update(username.getBytes("UTF-8"));
            this.digest = new String(Base64.getEncoder().encode(digestObj.digest(password.getBytes("UTF-8"))), "UTF-8");
        } catch (Exception err) {
            throw new Error(err);
        }
    }
    
    public boolean equals(Password password) {
        return password.digest.equals(this.digest);
    }
    
}
