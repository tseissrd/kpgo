/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.go;

import com.springapp.kpgo.model.User;

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
  
}
