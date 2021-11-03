/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.model;

import java.io.Serializable;
import java.util.HashSet;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author Anna
 */
@Entity
public class Resource<T extends Serializable>
implements Serializable
{
    
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
  private final String name;
  
  @ManyToMany
  private final Set<User> allowedUsers;
  
  @OneToOne(targetEntity=Content.class)
  private Content<T> content;
  
  public Resource() {
    this.name = "";
    this.allowedUsers = new HashSet<>();
  }

  public Resource(String name) {
    this.name = name;
    this.allowedUsers = new HashSet<>();
  }

  public boolean equals(Resource resource) {
    return getId() == resource.getId();
  }
  
  public long getId() {
    return id;
  }

  @Override
  public String toString() {
    if ((name != null) && (name.length() > 0))
      return this.name;
    else
      return ((Long)getId()).toString();
  }
  
  public void giveAccess(User user) {
    allowedUsers.add(user);
  }
  
  public void denyAccess(User user) {
    if (allowedUsers.contains(user))
      allowedUsers.remove(user);
  }
  
  public boolean checkAccess(User user) {
    return allowedUsers.contains(user);
  }
  
  public void setContent(Content<T> content) {
    this.content = content;
  }
  
  public Content<T> getContent() {
    return content;
  }
    
}
