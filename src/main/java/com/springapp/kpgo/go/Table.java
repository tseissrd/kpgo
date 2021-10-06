/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.go;

import java.util.ArrayList;

/**
 *
 * @author Sovereign
 */
public class Table {
  
  public class SizeExceededError extends Error {};
  
  private ArrayList<ArrayList<TablePoint>> points;
  public final int width;
  public final int height;
  
  public Table(int width, int height) {
    this.width = width;
    this.height = height;
    points = new ArrayList<>(width);
    points.parallelStream()
      .forEach(column -> {
        column = new ArrayList<>(height);
        for (TablePoint point: column) {
          point = new TablePoint();
        }
      });
  }
  
  public TablePoint getPoint(int x, int y) {
    if ((x > width) || (y > height))
      throw new SizeExceededError();
    
    return points.get(x - 1).get(y - 1);
  }
  
}
