/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.go;

import com.springapp.kpgo.model.User;
import java.io.Serializable;

/**
 *
 * @author Sovereign
 */
public abstract class Player
implements Serializable
{
  
  protected Bowl bowl;
  public boolean passed;
  
  abstract public String getName();
  abstract public boolean is(User user);
  
  public void takeTurn(TablePoint point) {
    if (point.getStone() == null) {
      if (bowl.stonesLeft() > 0) {
        try {
          point.putStone(bowl.getStone());
        } catch (Bowl.NoStonesLeftException ex) {
          throw new Error(ex);
        } catch (TablePoint.PointOccupiedException ex) {
          throw new Error(ex);
        }
      }
    }
  }
  
  public Player() {}
  
  public void assignBowl(Bowl bowl) {
    this.bowl = bowl;
  }
  
  public Bowl getBowl() {
    return bowl;
  }
  
  public void pass() {
    passed = true;
  }
  
}
