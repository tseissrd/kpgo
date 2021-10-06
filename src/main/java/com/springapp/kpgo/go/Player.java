/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.go;

/**
 *
 * @author Sovereign
 */
public abstract class Player {
  
  protected Bowl bowl;
  public boolean passed;
  
  abstract public String getName();
  
  public boolean takeTurn(TablePoint point) {
    if (point.getStone() == null) {
      if (bowl.stonesLeft() > 0) {
        point.putStone(bowl.getStone());
        return true;
      }
    }
    
    return false;
  }
  
  public Player() {}
  
  public void assignBowl(Bowl bowl) {
    this.bowl = bowl;
  }
  
  public void pass() {
    passed = true;
  }
  
}
