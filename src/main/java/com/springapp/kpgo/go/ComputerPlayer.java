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
public class ComputerPlayer extends Player {
  
  @Override
  public String getName() {
    return "Computer player";
  }
  
  @Override
  public boolean is(User user) {
    return false;
  }
  
}
