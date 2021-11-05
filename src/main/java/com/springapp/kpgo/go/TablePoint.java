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
public class TablePoint
implements Serializable
{
  
  public class PointVacantException extends Exception {};
  public class PointOccupiedException extends Exception {};
  
  private Stone stone;
  public final int x;
  public final int y;
  
  public TablePoint(int x, int y) {
    this.x = x;
    this.y = y;
    this.stone = null;
  }
  
  public Stone getStone() {
    return stone;
  }
  
  public void putStone(Stone stone)
  throws PointOccupiedException
  {
    if (this.stone == null)
      this.stone = stone;
    else
      throw new PointOccupiedException();
  }
  
  public void removeStone()
  throws PointVacantException
  {
    if (this.stone != null)
      this.stone = null;
    else
      throw new PointVacantException();
  }
  
}
