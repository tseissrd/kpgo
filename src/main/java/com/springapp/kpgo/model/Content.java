/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Lob;

/**
 *
 * @author Anna
 */
@Entity
public class Content<T extends Serializable>
implements Serializable
{
    
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
  @Lob
  private Serializable content;
  
  public Content() {
    this.content = "";
  }

  public Content(T content) {
    this.content = content;
  }

  public boolean equals(Content content) {
    return getId() == content.getId();
  }
  
  public long getId() {
    return id;
  }
  
  public T read() {
    return (T)content;
  }
  
  public void write(T content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return ((Long)getId()).toString();
  }
    
}
