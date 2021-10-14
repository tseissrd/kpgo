/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.model;

import java.io.Serializable;
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
public class Session
implements Serializable
{

  @Id
  private final String id;

  private Calendar expires;

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
  
  public String getId() {
    return id;
  }
  
  public Calendar getExpiration() {
    return expires;
  }
  
  public void setExpiration(Calendar expiration) {
    this.expires = expiration;
  }

  public boolean equals(Session session) {
      return this.id.equals(session.getId());
  }

  @Override
  public String toString() {
      return this.id;
  }

}
