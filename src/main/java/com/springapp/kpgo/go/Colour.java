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
public enum Colour
implements Serializable
{
  
  BLACK,
  WHITE;
  
  public String toString() {
    return this.name();
  }
  
}
