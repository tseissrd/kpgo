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
public class Bowl {
  
  public class NoStonesLeftError extends Error {};
  
  public final Colour colour;
  private int stones;
  
  public Bowl(Colour colour) {
    this.colour = colour;
  }
  
  public int stonesLeft() {
    return stones;
  }
  
  public Stone getStone() {
    if (stonesLeft() > 0)
      return new Stone(colour);
    else
      throw new NoStonesLeftError();
  }
  
}
