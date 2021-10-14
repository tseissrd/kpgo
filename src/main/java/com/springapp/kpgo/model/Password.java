/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.model;

import java.io.Serializable;
import java.util.Base64;
import java.security.MessageDigest;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Sovereign
 */

@Entity
public class Password
implements Serializable
{
    
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private final String digest;

  protected Password() {
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

  public String getDigest() {
    return digest;
  }

  public boolean equals(Password password) {
      return password.digest.equals(this.getDigest());
  }

}
