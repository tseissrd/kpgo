/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.go;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Sovereign
 */
public class Table
implements Serializable
{
  
  public class SizeExceededError extends Error {};
  
  private ArrayList<ArrayList<TablePoint>> points;
  public final int width;
  public final int height;
  
  public Table(int width, int height) {
    this.width = width;
    this.height = height;
    points = new ArrayList<>(width);
    for (int columnNum = 0; columnNum < width; columnNum += 1) {
      points.add(new ArrayList<>(height));
      ArrayList<TablePoint> column = points.get(columnNum);
      for (int rowNum = 0; rowNum < height; rowNum += 1)
        column.add(new TablePoint(columnNum, rowNum));
    }
  }
  
  public TablePoint getPoint(int x, int y) {
    if ((x > width) || (y > height))
      throw new SizeExceededError();
    
    return points.get(x).get(y);
  }
  
  public Set<TablePoint> getPoints() {
    Set<TablePoint> allPoints = new HashSet<>();
    
    for (int x = 0; x < width; x += 1) {
      for (int y = 0; y < height; y += 1) {
        allPoints.add(getPoint(x, y));
      }
    }
    
    return allPoints;
  }
  
  public List<List<String>> jsonView() {
    List<List<String>> view = new ArrayList<>(height);
    
    for (int rowNum = 0; rowNum < height; rowNum += 1) {
      view.add(new ArrayList<>(width));
      List<String> row = view.get(rowNum);
      
      for (int columnNum = 0; columnNum < width; columnNum += 1) {
        Stone stone = getPoint(columnNum, rowNum).getStone();
        if (stone != null)
          row.add(stone.toString());
        else
          row.add("EMPTY");
      }
    }
    return view;
  }
  
}
