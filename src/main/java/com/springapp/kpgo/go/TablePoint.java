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
public class TablePoint {
  
  public class PointOccupiedError extends Error {};
  
  private Stone stone;
  
  public TablePoint() {
    stone = null;
  }
  
  public Stone getStone() {
    return stone;
  }
  
  public void putStone(Stone stone) {
    if (this.stone == null)
      this.stone = stone;
    else
      throw new PointOccupiedError();
  }
  
}
