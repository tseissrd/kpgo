/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.go;

import java.io.Serializable;

/**
 *
 * @author Sovereign
 */
public class Bowl
implements Serializable
{
  
  public class NoStonesLeftException extends Exception {};
  
  public final Colour colour;
  private int stones;
  
  public Bowl(Colour colour) {
    this.colour = colour;
    if (this.colour.equals(Colour.BLACK))
      this.stones = 181;
    else
      this.stones = 180;
  }
  
  public int stonesLeft() {
    return stones;
  }
  
  public Stone getStone()
  throws NoStonesLeftException  
  {
    if (stonesLeft() > 0)
      return new Stone(colour);
    else {
      this.stones += 10;
      return getStone();
      // throw new NoStonesLeftException();
    }
  }
  
}
