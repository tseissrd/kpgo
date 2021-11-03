/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.go;

import com.springapp.kpgo.model.User;
import java.util.Objects;

/**
 *
 * @author Anna
 */
public class HumanPlayer extends Player {
  
  private final User user;
  
  public HumanPlayer(User user) {
    this.user = user;
  }
  
  @Override
  public String getName() {
    return user.getUsername();
  }
  
  public User getUser() {
    return user;
  }
  
  @Override
  public boolean is(User user) {
    return this.user.equals(user);
  }
  
  @Override
  public boolean equals(Object object) {
    if (object.getClass() == HumanPlayer.class)
      return ((HumanPlayer)object).is(getUser());
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 79 * hash + Objects.hashCode(this.user);
    return hash;
  }
  
}
