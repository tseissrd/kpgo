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
public class Stone
implements Serializable
{
  
  public final Colour colour;
  
  public Stone(Colour colour) {
    this.colour = colour;
  }
  
  @Override
  public String toString() {
    return colour.toString();
  }
  
}
